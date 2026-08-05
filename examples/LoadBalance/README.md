# Example: Load Balancing

## Description

Demonstrates load balancing using load-controller and load-balanced cells.
The TestInjector alternates sending stimuli with and without a transaction.
Three load-balanced cells (LBC_1, LBC_2 and LBC_3) express interest in the
stimuli and request permission from the load-controller (TestController)
to process them.  The load-controller distributes the stimuli among the
load-balanced cells.

## Features/Techniques Used

- Cluster and Cell definition files
- Custom load-balanced activator
- Transactions
- Local stream synapses (Unix sockets)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

`java -cp neupaths.jar:. Main`
