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
    private final int id;
    private final String name;
    private boolean verbose = false;

    private int sourceId;
    private int destinationId;

    public Transaction(final int transferAmount, final AccountManager accountMgr, final int id) {
        this.transferAmount = transferAmount;
        this.accountMgr = accountMgr;
        this.id = id;
        this.name = "t" + id;

        final String verboseString = System.getProperty(VERBOSE);
        if (verboseString != null && verboseString.equals(VERBOSE_ON)) {
            verbose = true;
        }
    }

    @Override
    public void run() {
        while (true) {
            
            sourceId = getRandomAccountId();
            destinationId = getRandomAccountId();

            if (sourceId == destinationId) {
                continue;
            }

            accountMgr.aquire(sourceId, destinationId, id);
            
            try {
                accountMgr.transact(sourceId, destinationId, transferAmount);
                final String transacting =
                        String.format("%s is transfering %d ----------> %d", name, sourceId, destinationId);

                if (verbose) {
                    System.out.println(transacting);
                }

                logger.debug(transacting);
            }
            catch (ZeroBalanceException e) {
                return;
            }
            finally {
                accountMgr.release(sourceId, destinationId, id);
            }
        }
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getDestinationId() {
        return this.destinationId;
    }

    @Override
    public String toString() {
        return "#<Transaction: "+ hashCode() + " " + "transferAmount: " + transferAmount +">";
    }

    private int getRandomAccountId() {
        return randomIdGenerator.nextInt(accountMgr.getNumberOfAccount());
    }
}
