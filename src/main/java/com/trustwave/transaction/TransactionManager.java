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
import java.util.concurrent.Semaphore;

public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    
    private final AccountManager accountMgr;

    public TransactionManager(final AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

    public void simulate(final int threadCount, final int transferAmount) {
        final List<Thread> pool = new ArrayList<Thread>();
        
        try {
            for (int x = 0; x < threadCount; ++x) {
                final Thread thread = new Thread(new Transaction(transferAmount, accountMgr, x));
                pool.add(thread);
                thread.start();
            }
        }
        catch(final ZeroBalanceThreadException e) {
            try {
                pool.get(e.getThreadId()).join();
            }
            catch (InterruptedException e1) {
                logger.error("Boom!, {}", e1);
            }
        }

        accountMgr.print();
    }
}
