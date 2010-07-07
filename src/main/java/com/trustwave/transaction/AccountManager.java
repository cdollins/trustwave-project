/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import java.util.List;

public class AccountManager {
    private final List<Account> accounts;

    public AccountManager(final List<Account> accounts) {
        this.accounts = accounts;
    }

    public Account retrieve(final int accountId) {
        final Account account =  accounts.get(accountId);

        if (account.getLock()) {
            return account;
        }
        else {
            return null;
        }
    }

    public void print() {
        System.out.println("Account Id    |   Balance");
        System.out.println("-------------------------");

        for (int x = 0; x < accounts.size(); ++x) {
            System.out.println(String.format("%10d    |   %7d", x, accounts.get(x).getBalance()));
        }
    }

    public int getNumberOfAccount() {
        return accounts.size();
    }
}
