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
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.util.ClusterCellExec <clusterDefinitionFile>}</pre>
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

  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
    if (args.length != 1)
    {
      System.out.println("usage: java -classpath neupaths.jar neupaths.util.CellClusterExec <clusterDefinitionFile>\n");
      System.out.println("    clusterDefinitionFile      Pathname of the cluster");
      System.out.println("                               definition file.");
      System.exit(1);
    }

    try
    {
      CellCluster cluster = new CellCluster(args[0]);

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
