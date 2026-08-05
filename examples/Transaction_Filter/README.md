# Example: Transaction Filtering

## Description

Demonstrates transactions.  The TransactionInjector cell emits a transaction
request (SignalStimulus) that is received by redundant transaction handlers
(TransactionCell_1 and TransactionCell_2.)  Each of those emits a service
request (StringStimulus) under a new transaction.  The ServiceCell processes
both requests, whose responses return to the corresponding TransactionCell
(TransactionCell_1 or TransactionCell_2.)  Each TransactionCell forwards the
service response under the original transaction, resulting in redundant
responses at the TransactionExtractor cell.  The TransactionExtractor cell
uses transaction filtering to omit the redundant response.

## Features/Techniques Used

- Chained transactions
- Redundant service providers
- Transaction filtering
- Multi-domain
- Network stream synapses (TCP/IP)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

`java -cp neupaths.jar:. Main`
