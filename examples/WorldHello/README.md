# Example: WorldHello

## Description

Source code for the WorldHello example in the SDK User Guide.  The "cfg"
subdirectory contains the Cluster and Cell definition files.  After execution,
the worldhello_request_events.out and worldhello_reply_events.out files
contain the trace log for the Request and Reply domains respectively.

## Features/Techniques Used

- Cluster and Cell definition files
- Custom activator with public default constructor (for use in
  cell definition files)
- Use of properties for parameterized activator
- Multi-domain
- Network unicast synapses (UDP/IP)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

`java -cp neupaths.jar:. Main`
