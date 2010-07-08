/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccountManager implements AppConstants {
    private static final Logger logger = LoggerFactory.getLogger(AccountManager.class);

    private enum State {
        WAITING, TRANSACTING
    }

    private boolean verbose = false;

    private final List<Account> accounts;

    private final ReentrantLock lock = new ReentrantLock();

    private final List<Condition> conditions = new ArrayList<Condition>();
    private final List<State> states = new ArrayList<State>();
    
    private final Map<Account, AtomicBoolean> locks = new ConcurrentHashMap<Account, AtomicBoolean>();

    private final Map<Account, List<AccountLockRequest>> requestMap =
            new ConcurrentHashMap<Account, List<AccountLockRequest>>();

    public AccountManager(final List<Account> accounts, final int threadCount) {
        this.accounts = accounts;

        for (final Account account : accounts) {
            requestMap.put(account, new ArrayList<AccountLockRequest>());
            locks.put(account, new AtomicBoolean(false));
        }

        for (int x = 0; x < threadCount; ++x) {
            conditions.add(lock.newCondition());
            states.add(State.WAITING);
        }

        final String verboseString = System.getProperty(VERBOSE);
        if (verboseString != null && verboseString.equals(VERBOSE_ON)) {
            verbose = true;
        }
    }

    public void test(final Account source, final Account destination, final int id) {

        final boolean sourceAquire = locks.get(source).compareAndSet(false, true);
        final boolean destinationAquire = locks.get(destination).compareAndSet(false, true);

        if (sourceAquire && destinationAquire) {
            if (lock.hasWaiters(conditions.get(id))) {
                conditions.get(id).signal();
            }
            states.set(id, State.TRANSACTING);
            return;
        }

        final AccountLockRequest request = new AccountLockRequest(source, destination, id);

        if (sourceAquire) {
            locks.get(source).set(false);
            requestMap.get(destination).add(request);
        }

        if (destinationAquire) {
            locks.get(destination).set(false);
            requestMap.get(source).add(request);
        }
    }

    public void aquire(final int sourceId, final int destinationId, final int id) throws InterruptedException {
        lock.lock();
        try {
            final Account source = accounts.get(sourceId);
            final Account destination = accounts.get(destinationId);

            test(source, destination, id);

            if (!states.get(id).equals(State.TRANSACTING)) {
                final String waiting = String.format("%s is waiting source: %d, dest: %d)", "t" + id, sourceId, destinationId);

                if (verbose) {
                    System.out.println(waiting);
                }
                logger.debug(waiting);

                try {
                    conditions.get(id).await();
                }
                catch (InterruptedException e) {
                    logger.debug("{} has been interrupted", id);
                    if (!states.get(id).equals(State.TRANSACTING))
                        throw e;
                }
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void release(final int sourceId, final int destinationId, final int id) {
        lock.lock();
        try {

            states.set(id, State.WAITING);

            final Account source = accounts.get(sourceId);
            final Account destination = accounts.get(destinationId);

            locks.get(source).set(false);
            locks.get(destination).set(false);

            honorRequested(source, destination);

        }
        finally {
            lock.unlock();
        }
    }

    private void honorRequested(final Account source, final Account destination) {
        final HashSet<AccountLockRequest> reqs = new HashSet<AccountLockRequest>();

            for (final AccountLockRequest req : requestMap.remove(source)) {
                reqs.add(req);
            }

            for (final AccountLockRequest req : requestMap.remove(destination)) {
                reqs.add(req);
            }

            requestMap.put(source, new ArrayList<AccountLockRequest>());
            requestMap.put(destination, new ArrayList<AccountLockRequest>());

            for (final AccountLockRequest req : reqs) {
                logger.debug("during release of account: {} testing thread: {}, source: {}, dest: {}",
                        new Object[]{source.getId(), "t" + req.getId(), req.getSource().getId(), req.getDestination().getId()});
                test(req.getSource(), req.getDestination(), req.getId());
            }
    }

    public void transact(final int sourceId, final int destinationId, final int transferAmount)
            throws ZeroBalanceException {
        final Account sourceAccount = accounts.get(sourceId);
        final Account destinationAccount = accounts.get(destinationId);

        sourceAccount.withdrawl(transferAmount);
        destinationAccount.deposit(transferAmount);

        hasZeroBalance(sourceId);
    }

    private void hasZeroBalance(final int sourceId) {
        if (accounts.get(sourceId).getBalance() == 0) {
            logger.debug("zomg zero balance found!");
            throw new ZeroBalanceException();
        }
    }

    public void print() {
        System.out.println("Account Id    |   Balance");
        System.out.println("-------------------------");

        for (int x = 0; x < accounts.size(); ++x) {
            System.out.println(String.format("%10d    |   %7d", x, accounts.get(x).getBalance()));
        }
    }

    public int getNumberOfAccount() {
        return accounts.size();
    }

    private static class AccountLockRequest {
        private final Account source;
        private final Account destination;
        private final int id;

        public AccountLockRequest(final Account source, final Account destination, final int id) {
            this.source = source;
            this.destination = destination;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Account getSource() {
            return source;
        }

        public Account getDestination() {
            return destination;
        }
    }
}
