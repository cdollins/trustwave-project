/*
 * Copyright 2009 LugIron Software, Inc. All Rights Reserved.
 *
 * $Id$
 */

package com.trustwave.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import java.util.List;
import java.util.ArrayList;

public class Runner {
    private static final int DEFAULT_BALANCE = 2000;

    public static void main(final String... args) {
        final Logger logger = LoggerFactory.getLogger(Runner.class);

        final CommandLineParser parser = new PosixParser();

        try {
            final CommandLine cmd = parser.parse(createOptions(), args);

            final boolean reqOptions = cmd.hasOption("T") && cmd.hasOption("X") && cmd.hasOption("N");

            if (cmd.hasOption("help") || !reqOptions) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("transaction", createOptions());
                return;
            }

            if (cmd.hasOption("V")) {
                System.setProperty("verbose", "true");
            }

            final int accountCount = Integer.parseInt(cmd.getOptionValue("N"));
            final int threadCount = Integer.parseInt(cmd.getOptionValue("T"));
            final int transferAmount = Integer.parseInt(cmd.getOptionValue("X"));

            final AccountManager accountMgr = new AccountManager(createAccounts(accountCount));

            final TransactionManager transactionMgr =
                    new TransactionManager(accountMgr);

            transactionMgr.simulate(threadCount, transferAmount);
        }
        catch (ParseException e) {
            logger.error("Unable to parse the provided options.", e);
        }
    }

    private static List<Account> createAccounts(final int size) {
        final List<Account> accounts = new ArrayList<Account>();
        for (int x = 0; x < size; ++x) {
            accounts.add(new Account(DEFAULT_BALANCE));
        }

        return accounts;
    }

    private static Options createOptions() {

        final Options options = new Options();

        options.addOption("help", false, "Print this message");

        options.addOption("V", "verbose", false, "Provides more indepth debug information");

        options.addOption("T", "threads", true, "Number of simultaneous threads to start");

        options.addOption("X", "transfer", true, "Number of dollars for each transfer");

        options.addOption("N", "accounts", true, "Number of user accounts");

        return options;
    }
}
