// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import neupaths.api.*;

/**
 * Utility that creates and starts a NeuPaths cell cluster.
 * <p>
 * This utility creates a cell cluster according to the specified cluster
 * definition file and starts it.  The utility will not terminate unless
 * an error is encountered or the user manually terminates.
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar:<other_jars_etc> neupaths.util.ClusterCellExec [-d|--dir <configTopDir>] <clusterDefinitionFile>}</pre>
 * <p>
 * <i>configTopDir</i>: Pathname of the top-level directory for the cell
 * definition files.  When not specified, current working directory is used.
 * </p>
 * <p>
 * <i>clusterDefinitionFile</i>: Pathname of the cluster definition file.  See {@link neupaths.api.CellCluster} for a description of cluster definition files.
 * </p>
 * </ul>
 *
 * @author Aaron Caraveo
 */
public class CellClusterExec
{
  private CellClusterExec ()
  {
    // Construction not necessary
  }

  private static void printUsage()
  {
    System.out.println("usage: java -classpath neupaths.jar:<other_jars_etc> neupaths.util.CellClusterExec [-d|--dir <configTopDir>] <clusterDefinitionFile>\n");
    System.out.println("    configTopDir            Pathname of the top-level");
    System.out.println("                            directory for the cell");
    System.out.println("                            definition files.");
    System.out.println("    clusterDefinitionFile   Pathname of the cluster");
    System.out.println("                            definition file.");
  }
  
  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
    PropertySet arguments = CommandLine.parse(args, new String[] {"d", "dir"});
    String configTopDir = null;
    String clusterDef = null;

    if (arguments == null)
    {
      System.out.println("ERROR: Invalid command-line flag");
      printUsage();
      System.exit(1);
    }

    if (arguments.contains("d") || arguments.contains("dir"))
    {
      if (arguments.contains("d"))
        configTopDir = arguments.get("d");
      else
        configTopDir = arguments.get("dir");
    }

    int optCnt = arguments.get("optCnt");
    
    if (optCnt != 1)
    {
      printUsage();
      System.exit(1);
    }

    clusterDef = arguments.get("opt1");
    
    try
    {
      CellCluster cluster = new CellCluster(clusterDef, configTopDir);

      cluster.start();

      while (true)
      {
        try {Thread.sleep(10_000);} catch (InterruptedException ie) {/*ignore*/}
      }
    }
    catch (NeuPathsException bre)
    {
      System.out.println("ERROR: Failed to create cluster: " + bre);
      System.exit(1);
    }

    System.exit(0);
  }
}
