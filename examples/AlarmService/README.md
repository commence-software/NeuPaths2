# Example: Alarm Service

## Description

A simple service that schedules one-time and recurring alarms.  The
AlarmServiceCell uses two activators: ScheduleActivator and AlarmActivator.
The initialize() method of AlarmServiceCell creates a synchronized list to
hold the scheduled alarms and places the list in a shared property.  The
ScheduleActivator populates the scheduled alarm list, and the AlarmActivator
periodically checks for expired alarms using the cell's pulses.

## Features/Techniques Used

- Cluster and Cell definition files
- Custom (specialized) cell that overrides initialize() method
- Multiple activators using shared property
- Pulsed activator
- Local stream synapses (Unix sockets)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

`java -cp neupaths.jar:. Main`
