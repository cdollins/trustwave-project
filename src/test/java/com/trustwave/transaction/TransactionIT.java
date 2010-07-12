/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Queue;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@Test
public class TransactionIT implements AppConstants {

    private static final Random randomIdGenerator = new Random();

    @Test(enabled=false)
    public void continuesTesting() {
        for(;;) {
            emptyRequstMap();
            closedTransactionSystem();
        }
    }

    @Test
    public void emptyRequstMap() {
        for (int x = 0; x < 10; ++x) {
            final AccountMonitorTest accountMgr = runSimulator();

            assertTrue(accountMgr.isRequestMapEmpty());
        }
    }

    @Test
    public void closedTransactionSystem() {
        for (int x = 0; x < 10; ++x) {
            final AccountMonitorTest accountMgr = runSimulator();

            assertEquals(accountMgr.getNumberOfAccount() * DEFAULT_BALANCE, accountMgr.getTotalBalanceOfAccounts());
        }
    }

    private AccountMonitorTest runSimulator() {
        final int accountCount = getAccountCount();

        final int transferAmount = getTransferAmount();

        final int threadCount = getThreadCount();

        final AccountMonitorTest accountMgr = new AccountMonitorTest(createAccounts(accountCount), threadCount);

        final TransactionManager transactionMgr = new TransactionManager(accountMgr);

        transactionMgr.simulate(threadCount, transferAmount);
        
        return accountMgr;
    }

    private int getThreadCount() {
        return randomIdGenerator.nextInt(10) + 1;
    }

    private int getTransferAmount() {
        return (int) Math.pow(10, (randomIdGenerator.nextInt(3) + 1));
    }

    private int getAccountCount() {
        return 20 * (randomIdGenerator.nextInt(24) + 2);
    }

    private static List<Account> createAccounts(final int size) {
        final List<Account> accounts = new ArrayList<Account>();
        for (int x = 0; x < size; ++x) {
            accounts.add(new Account(x, DEFAULT_BALANCE));
        }
        return accounts;
    }

    class AccountMonitorTest extends AccountMonitor {

        public AccountMonitorTest(final List<Account> accounts, final int threadCount) {
            super(accounts, threadCount);
        }

        public boolean isRequestMapEmpty() {
            for (final Queue<AccountLockRequest> requests : requestMap.values()) {
                if (!requests.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        public int getTotalBalanceOfAccounts() {
            int sum = 0;
            for (final Account account : accounts) {
                sum += account.getBalance();
            }
            return sum;
        }
    }
}