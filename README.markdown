# Trustwave Programming Assignment

## Problem Statement
Write a multithreaded simulator that runs 'T' threads and exits when done. 
The behavior of the threads and the executable is described below:

Thread run: Each thread has to do the following

 1.  Picks 2 random accounts from N-user-accounts.
 2.  Transfers 'X' dollars from source-account to destination-account.
 3.  Go back to step-1

Thread Exit: The threads should all terminate when there is one account with zero balance.

Input: The executable can take three arguments

 *   'T': number of simultaneous threads to start.
 *   'X': number of dollars for each transfer.
 *   'N': number of user accounts

Output: The output will be the number of transactions it takes before the threads exit.


## Build Instructions
I used [Maven](http://maven.apache.org/) mostly for dependency management & package automation.

    $ mvn package appassembler:assemble
    $ sh target/appassembler/bin/transaction

## Contributing
You can find the repository at: http://github.com/cdollins/trustwave-project

Issues can be submitted at: http://github.com/cdollins/trustwave-project/issues

I'd really like to hear your feedback, and I'd love to receive your pull-requests!

## TODO
 1. Comment the Code

 2. More Tests

## Copyright
Copyright (c) 2010 Chad Dollins. See LICENSE for details.