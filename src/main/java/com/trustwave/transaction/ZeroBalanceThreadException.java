/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

public class ZeroBalanceThreadException extends RuntimeException {
    private final int threadId;

    public ZeroBalanceThreadException(final int threadId) {

        this.threadId = threadId;
    }

    public int getThreadId() {
        return threadId;
    }
}
