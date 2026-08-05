# Example: Fibonacci Generator

## Description

A Fibonacci sequence generator.  This example uses a logic cell with a
single pulsed activator that produces two stimuli at startup: F(n-1)=1 and
F(n-2)=0.  At each pulse F(n) is calculated, and F(n-1) and F(n-2) are emitted.
Loopback subscriptions complete the loop.

## Features/Techniques Used

- Pulsed activator
- Loopback subscriptions
- Custom activator with overridden start() method that emits
  stimuli
- Local stream synapses (Unix sockets)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

`java -cp neupaths.jar:. Main`
