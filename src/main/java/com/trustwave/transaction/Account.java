/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import java.util.concurrent.Semaphore;

public class Account {
    private int balance;

    private Semaphore lock;

    public Account(final int balance) {
        this.balance = balance;
        this.lock = new Semaphore(1);
    }

    public void withdrawl(final int amount) {
        balance -= amount;
    }

    public void deposit(final int amount){
        balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public boolean getLock() {
        return lock.tryAcquire();
    }

    public void releaseLock() {
        lock.release();
    }
}
