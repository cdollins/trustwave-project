/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    
    private final AccountManager accountMgr;

    public TransactionManager(final AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

    public void simulate(final int threadCount, final int transferAmount) {

        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int x = 0; x < threadCount; ++x) {
            executor.execute(new Transaction(transferAmount, accountMgr, x));
        }

        executor.shutdown();

        while(!executor.isTerminated()) {}

        accountMgr.print();
    }
}
