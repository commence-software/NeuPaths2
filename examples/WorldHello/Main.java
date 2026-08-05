// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.LinkedList;

public class Main
{
  public static void main (String[] args)
  {
    try
    {
      System.out.println("Initializing Cell Network...");
      
      // Construct and start the cluster
      CellCluster cluster = new CellCluster("cfg/WorldCluster.xml");
      cluster.start();

      // Retrieve injector and extractor from cluster
      InjectorCell hail = cluster.getCell("HailInjector");
      ExtractorCell salutations = cluster.getCell("SalutationExtractor");

      // Give cluster time to initialize
      try {Thread.sleep(2000);} catch (InterruptedException ie) { /* ignore */ }
      
      // Hail the cities
      System.out.println("Hailing the cities...");
      hail.inject(new DateStimulus());
      
      // Display salutations
      int responseCount = 0;
      while (responseCount < 7)
      {
        WorldHelloSalutation salutation = salutations.extract();
        responseCount++;
        
        System.out.println("--> " + salutation);
      }
      
      System.out.println("All cities have reported.  Terminating...");
      
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
  
