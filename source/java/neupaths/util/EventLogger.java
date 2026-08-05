// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import neupaths.api.*;

/**
 * Utility for reporting log events to standard output or designated file.
 * <p>
 * This utility subscribes to log events in the synapse domain and
 * displays them to standard output or spools them to a designated file.
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.util.EventLogger [-k|--key <cryptoKeyFile>] <synapseName> [<outputFile>]}</pre>
 * <p>
 * <i>cryptoKeyFile</i>: Optional file pathname of a crypto key file created
 * by the {@link GenerateCryptoKey} utility.
 * </p>
 * <p>
 * <i>synapseName</i>: Synapse specification.  Supports Listener and Peer synapses.
 * </p>
 * <p>
 * <i>outputFile</i>: Optional file pathname to spool events to.
 * </p>
 * </ul>
 * 
 * @author Aaron Caraveo
 */
public class EventLogger
{
  private EventLogger ()
  {
    // Construction is not necessary
  }

  private static void printUsage ()
  {
    System.out.println("usage: java -classpath neupaths.jar neupaths.util.EventLogger [-k|--key <cryptoKeyFile>] <synapseName> [<outputFile>]\n");
    System.out.println("    cryptoKeyFile    Optional file pathname of a crypto key file");
    System.out.println("                     created by the GenerateCryptoKey utility.");
    System.out.println("    synapseName      Synapse specification.  Supports");
    System.out.println("                     Listener and Peer synapses.");
    System.out.println("    outputFile       Optional file pathname to spool events to.");
  }
  
  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
    EventCell eventCell = null;
    byte[] cryptoKey = null;
    
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
        System.out.println("ERROR: Could not open/read " + keyFileName + ": " + ioe.getMessage());
        System.exit(1);
      }
    }
    
    UUID loggerID = UUID.randomUUID();
    String[] idFields = loggerID.toString().split("-");

    if (Integer.valueOf(arguments.get("optCnt")) >= 2)
    {
      eventCell = new EventCell("EventLogger_" + idFields[idFields.length-1],
                                (String)arguments.get("opt1"),
                                (String)arguments.get("opt2"),
                                cryptoKey);
    }
    else if (Integer.valueOf(arguments.get("optCnt")) == 1)
    {
      eventCell = new EventCell("EventLogger_" + idFields[idFields.length-1],
                                (String)arguments.get("opt1"),
                                System.out,
                                cryptoKey);
    }
    else
    {
      System.out.println("ERROR: synapse required");
      printUsage();
      System.exit(1);
    }

    // Start the event cell
    eventCell.start();
    
    // All processing done by event cell
    while (true)
    {
      try {Thread.sleep(10000);} catch (InterruptedException ie) {/* ignore */}
    }
  }
}
