// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.LinkedList;
import java.util.UUID;

public class Main
{
  public static void main (String[] args)
  {
    try
    {
      System.out.println("Initializing Cell Network...");
      
      // Construct and start the cluster
      CellCluster cluster = new CellCluster("cfg/Cluster.xml");
      cluster.start();

      // Retrieve injector and extractor from cluster
      InjectorCell reqInj = cluster.getCell("RequestInjector");
      ExtractorCell respExtr = cluster.getCell("ReplyExtractor");

      // Give cluster time to initialize
      try {Thread.sleep(2000);} catch (InterruptedException ie) { /* ignore */ }
      
      // Make a request
      System.out.println("Requesting property 'ExampleName' ...");
      UUID transID = reqInj.injectAsTransaction(new ClusterRequest("ExampleName"));

      // Get the response
      ClusterResponse resp = respExtr.extractFromTransaction(transID);
      System.out.println("Response: " + resp.propertyValue);

      // Give cluster time to process final events
      try {Thread.sleep(1000);} catch (InterruptedException ie) { /* ignore */ }
      
      // Stop all cells
      cluster.stop();
      
      System.out.println("Cell Network shutdown.  Good bye.");
    }
    catch (NeuPathsException npe)
    {
      System.out.println("Fatal error occurred: " + npe);
    }
  }  
}
  
