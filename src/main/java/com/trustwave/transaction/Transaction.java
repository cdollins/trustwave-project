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
    private boolean verbose = false;

    public Transaction(final int transferAmount, final AccountManager accountMgr) {
        this.transferAmount = transferAmount;
        this.accountMgr = accountMgr;
        
        final String verboseString = System.getProperty(VERBOSE);
        if (verboseString != null && verboseString.equals(VERBOSE_ON)) {
            verbose = true;
        }
    }

    public void run() {
        while(true) {
            
            final int sourceId = getRandomAccountId();
            final int destinationId = getRandomAccountId();

            if (sourceId == destinationId) {
                continue;
            }

            final Account sourceAccount = accountMgr.retrieve(sourceId);
            final Account destinationAccount = accountMgr.retrieve(destinationId);

            if (sourceAccount == null || destinationAccount == null) {
                final String waiting = String.format("waiting");

                if (verbose) {
                    System.out.println(waiting);
                }
                logger.debug(waiting);

                if (sourceAccount != null) {
                    sourceAccount.releaseLock();
                }

                if (destinationAccount != null) {
                    destinationAccount.releaseLock();
                }
                
                continue;
            }

            final String transacting = String.format("transfering %d ----------> %d", sourceId, destinationId);

            if (verbose) {
                System.out.println(transacting);
            }
            logger.debug(transacting);

            sourceAccount.withdrawl(transferAmount);
            destinationAccount.deposit(transferAmount);


            sourceAccount.releaseLock();
            destinationAccount.releaseLock();

            if (sourceAccount.getBalance() == 0) {
                final String zeroBalance = String.format("zero account balance!");

                if (verbose) {
                    System.out.println(zeroBalance);
                }
                logger.debug(zeroBalance);
                return;
            }
        }
    }

    private int getRandomAccountId() {
        return randomIdGenerator.nextInt(accountMgr.getNumberOfAccount());
    }
}
