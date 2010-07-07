/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Random;

public class Transaction implements Runnable, AppConstants {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);
    private static final Random randomIdGenerator = new Random();

    private final int transferAmount;
    private final AccountManager accountMgr;
    private final String name;
    private boolean verbose = false;

    public Transaction(final int transferAmount, final AccountManager accountMgr, final int id) {
        this.transferAmount = transferAmount;
        this.accountMgr = accountMgr;
        this.name = "t" + id;

        final String verboseString = System.getProperty(VERBOSE);
        if (verboseString != null && verboseString.equals(VERBOSE_ON)) {
            verbose = true;
        }
    }

    @Override
    public void run() {
        while (true) {
            
            final int sourceId = getRandomAccountId();
            final int destinationId = getRandomAccountId();

            if (sourceId == destinationId) {
                continue;
            }

            // SpinLock
            // But essentially we want a monitor make this an event based thingy
            // So threads don't starve
            try {

                while (!accountMgr.aquire(sourceId, destinationId)) {
                    final String waiting = String.format("%s is waiting (%d, %d)", name, destinationId, sourceId);

                    if (verbose) {
                        System.out.println(waiting);
                    }
                    logger.debug(waiting);
                }
            }
            catch (final ZeroBalanceException e) {
                    final String zeroBalance = String.format("%s is giving up", name);

                if (verbose) {
                    System.out.println(zeroBalance);
                }
                logger.debug(zeroBalance);
                return;
            }


            final String transacting =
                    String.format("%s is transfering %d ----------> %d", name, sourceId, destinationId);

            if (verbose) {
                System.out.println(transacting);
            }
            logger.debug(transacting);

            try {
                accountMgr.transact(sourceId, destinationId, transferAmount);
            }
            catch (final ZeroBalanceException e) {
                final String zeroBalance = String.format("%s has found zero account balance!", name);

                if (verbose) {
                    System.out.println(zeroBalance);
                }
                logger.debug(zeroBalance);

                return;
            }
            finally {
                accountMgr.release(sourceId, destinationId);
            }
        }
    }

    @Override
    public String toString() {
        return "#<Transaction: "+ hashCode() + " " + "transferAmount: " + transferAmount +">";
    }

    private int getRandomAccountId() {
        return randomIdGenerator.nextInt(accountMgr.getNumberOfAccount());
    }
}
