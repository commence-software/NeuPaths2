# Example: Transaction

## Description

Source code for the Transaction example in the SDK User Guide.  The "cfg"
subdirectory contains the Cluster and Cell definition files.  After execution,
the events.out file contains the trace log for the global domain.

## Features/Techniques Used

- Cluster and Cell definition files
- Custom activator with public default constructor (for use in
  cell definition files)
- Chained transactions
- Network unicast synapses (UDP/IP)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

`java -cp neupaths.jar:. Main`
