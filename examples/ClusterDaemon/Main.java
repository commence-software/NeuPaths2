// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.UUID;

public class Main
{
  public static void main (String[] args)
  {
    UUID trans = null;
    StringStimulus resp = null;
    
    try
    {
      System.out.println("Creating event cell ...");
      EventCell events =
        new EventCell("TestEvents",
                      "Local#Stream#Peer#Test#/tmp/router.syn",
                      "./test_events.out",
                      null);
      events.start();

      System.out.println("Creating injector ...");
      InjectorCell inj =
        new InjectorCell("TestInjector",
                         "Local#Stream#Peer#Test#/tmp/router.syn",
                         new TransmitterSpec("Request",
                                             SignalStimulus.TYPE_ID,
                                             StimulusTrace.ENABLED),
                         null);
      inj.enableTraceLogging();
      inj.enableDebugLogging();
      inj.enableRuntimeLogging();
      inj.start();

      System.out.println("Creating extractor ...");
      ExtractorCell extr =
        new ExtractorCell("TestExtractor",
                          "Local#Stream#Peer#Test#/tmp/router.syn",
                          new ExtractorSubscriptionSpec("LBC_.*",
                                                        "Response",
                                                        "Test"),
                          null);
      extr.enableTraceLogging();
      extr.enableDebugLogging();
      extr.enableRuntimeLogging();
      extr.start();

      try { Thread.sleep(2000); } catch (InterruptedException ie) {}
      
      int seq_num = 0;
      while (true)
      {
        try
        {
          Thread.sleep(2000);

          seq_num++;

          if (seq_num % 2 == 1)
          {
            System.out.println("Injecting stimulus ...");
            inj.inject(new SignalStimulus(true));
            
            System.out.println("Extracting stimulus ...");
            resp = extr.extract();
          }
          else
          {
            System.out.println("Injecting stimulus as transaction ...");
            trans = inj.injectAsTransaction(new SignalStimulus(true));

            System.out.println("Extracting stimulus from transaction " + trans + " ...");
            resp = extr.extractFromTransaction(trans);
          }

          System.out.println("RESP[" + seq_num + "] " + resp);
        }
        catch (InterruptedException ie)
        {
          // do nothing
        }
      }
    }
    catch (NeuPathsException npe)
    {
      System.out.println("ERROR: " + npe);
    }
  }
}
