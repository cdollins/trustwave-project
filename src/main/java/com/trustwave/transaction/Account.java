/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

public class Account {
    private final int id;
    private int balance;

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
}
