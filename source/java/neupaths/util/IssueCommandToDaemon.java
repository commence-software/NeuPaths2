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
 * Issues commands to deploy and control NeuPaths cell clusters.
 * <p>
 * The <i>synapseName</i> can be a Listener or a Peer.  When using a
 * Listener synapse, cell cluster daemons can connect to the synapse,
 * allowing this utility to maintain multiple daemons (one-to-many.) 
 * When using a Peer synapse, the utility can connect directly to a running
 * daemon instance (one-to-one.)  The synapse domain will be overridden with
 * an internal domain.  Users can specify the global ("@") domain for
 * convenience.
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.util.IssueCommandToDaemon [-h|--help] [-k|--key <cryptoKeyFile>] <synapseName> <command> <arg1> ... <argN>}</pre>
 * <p>
 * <i>cryptoKeyFile</i>: Optional file pathname of a crypto key file created by
 * the {@link GenerateCryptoKey} utility.
 * </p>
 * <p>
 * <i>synapseName</i>: Synapse specification.  Supports Listener and Peer synapses.
 * </p>
 * <p>
 * <i>command</i>: The command to be issued.
 * </p>
 * <p>
 * <i>arg1 ... argN</i>: The commands' arguments.
 * </p>
 * </ul>
 * <p>
 * Commands:
 * <ul>
 * <li type=circle>
 * list:
 * <ul>
 *   <li><b>list daemons</b>: Lists the running CellClusterDaemons.</li>
 *   <li><b>list clusters</b> <i>daemonName</i>: Lists the clusters hosted by the specified daemon.</li>
 *   <li><b>list cells</b> <i>daemonName</i> <i>clusterName</i>: Lists the cells hosted by the specified cluster.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * deploy:
 * <ul>
 *   <li><b>deploy cluster</b> <i>daemonName</i> <i>clusterCfgFile</i>: Deploys and starts the cell cluster defined in the cluster definition file.  Pathname is relative to the daemon's configTopDir.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * recall:
 * <ul>
 *   <li><b>recall cluster</b> <i>daemonName</i> <i>clusterName</i>: Stops and recalls (removes) the specified cell cluster.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * start:
 * <ul>
 *   <li><b>start cluster</b> <i>daemonName</i> <i>clusterName</i>: Starts all cells in the specified cluster.</li>
 *   <li><b>start cell</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Starts the specified cell.  The cellName parameter can contain a regular expression.  All cells with names matching the expression will be started.<li>
 * </ul>
 * </li>
 * <li type=circle>
 * pause:
 * <ul>
 *   <li><b>pause cluster</b> <i>daemonName</i> <i>clusterName</i>: Pauses all cells in the specified cluster.</li>
 *   <li><b>pause cell</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Pauses the specified cell.  The cellName parameter can contain a regular expression.  All cells with names matching the expression will be paused.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * resume:
 * <ul>
 *   <li><b>resume cluster</b> <i>daemonName</i> <i>clusterName</i>: Resumes all cells in the specified cluster.</li>
 *   <li><b>resume cell</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Resumes the specified cell.  The cellName parameter can contain a regular expression.  All cells with names matching the expression will be resumed.</i>
 * </ul>
 * </li>
 * <li type=circle>
 * stop:
 * <ul>
 *   <li><b>stop cluster</b> <i>daemonName</i> <i>clusterName</i>: Stops all cells in the specified cluster.</li>
 *   <li><b>stop cell</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Stops the specified cell.  The cellName parameter can contain a regular expression.  All cells with names matching the expression will be stopped.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * publish:
 * <ul>
 *   <li><b>publish cluster</b> <i>daemonName</i> <i>clusterName</i>: Publishes the subscriptions for all cells in the specified cluster.</li>
 *   <li><b>publish cell</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Publishes the subscriptions for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * enable:
 * <ul>
 *   <li><b>enable cluster logging</b> <i>daemonName</i> <i>clusterName</i>: Enables event logging for all cells in the specified cluster.</li>
 *   <li><b>enable cell logging</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Enables event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 *   <li><b>enable cluster trace</b> <i>daemonName</i> <i>clusterName</i>: Enables trace event logging for all cells in the specified cluster.</li>
 *   <li><b>enable cell trace</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Enables trace event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 *   <li><b>enable cluster debug</b> <i>daemonName</i> <i>clusterName</i>: Enables debug event logging for all cells in the specified cluster.</li>
 *   <li><b>enable cell debug</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Enables debug event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 *   <li><b>enable cluster runtime</b> <i>daemonName</i> <i>clusterName</i>: Enables runtime event logging for all cells in the specified cluster.</li>
 *   <li><b>enable cell runtime</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Enables runtime event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 * </ul>
 * </li>
 * <li type=circle>
 * disable:
 * <ul>
 *   <li><b>disable cluster logging</b> <i>daemonName</i> <i>clusterName</i>: Disables event logging for all cells in the specified cluster.</li>
 *   <li><b>disable cell logging</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Disables event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 *   <li><b>disable cluster trace</b> <i>daemonName</i> <i>clusterName</i>: Disables trace event logging for all cells in the specified cluster.</li>
 *   <li><b>disable cell trace</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Disables trace event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 *   <li><b>disable cluster debug</b> <i>daemonName</i> <i>clusterName</i>: Disables debug event logging for all cells in the specified cluster.</li>
 *   <li><b>disable cell debug</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Disables debug event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 *   <li><b>disable cluster runtime</b> <i>daemonName</i> <i>clusterName</i>: Disables runtime event logging for all cells in the specified cluster.</li>
 *   <li><b>disable cell runtime</b> <i>daemonName</i> <i>clusterName</i> <i>cellName</i>: Disables runtime event logging for the specified cell. The cellName parameter can contain a regular expression.  All cells with names matching the expression will be affected.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 *
 * @author Aaron Caraveo
 */
