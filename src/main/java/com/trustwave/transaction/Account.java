package com.trustwave.transaction;

import java.util.concurrent.atomic.AtomicBoolean;

public class Account {
    private final int id;
    private int balance;

    private final AtomicBoolean lock = new AtomicBoolean(false);

    public Account(final int id, final int balance) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public boolean aquireLock() {
        return lock.compareAndSet(false, true);
    }

    public void releaseLock() {
        lock.set(false);
    }
}
