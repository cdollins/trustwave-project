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
    private final AccountMonitor accountMgr;
    private final int id;
    private final String name;
    private boolean verbose = false;

    private Account source;
    private Account destination;

    public Transaction(final int transferAmount, final AccountMonitor accountMgr, final int id) {
        this.transferAmount = transferAmount;
        this.accountMgr = accountMgr;
        this.id = id;
        this.name = "t" + id;

        final String verboseString = System.getProperty(VERBOSE);
        if (verboseString != null && verboseString.equals(VERBOSE_ON)) {
            verbose = true;
        }
    }

    public void run() {
        while (true) {
            
            source = accountMgr.findById(getRandomAccountId());
            destination = accountMgr.findById(getRandomAccountId());

            if (source == destination) {
                continue;
            }

            accountMgr.aquire(source, destination, id);

            try {
                final String before =
                        String.format("%s is transfering %d to %d: (%d, %d) ----------> ", name, source.getId(),
                                destination.getId(), source.getBalance(), destination.getBalance());

                accountMgr.transact(source, destination, transferAmount);
                final String after =
                        String.format("%s(%d, %d)", before, source.getBalance(), destination.getBalance());

                if (verbose) {
                    System.out.println(after);
                }

                logger.debug(after);
            }
            catch (final ZeroBalanceException e) {
                logger.debug("{} exiting stage left", "t" + id);
                return;
            }
            finally {
                accountMgr.release(source, destination, id);
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
