/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import java.util.Random;

public class Transaction implements Runnable{
    private static Random randomIdGenerator = new Random();

    private final int transferAmount;
    private final AccountManager accountMgr;

    public Transaction(final int transferAmount, final AccountManager accountMgr) {
        this.transferAmount = transferAmount;
        this.accountMgr = accountMgr;
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
                if (sourceAccount != null) {
                    sourceAccount.releaseLock();
                }

                if (destinationAccount != null) {
                    destinationAccount.releaseLock();
                }
                
                continue;
            }

            sourceAccount.withdrawl(transferAmount);
            destinationAccount.deposit(transferAmount);

            sourceAccount.releaseLock();
            destinationAccount.releaseLock();

            if (sourceAccount.getBalance() == 0) {
                return;
            }
        }
    }

    private int getRandomAccountId() {
        return randomIdGenerator.nextInt(accountMgr.getNumberOfAccount());
    }
}
