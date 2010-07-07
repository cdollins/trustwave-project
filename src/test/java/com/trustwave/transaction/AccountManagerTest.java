/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.testng.annotations.Test;

public class AccountManagerTest {

    @Test
    public void testAquire() {
        //aquireAccounts

    }

    @Test void testAquireRelease() {
        // aquireAccounts
        // releaseAccounts
    }

    @Test void testAquireReleaseAquire() {
        // aquireAccounts
        // releaseAccounts
        // aquireAccounts
        
    }

    @Test
    public void testAlreadyAquired() {
        // aquireAccounts
        // aquireAccounts

    }

    /********************
     * Negative Testing
     */

    @Test
    public void testRelease() {
        // releaseAccounts
    }

    @Test void testReleaseRelease() {
        // aquireAccounts
        // releaseAccounts
        // releaseAccounts
    }
}
