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

public class Runner {

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
            
        }
        catch (ParseException e) {
            logger.error("Unable to parse the provided options.", e);
        }
    }

    private static Options createOptions() {

        final Options options = new Options();

        options.addOption("help", false, "Print this message");

        options.addOption("V", "verbose", false, "Provides more indepth debug information");

        options.addOption("T", "threads", true, "Number of simultaneous threads to start");

        options.addOption("X", "transfer", true, "Number of dollars for each transfer");

        options.addOption("N", "accounts", true, "number of user accounts");

        return options;
    }
}
