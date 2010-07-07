/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import java.util.List;

public class AccountManager {
    private final List<Account> accounts;

    private boolean exitLock = false; 

    public AccountManager(final List<Account> accounts) {
        this.accounts = accounts;
    }

    public boolean aquire(final int sourceId, final int destinationId) throws ZeroBalanceException {
        final Account sourceAccount = accounts.get(sourceId);
        final Account destinationAccount = accounts.get(destinationId);

        hasZeroBalance(sourceAccount, destinationAccount);

        final boolean sourceAccountLockAquired = sourceAccount.getLock();
        final boolean destinationAccountLockAquired = destinationAccount.getLock();

        if (sourceAccountLockAquired && destinationAccountLockAquired) {
            return true;
        }
        else if (destinationAccountLockAquired) {
            destinationAccount.releaseLock();
        }
        else if (sourceAccountLockAquired) {
            sourceAccount.releaseLock();
        }

        return false;
    }

    public void release(final int sourceId, final int destinationId) {
        accounts.get(destinationId).releaseLock();
        accounts.get(sourceId).releaseLock();
    }

    public void transact(final int sourceId, final int destinationId, final int transferAmount)
            throws ZeroBalanceException {
        final Account sourceAccount = accounts.get(sourceId);
        final Account destinationAccount = accounts.get(destinationId);

        hasZeroBalance(sourceAccount, destinationAccount);

        sourceAccount.withdrawl(transferAmount);
        destinationAccount.deposit(transferAmount);

        hasZeroBalance(sourceAccount, destinationAccount);
    }

    private void hasZeroBalance(final Account sourceAccount, final Account destinationAccount) throws ZeroBalanceException {
        if (exitLock) {
           throw new ZeroBalanceException();
        }
        if (sourceAccount.getBalance() == 0 || destinationAccount.getBalance() == 0) {
            exitLock = true;
            throw new ZeroBalanceException();
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
