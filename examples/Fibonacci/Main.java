// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

class Main
{
  public static void main (String[] args)
  {
    // NOTES:
    //   1.  When started, a cell's binders join the cellular system
    //       immediately.
    //   2.  Cells wait one subscription refresh period (default is 1500 ms)
    //       to send out the first subscriptions, and then periodically on
    //       the subscription refresh interval after that (unless
    //       subscription refresh is disabled.)
    //   3.  Activators are started 1.1 * SubscriptionRefreshInterval
    //       milliseconds after cell start (e.g. if SubscriptionRefreshInterval
    //       is 1500 ms, activators start 1650 ms after cell start.)  This
    //       gives time for subscriptions to reach the cell before the
    //       activator start() method is invoked, which may set initial
    //       stimuli.
    //
    //
    
    // Create and start the fibonacci cell
    LogicCell fibonacci =
      new LogicCell("FibonacciGenerator",
                    new String[] {
                      "Local#Stream#Listener#@#/tmp/fibonacci.syn" },
                    new Activator[] {
                      new FibonacciActivator() },
                    null);

    // Pulse every second
    fibonacci.setPulseInterval(1000);
    fibonacci.enableTraceLogging();
    fibonacci.enableDebugLogging();
    fibonacci.start();

    // Create and start the event cell
    EventCell events =
      new EventCell("Events",
                    "Local#Stream#Peer#@#/tmp/fibonacci.syn",
                    "./fibonacci.out",
                    null);

    events.start();
                    
    // Create and start the extractor
    ExtractorCell extr =
      new ExtractorCell("ResultExtractor",
                        "Local#Stream#Peer#@#/tmp/fibonacci.syn",
                        new ExtractorSubscriptionSpec("FibonacciGenerator",
                                                      "Result",
                                                      "@"),
                        null);

    extr.enableTraceLogging();
    extr.enableDebugLogging();
    extr.start();

    // Repeatedly get the next fibonacci number
    while (true)
    {
      IntegerStimulus fib = extr.extract();
      System.out.println("Next fib number: " + fib.get());
    }
  }
}
