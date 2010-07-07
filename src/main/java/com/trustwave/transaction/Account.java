/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private int balance;

    private final ReentrantLock lock = new ReentrantLock();

    public Account(final int balance) {
        this.balance = balance;
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
        return lock.tryLock();
    }

    public void releaseLock() {
        lock.unlock();
    }
}
