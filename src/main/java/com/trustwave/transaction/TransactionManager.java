/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import java.util.List;
import java.util.ArrayList;

public class TransactionManager {
    private final AccountManager accountMgr;

    public TransactionManager(final AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

    public void simulate(final int threadCount, final int transferAmount) {
        final List<Thread> pool = new ArrayList<Thread>();

        for (int x = 0; x < threadCount; ++x) {
            final Thread thread = new Thread(new Transaction(transferAmount, accountMgr));
            thread.setName("t" + x);
            pool.add(thread);
            thread.start();
        }

        for (int x = 0; x < pool.size(); ++x) {
            pool.get(x);
        }

        try {
            pool.get(0).join();
        }
        catch (InterruptedException e) {
            System.out.println("BOOM!");
        }

        accountMgr.print();
    }
}
