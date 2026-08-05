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
    try
    {
      System.out.println("Creating cells ...");
  
      LogicCell servCell =
          new LogicCell("ServiceCell",
                        new String[] {
                          "Network#Stream#Listener#Service#30001" },
                        new Activator[] {
                          new ServiceRequestActivator() },
                        null);
  
      LogicCell transCell_1 =
          new LogicCell("TransactionCell_1",
                        new String[] {
                          "Network#Stream#Listener#@#30002",
                          "Network#Stream#Peer#Service#30001#localhost" },
                        new Activator[] {
                          new TransactionRequestActivator(),
                          new TransactionResponseActivator() },
                        null);
  
      LogicCell transCell_2 =
          new LogicCell("TransactionCell_2",
                        new String[] {
                          "Network#Stream#Listener#@#30003",
                          "Network#Stream#Peer#Service#30001#localhost" },
                        new Activator[] {
                          new TransactionRequestActivator(),
                          new TransactionResponseActivator() },
                        null);
  
      InjectorCell transInjector =
          new InjectorCell("TransactionInjector",
                           new String[] {
                             "Network#Stream#Peer#@#30002#localhost",
                             "Network#Stream#Peer#@#30003#localhost" },
                           new TransmitterSpec("TransactionRequest",
                                               SignalStimulus.TYPE_ID,
					       StimulusTrace.ENABLED),
                           null);
  
      ExtractorCell transExtractor =
          new ExtractorCell("TransactionExtractor",
                            new String[] {
                              "Network#Stream#Peer#@#30002#localhost",
                              "Network#Stream#Peer#@#30003#localhost" },
                            new ExtractorSubscriptionSpec("TransactionCell.*",
                                                          "TransactionResponse",
                                                          "@",
                                                          TransactionFilter.ENABLED),
                            null);
  
      // Enable trace logging
      System.out.println("Enabling logging ...");
      servCell.enableSystemLogging();
      //servCell.enableDebugOutputLogging();
      transCell_1.enableSystemLogging();
      //transCell_1.enableDebugOutputLogging();
      transCell_2.enableSystemLogging();
      //transCell_2.enableDebugOutputLogging();
      transExtractor.enableSystemLogging();
      //transExtractor.enableDebugOutputLogging();
      transInjector.enableSystemLogging();
      //transInjector.enableDebugOutputLogging();
      
      // Set the subscription refresh intervals at 10 sec
      servCell.setSubscriptionRefreshInterval(10000L);
      transCell_1.setSubscriptionRefreshInterval(10000L);
      transCell_2.setSubscriptionRefreshInterval(10000L);
      transExtractor.setSubscriptionRefreshInterval(10000L);
      transInjector.setSubscriptionRefreshInterval(10000L);
  
      // Start the cells
      System.out.println("Starting the cells ...");
      servCell.start();
      transCell_1.start();
      transCell_2.start();
      transExtractor.start();
      transInjector.start();
  
      // Give some time for the cluster to stabilize
      try {Thread.sleep(11000);} catch (InterruptedException ie) { /* ignore */ }
  
      // Inject a transaction request
      UUID transID = transInjector.injectAsTransaction(new SignalStimulus());
      System.out.println("Injected transaction " + transID + " ...");
  
      // Wait for the response
      while (true)
      {
        System.out.println("Extracting the transaction response ...");
        StringStimulus transResp = transExtractor.extractFromTransaction(transID);
  
        System.out.println("RECEIVED: " + transResp.toString());
      }
    }
    catch (NeuPathsException bre)
    {
      System.out.println("It Bombed! " + bre);
    }
  }
}
