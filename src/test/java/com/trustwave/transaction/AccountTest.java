/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class AccountTest {

    @Test
    public void testWithdrwal() {
        final Account account = new Account(0, 1000);
        for (int x = 0; x < 10; ++x) {
            account.withdrawl(100);
        }

        assertEquals(account.getBalance(), 0);
    }

    @Test
    public void testDeposit() {

        final Account account = new Account(0, 0);
        for (int x = 0; x < 10; ++x) {
            account.deposit(100);
        }

        assertEquals(account.getBalance(), 1000);
    }
}
