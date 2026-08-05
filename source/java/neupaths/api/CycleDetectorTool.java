// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.CommandLine;
import neupaths.util.PropertySet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Utility for identifying cyclical paths in a NeuPaths cell system.  While the
 * NeuPaths runtime automatically filters duplicate stimuli, cyclical paths can
 * result in excessive stimuli activity, which can impact performance.
 * Note that cyclical paths are not necessarily a design flaw.
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.api.CycleDetectorTool [-k|--key <crypto_key_file>] <synapseName>}</pre>
 * <p>
 * <i>crypto_key_file</i>: Optional file path/name of the crypto key file created by the {@link neupaths.util.GenerateCryptoKey} utility.
 * </p>
 * <p>
 * <i>synapseName</i>: Synapse specification.
 * </p>
 * </ul>
 * <p>
 * Cycles will be reported using the following format:
 * <ul>
 * <pre>{@code Cell_Name1 (Domain1) => Cell_Name2 (Domain2) => ... Cell_NameN (DomainN)}</pre>
 * </p>
 * </ul>
 *
 * @author Aaron Caraveo
 */
public final class CycleDetectorTool
{
  private CycleDetectorTool ()
  {
    // Construction is not necessary
  }
  
  private static void printUsage ()
  {
    System.out.println("usage: java -classpath neupaths.jar neupaths.api.CycleDetectorTool [-k|--key <crypto_key_file>] <synapseName>");
    System.out.println("    crypto_key_file  Optional file path/name of a crypto key file");
    System.out.println("                     created by the GenerateCryptoKey utility.");
    System.out.println("    synapseName      Synapse specification.");
  }
  
  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
    byte[] cryptoKey = null;
    InjectorCell injector = null;
    LogicCell detector = null;
    Actv_CycleDetected cdetActivator = new Actv_CycleDetected();
    
    PropertySet arguments = CommandLine.parse(args, new String[] {"k", "key"});
    
    if (arguments == null)
    {
      System.out.println("ERROR: Invalid command-line flag");
      printUsage();
      System.exit(1);      
    }
    
    if (arguments.contains("k") || arguments.contains("key"))
    {
      String keyFileName = null;
      
      if (arguments.contains("k"))
        keyFileName = arguments.get("k");
      else
        keyFileName = arguments.get("key");
      
      try
      {
        File keyFile = new File(keyFileName);

        cryptoKey = new byte[(int)keyFile.length()];

        FileInputStream keyFileStream = new FileInputStream(keyFile);
        keyFileStream.read(cryptoKey);
      }
      catch (FileNotFoundException fnfe)
      {
        System.out.println("ERROR: Could not find " + keyFileName + ": " + fnfe.getMessage());
        System.exit(1);
      }
      catch (IOException ioe)
      {
        System.out.println("ERROR: Could not open " + keyFileName + ": " + ioe.getMessage());
        System.exit(1);
      }
    }
    
    if (arguments.contains("opt1"))
    {
      try
      {
        injector =
            new InjectorCell("CycleInjector",
                             (String)arguments.get("opt1"),
                             new TransmitterSpec(Cdet.CDET_DETECTION_TRANSMITTER,
                                                 Stim_CycleDetection.TYPE_ID,
                                                 StimulusTrace.ENABLED),
                             cryptoKey);

        detector =
            new LogicCell("CycleCell",
                          arguments.get("opt1"),
                          cdetActivator,
                          cryptoKey);
        
//        injector.enableDebugOutputLogging();
//        detector.enableDebugOutputLogging();
        
        // Start the cells
        injector.start();
        detector.start();
        
        try { Thread.sleep(2000); } catch (InterruptedException ie) { /* do nothing */ }
        
        // Inject the cycle detection stimulus
        injector.inject(new Stim_CycleDetection());
        
        System.out.println("\nDetecting (probing system for 1 minute) ...");
        
        int iteration = 0;
        while (iteration < 6)
        {
          iteration++;
          
          try { Thread.sleep(10000); } catch (InterruptedException ie) { /* do nothing */ }
        }
        
        // Stop the cells
        detector.stop();
        injector.stop();

        int numCycles = 0;
        for (String c : cdetActivator.cycles)
        {
          if (c.indexOf("Cluster:Ctrl") == -1)
	    numCycles++;
        }

        System.out.println("\n" + numCycles + " cycle(s) detected\n");
        
        if (numCycles > 0)
        {
          System.out.println("Cycles:");
          
          for (String c : cdetActivator.cycles)
          {
            if (c.indexOf("Cluster:Ctrl") == -1)
              System.out.println("  " + c);
          }
        }
        
        System.exit(0);
      }
      catch (NeuPathsException bre)
      {
        System.out.println("ERROR: " + bre);
        System.exit(1);
      }
    }
    else
    {
      System.out.println("ERROR: synapse required");
      printUsage();
      System.exit(1);
    }
  }
}
