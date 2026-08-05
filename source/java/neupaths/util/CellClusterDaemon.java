// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import neupaths.api.*;

/**
 * A NeuPaths cell cluster daemon.
 * <p>
 * This utility operates as a daemon for deploying and controlling NeuPaths
 * cell clusters.  It will not terminate unless an error is encountered or
 * the user manually terminates.  Multiple instances of the daemon can
 * exist on the same host as long as the synapses do not conflict.
 * </p>
 * <p>
 * The daemon's <i>synapseName</i> can be a Listener or a Peer.  When using a
 * Listener synapse, tools for deploying and controlling cells (e.g.
 * {@link IssueCommandToDaemon}) will connect directly to the daemon
 * (one-to-one.)  When using a Peer synapse, the tools can establish an
 * advertised Listener to which all daemons connect (many-to-one.)  The
 * synapse domain will be overridden with an internal domain.  Users can
 * specify the global ("@") domain for convenience.
 * </p>
 * <p>
 * The <i>configTopDir</i> argument specifies the root directory of the
 * configuration directory structure.  This pathname can specify a
 * network accessible location so that all daemons use the same configuration
 * set.
 * </p>
 * <p>
 * The classpath specified at invocation should include all user classes
 * needed for cell cluster deployment (custom stimulus types and activators.)
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.util.CellClusterDaemon [-k|--key <cryptoKeyFile>] <synapseName> <daemonName> <configTopDir>}</pre>
 * <p>
 * <i>cryptoKeyFile</i>: Optional file pathname of a crypto key file created
 * by the {@link GenerateCryptoKey} utility.
 * </p>
 * <p>
 * <i>synapseName</i>: Synapse specification.  Supports Listener and Peer synapses.
 * </p>
 * <p>
 * <i>daemonName</i>: Name of this cell cluster daemon.  This name should be
 * unique among all running cell cluster daemons.
 * </p>
 * <p>
 * <i>configTopDir</i>: Pathname of the top-level directory for the Cluster
 * and Cell definition files.
 * </p>
 * </ul>
 *
 * @author Aaron Caraveo
 */
public class CellClusterDaemon
{
  private CellClusterDaemon ()
  {
    // Construction not necessary
  }

  private static void printUsage ()
  {
    System.out.println("usage: java -classpath neupaths.jar neupaths.util.CellClusterDaemon [-k|--key <cryptoKeyFile>] <synapseName> <daemonName> <configTopDir>\n");
    System.out.println("    cryptoKeyFile    Optional file pathname of a crypto key file");
    System.out.println("                     created by the GenerateCryptoKey utility.");
    System.out.println("    synapseName      Synapse specification.  Supports");
    System.out.println("                     Listener and Peer synapses.");
    System.out.println("    daemonName       Name of this cell cluster daemon.");
    System.out.println("                     This name should be unique among");
    System.out.println("                     all running cell cluster daemons.");
    System.out.println("    configTopDir     Pathname of the top-level directory");
    System.out.println("                     for the Cluster and Cell definition");
    System.out.println("                     files.");
  }

  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
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

    if (Integer.valueOf(arguments.get("optCnt")) < 3)
    {
      System.out.println("ERROR: Missing required arguments");
      printUsage();
      System.exit(1);      
    }
    
    try
    {
      String synapseName  = arguments.get("opt1");
      String daemonName   = arguments.get("opt2");
      String configTopDir = arguments.get("opt3");

      // Override the domain if necessary
      SynapseSpec daemonSynapseSpec = new SynapseSpec(synapseName);
      if (!daemonSynapseSpec.getDomain().equals("Daemon:Ctrl"))
      {
        System.out.println("Overriding domain ...");
        daemonSynapseSpec.updateDomain("Daemon:Ctrl");
      }

      // Create a random UUID to be used in daemon cell and temporary file names
      UUID uuid = UUID.randomUUID();

      // Create temp file name for event synapse
      String internalSynapseFileName = System.getProperty("java.io.tmpdir") +
                                       File.separator +
                                       daemonName + "_" + uuid + ".syn";

      // Delete the file in case it exists
      File internalTmpFile = new File(internalSynapseFileName);
      internalTmpFile.delete();

      // Create internal synapse name
      String internalSynapseName = "Local#Stream#Listener#Daemon:Ctrl#" +
                                   internalSynapseFileName;

      SynapseSpec internalPeerSynapse = new SynapseSpec(internalSynapseName);
      internalPeerSynapse.toggleMode();

      // Create the daemon activator
      DaemonCmdActv activator = new DaemonCmdActv();

      // Create the daemon cell
      LogicCell daemonCell =
          new LogicCell(daemonName + "_" + uuid,
                        new String[] {
                            daemonSynapseSpec.toString(),
                            internalSynapseName
                        },
                        new Activator[] {
                            activator
                        },
                        cryptoKey);

      // Make sure global subscriptions don't leak out of daemon
      daemonCell.disableGlobalSubscriptionPropagation();

      //daemonCell.enableSystemLogging();

      activator.setProperty("daemonName", daemonName);
      activator.setProperty("daemonID", uuid.toString());
      activator.setProperty("configTopDir", configTopDir);

      // Create the event cell
      EventCell eventCell = new EventCell("DaemonEvents",
                                          internalPeerSynapse.toString(),
                                          "daemonEvents.out",
                                          cryptoKey);

      // Turn on trace logging for daemon
      daemonCell.enableTraceLogging();

      // Start the cells
      daemonCell.start();
      eventCell.start();

      System.out.println("Daemon ready.");
      System.out.println("  Name:       " + daemonName);
      System.out.println("  Synapse:    " + synapseName);
      System.out.println("  Config Dir: " + configTopDir);

      while (true)
      {
        try
        {
          Thread.sleep(10000);
        }
        catch (InterruptedException ie)
        {
          // Ignore
        }
      }

    }
    catch (NeuPathsException bre)
    {
      System.out.println("ERROR: Failed to start daemon: " + bre);
      System.exit(1);
    }

    System.exit(0);
  }
}