public class IssueCommandToDaemon
{
  private IssueCommandToDaemon ()
  {
    // Construction not necessary
  }

  private static void printUsage ()
  {
    System.out.println("usage: java -classpath neupaths.jar neupaths.util.IssueCommandToDaemon [-h|--help] [-k|--key <cryptoKeyFile>] <synapseName> <command> <arg1> ... <argN>\n");
    System.out.println("    cryptoKeyFile    Optional file pathname of a crypto key file");
    System.out.println("                     created by the GenerateCryptoKey utility.");
    System.out.println("    synapseName      Synapse specification.  Supports");
    System.out.println("                     Listener and Peer synapses.");
    System.out.println("    command          The command to be issued.");
    System.out.println("    arg1 ... argN    The command's arguments.");

    System.out.println("\nCommands:");
    System.out.println("  list:");
    System.out.println("    list daemons");
    System.out.println("        Lists the running CellClusterDaemons.");
    System.out.println("    list clusters <daemonName>");
    System.out.println("        Lists the clusters hosted by the specified daemon.");
    System.out.println("    list cells <daemonName> <clusterName>");
    System.out.println("        Lists the cells hosted by the specified cluster.");

    System.out.println("\n  deploy:");
    System.out.println("    deploy cluster <daemonName> <clusterCfgFile>");
    System.out.println("        Deploys and starts the cell cluster defined in the");
    System.out.println("        cluster definition file.  Pathname is relative to");
    System.out.println("        the daemon's configTopDir.");

    System.out.println("\n  recall:");
    System.out.println("    recall cluster <daemonName> <clusterName>");
    System.out.println("        Stops and recalls (removes) the specified cell cluster.");

    System.out.println("\n  start:");
    System.out.println("    start cluster <daemonName> <clusterName>");
    System.out.println("        Starts all cells in the specified cluster.");
    System.out.println("    start cell <daemonName> <clusterName> <cellName>");
    System.out.println("        Starts the specified cell.  The cellName parameter");
    System.out.println("        can contain a regular expression.  All cells with");
    System.out.println("        names matching the expression will be started.");

    System.out.println("\n  pause:");
    System.out.println("    pause cluster <daemonName> <clusterName>");
    System.out.println("        Pauses all cells in the specified cluster.");
    System.out.println("    pause cell <daemonName> <clusterName> <cellName>");
    System.out.println("        Pauses the specified cell.  The cellName parameter");
    System.out.println("        can contain a regular expression.  All cells with");
    System.out.println("        names matching the expression will be paused.");

    System.out.println("\n  resume:");
    System.out.println("    resume cluster <daemonName> <clusterName>");
    System.out.println("        Resumes all cells in the specified cluster.");
    System.out.println("    resume cell <daemonName> <clusterName> <cellName>");
    System.out.println("        Resumes the specified cell.  The cellName parameter");
    System.out.println("        can contain a regular expression.  All cells with");
    System.out.println("        names matching the expression will be resumed.");

    System.out.println("\n  stop:");
    System.out.println("    stop cluster <daemonName> <clusterName>");
    System.out.println("        Stops all cells in the specified cluster.");
    System.out.println("    stop cell <daemonName> <clusterName> <cellName>");
    System.out.println("        Stops the specified cell.  The cellName parameter");
    System.out.println("        can contain a regular expression.  All cells with");
    System.out.println("        names matching the expression will be stopped.");

    System.out.println("\n  publish:");
    System.out.println("    publish cluster <daemonNanme> <clusterName>");
    System.out.println("        Publishes the subscriptions for all cells in");
    System.out.println("        specified cluster.");
    System.out.println("    publish cell <daemonName> <clusterName> <cellName>");
    System.out.println("        Publishes the subscriptions for the specified");
    System.out.println("        cell.  The cellName parameter can contain a");
    System.out.println("        regular expression.  All cells with names");
    System.out.println("        matching the expression will be affected.");

    System.out.println("\n  enable:");
    System.out.println("    enable cluster logging <daemonName> <clusterName>");
    System.out.println("        Enables event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    enable cell logging <daemonName> <clusterName> <cellName>");
    System.out.println("        Enables event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
    System.out.println("    enable cluster trace <daemonName> <clusterName>");
    System.out.println("        Enables trace event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    enable cell trace <daemonName> <clusterName> <cellName>");
    System.out.println("        Enables trace event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
    System.out.println("    enable cluster debug <daemonName> <clusterName>");
    System.out.println("        Enables debug event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    enable cell debug <daemonName> <clusterName> <cellName>");
    System.out.println("        Enables debug event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
    System.out.println("    enable cluster runtime <daemonName> <clusterName>");
    System.out.println("        Enables runtime event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    enable cell runtime <daemonName> <clusterName> <cellName>");
    System.out.println("        Enables runtime event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");

    System.out.println("\n  disable:");
    System.out.println("    disable cluster logging <daemonName> <clusterName>");
    System.out.println("        Disables event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    disable cell logging <daemonName> <clusterName> <cellName>");
    System.out.println("        Disables event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
    System.out.println("    disable cluster trace <daemonName> <clusterName>");
    System.out.println("        Disables trace event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    disable cell trace <daemonName> <clusterName> <cellName>");
    System.out.println("        Disables trace event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
    System.out.println("    disable cluster debug <daemonName> <clusterName>");
    System.out.println("        Disables debug event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    disable cell debug <daemonName> <clusterName> <cellName>");
    System.out.println("        Disables debug event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
    System.out.println("    disable cluster runtime <daemonName> <clusterName>");
    System.out.println("        Disables runtime event logging for all cells in the");
    System.out.println("        specified cluster.");
    System.out.println("    disable cell runtime <daemonName> <clusterName> <cellName>");
    System.out.println("        Disables runtime event logging for the specified cell.");
    System.out.println("        The cellName parameter can contain a regular");
    System.out.println("        expression.  All cells with names matching the");
    System.out.println("        expression will be affected.");
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

    int optCnt = Integer.valueOf(arguments.get("optCnt"));

    // Should have at least two arguments: synapseName command
    if (optCnt < 2)
    {
      System.out.println("ERROR: Missing required arguments");
      printUsage();
      System.exit(1);
    }
    
    try
    {
      String synapseName = arguments.get("opt1");
      String command     = arguments.get("opt2");

      // Override the domain if necessary
      SynapseSpec synapseSpec = new SynapseSpec(synapseName);
      if (!synapseSpec.getDomain().equals("Daemon:Ctrl"))
      {
        System.out.println("Overriding domain ...");
        synapseSpec.updateDomain("Daemon:Ctrl");
      }

      // Create a random UUID to be used in temporary file names
      UUID uuid = UUID.randomUUID();

      // Create temp file name for synapse
      String internalSynapseFileName = System.getProperty("java.io.tmpdir") +
                                       File.separator +
                                       "commandRouter_" + uuid + ".sock";

      // Delete the file in case it exists
      File internalTmpFile = new File(internalSynapseFileName);
      internalTmpFile.delete();

      // Create internal synapse name
      String internalSynapseName = "Local#Stream#Listener#Daemon:Ctrl#" +
                                   internalSynapseFileName;

      SynapseSpec internalPeerSynapse = new SynapseSpec(internalSynapseName);
      internalPeerSynapse.toggleMode();

      RouterCell commandRouter =
          new RouterCell("CommandRouter",
                         new String[] {
                             synapseSpec.toString(),
                             internalSynapseName,
                         },
                         cryptoKey);

      // Make sure global subscriptions don't leak out
      commandRouter.disableGlobalSubscriptionPropagation();

      InjectorCell commandInjector =
          new InjectorCell("CommandInjector",
                           internalPeerSynapse.toString(),
                           new TransmitterSpec(Daemon.COMMAND_REQ_TRANSMITTER,
                                               DaemonCmdStim.TYPE_ID),
                           cryptoKey);

      ExtractorCell responseExtractor =
          new ExtractorCell("ResponseExtractor",
                            internalPeerSynapse.toString(),
                            new ExtractorSubscriptionSpec(".*",
                                                          Daemon.COMMAND_RSP_TRANSMITTER,
                                                          "Daemon:Ctrl"),
                            cryptoKey);

      commandRouter.enableTraceLogging();
      commandInjector.enableTraceLogging();
      responseExtractor.enableTraceLogging();

//      commandRouter.enableDebugOutputLogging();
//      commandInjector.enableDebugOutputLogging();
//      responseExtractor.enableDebugOutputLogging();

      // Start the cells
      commandRouter.start();
      commandInjector.start();
      responseExtractor.start();

      System.out.println("Joining the cell system ...");

      // Give cluster time to initialize
      try {Thread.sleep(3000);} catch (InterruptedException ie) { /* ignore */ }
      
      System.out.println("Issuing command ...");

      // Issue the command
      if (command.equals("list") &&
          optCnt >= 3 &&
          arguments.get("opt3").equals("daemons"))
      {
        UUID transID = commandInjector.injectAsTransaction(
            new DaemonCmdStim(DaemonCmdType.DISCOVER_DAEMON,
                              "NA"));

        System.out.println("Daemons:");
        int iter = 0;
        while (iter < 10)
        {
          DaemonCmdRespStim response =
              responseExtractor.extractFromTransaction(transID, 1000);

          if (response != null)
          {
            for (DaemonCellInfo ci : response.records)
            {
              System.out.println("  " + ci.daemonName + " v" + ci.daemonVersion);
            }
          }

          iter++;
        }

        System.out.println("\nQuery complete.");
      }
      else if (command.equals("list") &&
               optCnt >= 4 &&
               arguments.get("opt3").equals("clusters"))
      {
        String daemonName = arguments.get("opt4");

        UUID transID = commandInjector.injectAsTransaction(
            new DaemonCmdStim(DaemonCmdType.DISCOVER_CLUSTER,
                              daemonName));

        System.out.println("Clusters:");
        int iter = 0;
        int clusterCnt = 0;
        while (iter < 10)
        {
          DaemonCmdRespStim response =
              responseExtractor.extractFromTransaction(transID, 1000);

          if (response != null)
          {
            clusterCnt += response.records.size();
            for (DaemonCellInfo ci : response.records)
            {
              System.out.println("  " + ci.clusterName + " v" + ci.daemonVersion);
            }
          }

          iter++;
        }

        if (clusterCnt == 0)
          System.out.println("  No clusters reported.");

        System.out.println("\nQuery complete.");
      }
      else if (command.equals("list") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cells"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        UUID transID = commandInjector.injectAsTransaction(
            new DaemonCmdStim(DaemonCmdType.DISCOVER_CELL,
                              daemonName,
                              clusterName,
                              "NA",
                              "NA"));

        System.out.println("Cells:");
        int iter = 0;
        int cellCnt = 0;
        while (iter < 10)
        {
          DaemonCmdRespStim response =
              responseExtractor.extractFromTransaction(transID, 1000);

          if (response != null)
          {
            cellCnt += response.records.size();
            for (DaemonCellInfo ci : response.records)
            {
                System.out.println("  " + ci.cellName + " v" + ci.daemonVersion + " " + ci.cellType + " " + ci.cellState + " L:" + toB(ci.loggingEnabled) + " T:" + toB(ci.traceLoggingEnabled) + " D:" + toB(ci.debugLoggingEnabled) + " R:" + toB(ci.runtimeLoggingEnabled));
            }
          }

          iter++;
        }

        if (cellCnt == 0)
          System.out.println("  No cells reported.");

        System.out.println("\nQuery complete.");
      }
      else if (command.equals("deploy") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterDef = arguments.get("opt5");

        UUID transID = commandInjector.injectAsTransaction(
            new DaemonCmdStim(DaemonCmdType.DEPLOY_CLUSTER,
                              daemonName,
                              "NA",
                              "NA",
                              clusterDef));

        DaemonCmdRespStim response =
            responseExtractor.extractFromTransaction(transID, 10_000);

        if (response == null)
        {
          System.out.println("ERROR: Daemon unresponsive.");
        }
        else if (response.succeeded)
        {
          System.out.println("Command successful");
        }
        else
        {
          System.out.println("ERROR: " + response.errorInfo);
        }
      }
      else if (command.equals("start") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.START_CLUSTER,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("pause") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.PAUSE_CLUSTER,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("resume") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.RESUME_CLUSTER,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("stop") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.STOP_CLUSTER,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("publish") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.PUBLISH_CLUSTER,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("recall") &&
               optCnt >= 5 &&
               arguments.get("opt3").equals("cluster"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.RECALL_CLUSTER,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("start") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cell"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");
        String cellName = arguments.get("opt6");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.START_CELL,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("pause") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cell"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");
        String cellName = arguments.get("opt6");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.PAUSE_CELL,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("resume") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cell"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");
        String cellName = arguments.get("opt6");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.RESUME_CELL,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("stop") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cell"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");
        String cellName = arguments.get("opt6");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.STOP_CELL,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("publish") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cell"))
      {
        String daemonName = arguments.get("opt4");
        String clusterName = arguments.get("opt5");
        String cellName = arguments.get("opt6");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.PUBLISH_CELL,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("enable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("logging"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.ENABLE_CLUSTER_LOGGING,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("disable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("logging"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.DISABLE_CLUSTER_LOGGING,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("enable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("trace"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.ENABLE_CLUSTER_TRACE,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("disable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("trace"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.DISABLE_CLUSTER_TRACE,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("enable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("debug"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.ENABLE_CLUSTER_DEBUG,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("disable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("debug"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.DISABLE_CLUSTER_DEBUG,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("enable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("runtime"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.ENABLE_CLUSTER_RUNTIME,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("disable") &&
               optCnt >= 6 &&
               arguments.get("opt3").equals("cluster") &&
               arguments.get("opt4").equals("runtime"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");

        executeClusterCmd(commandInjector,
                          responseExtractor,
                          DaemonCmdType.DISABLE_CLUSTER_RUNTIME,
                          daemonName,
                          clusterName);
      }
      else if (command.equals("enable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("logging"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.ENABLE_CELL_LOGGING,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("disable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("logging"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.DISABLE_CELL_LOGGING,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("enable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("trace"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.ENABLE_CELL_TRACE,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("disable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("trace"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.DISABLE_CELL_TRACE,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("enable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("debug"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.ENABLE_CELL_DEBUG,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("disable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("debug"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.DISABLE_CELL_DEBUG,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("enable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("runtime"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.ENABLE_CELL_RUNTIME,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else if (command.equals("disable") &&
               optCnt >= 7 &&
               arguments.get("opt3").equals("cell") &&
               arguments.get("opt4").equals("runtime"))
      {
        String daemonName = arguments.get("opt5");
        String clusterName = arguments.get("opt6");
        String cellName = arguments.get("opt7");

        executeCellCmd(commandInjector,
                       responseExtractor,
                       DaemonCmdType.DISABLE_CELL_RUNTIME,
                       daemonName,
                       clusterName,
                       cellName);
      }
      else
      {
        System.out.println("ERROR: Illegal command.");
        printUsage();
      }

      // Give cluster time to process final events
      try {Thread.sleep(1000);} catch (InterruptedException ie) { /* ignore */ }
      
      // Stop the cells
      commandInjector.stop();
      responseExtractor.stop();
      commandRouter.stop();

      // Give cluster time to process final events
      try {Thread.sleep(1000);} catch (InterruptedException ie) { /* ignore */ }

      System.exit(0);      
    }
    catch (NeuPathsException bre)
    {
      System.out.println("ERROR: Session failed: " + bre);
      System.exit(1);
    }

    System.exit(0);
  }

  private static String toB (boolean b)
  {
    if (b)
      return "T";
    else
      return "F";
  }

  private static void executeClusterCmd (InjectorCell  injector,
                                         ExtractorCell extractor,
                                         DaemonCmdType command,
                                         String        daemonName,
                                         String        clusterName)
  {
    UUID transID = injector.injectAsTransaction(
        new DaemonCmdStim(command,
                          daemonName,
                          clusterName,
                          "NA",
                          "NA"));

    DaemonCmdRespStim response = extractor.extractFromTransaction(transID, 10_000);

    if (response == null)
    {
      System.out.println("ERROR: Daemon unresponsive.");
    }
    else if (response.succeeded)
    {
      System.out.println("Command successful");
    }
    else
    {
      System.out.println("ERROR: " + response.errorInfo);
    }
  }

  private static void executeCellCmd (InjectorCell  injector,
                                      ExtractorCell extractor,
                                      DaemonCmdType command,
                                      String        daemonName,
                                      String        clusterName,
                                      String        cellName)
  {
    UUID transID = injector.injectAsTransaction(
        new DaemonCmdStim(command,
                          daemonName,
                          clusterName,
                          cellName,
                          "NA"));

    DaemonCmdRespStim response = extractor.extractFromTransaction(transID, 10_000);

    if (response == null)
    {
      System.out.println("ERROR: Daemon unresponsive.");
    }
    else if (response.succeeded)
    {
      System.out.println("Command successful");
    }
    else
    {
      System.out.println("ERROR: " + response.errorInfo);
    }
  }
}
