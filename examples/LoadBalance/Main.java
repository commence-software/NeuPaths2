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
    Cell[] lbcs = new Cell[3];
    UUID trans = null;
    StringStimulus resp = null;
    
    try
    {
      System.out.println("Creating cluster ...");
      CellCluster cluster = new CellCluster("cfg/Cluster.xml");

      InjectorCell inj = cluster.getCell("TestInjector");
      ExtractorCell extr = cluster.getCell("TestExtractor");
      
      System.out.println("Starting cluster ...");
      cluster.start();
      
      try { Thread.sleep(5000); } catch (InterruptedException ie1) { /* do nothing */ }

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
