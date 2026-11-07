// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.io.File;

/**
 * Produces a <i>Dataflow</i> and <i>System</i> model for a cellular system.
 * The model is spooled to standard output as JSON.
 * <p>
 * The cellular system is specified in a system definition file, which
 * contains an XML schema listing the cluster definition files for the
 * system.
 * </p>
 * <ul>
 * <pre>
 * {@literal <cell_system>}
 * {@literal     <name></name>}
 * {@literal     <cluster_definitions>}
 * {@literal         <cluster_definition></cluster_definition>}
 * {@literal         ...}
 * {@literal     </cluster_definitions>}
 * {@literal </cell_system>}</pre>
 * </ul>
 * <p>
 * Each {@literal <cluster_definition>} is the pathname of a cluster definition
 * file as detailed in {@link CellCluster}.
 * </p>
 * <p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.api.AnalyzeCellSystem <systemDefinitionFile> <configTopDir> >}</pre>
 * <p>
 * <i>systemDefinitionFile</i>: Pathname of the system definition file.
 * </p>
 * <p>
 * <i>configTopDir</i>: Pathname of the top-level directory for the cluster
 * and cell definition files.
 * </p>
 * </ul>
 * </p>
 * 
 * @author Aaron Caraveo
 */
public final class AnalyzeCellSystem
{
  private AnalyzeCellSystem ()
  {
    // Construction is not necessary
  }

  private static void printUsage ()
  {
    System.out.println("usage: java -classpath neupaths.jar neupaths.api.AnalyzeCellSystem <systemDefinitionFile> <configTopDir>");
    System.out.println("    systemDefinitionFile    Pathname of the system definition file.");
    System.out.println("    configTopDir            Pathname of the top-level directory for the cluster and cell definition files");
  }
  
  public static void main (String args[])
  {
    if (args.length != 2)
    {
      printUsage();
      System.exit(1);
    }

    Cfg_CellSystemParser parser = new Cfg_CellSystemParser(args[0], args[1]);
    parser.validate();

    Mdl_System system = new Mdl_System(parser.handler.name);
    parser.model(system.cells, system.flows, system.syns);

    System.out.println("{");
    System.out.println("  \"Format\": \"neupaths-topology/1\",");
    System.out.format ("  \"Generated\": \"%s\",\n", new Date());
    System.out.format ("  \"Source\": \"%s\",\n", args[0]);
    System.out.println("");

    System.out.println("  \"Cells\": [");

    boolean firstCell = true;
    for (Mdl_Cell cell : system.cells.values())
    {
      if (firstCell)
      {
        System.out.format("      { \"Name\": \"%s\", \"Type\": \"%s\" }\n", cell.name, cell.type);
        firstCell = false;
      }
      else
      {
        System.out.format("    , { \"Name\": \"%s\", \"Type\": \"%s\" }\n", cell.name, cell.type);
      }
    }
    System.out.println("  ],");
    System.out.println("");
    
    System.out.println("  \"Dataflow\": [");

    boolean firstFlow = true;
    for (Mdl_Flow flow : system.flows)
    {
      if (firstFlow)
      {
        System.out.format("      { \"From\": \"%s\", \"Transmitter\": \"%s\",\n", flow.src, flow.tx);
        firstFlow = false;
      }
      else
      {
        System.out.format("    , { \"From\": \"%s\", \"Transmitter\": \"%s\",\n", flow.src, flow.tx);
      }

      System.out.format("        \"To\": \"%s\", \"Receptor\": \"%s\",\n", flow.dst, flow.rx);
      System.out.format("        \"Domain\": \"%s\", \"Activator\": \"%s\" }\n", flow.dmn, flow.act);
    }
    
    System.out.println("  ],");
    System.out.println("");

    System.out.println("  \"Synapses\": [");

    boolean firstSyn = true;
    for (Mdl_Syn syn : system.syns)
    {
      if (firstSyn)
      {
        System.out.format("      { \"Name\": \"%s\",\n", syn.name);
        firstSyn = false;
      }
      else
      {
        System.out.format("    , { \"Name\": \"%s\",\n", syn.name);
      }
      
      System.out.format("        \"Scope\": \"%s\", \"Type\": \"%s\",\n", syn.scope, syn.type);
      System.out.format("        \"Domain\": \"%s\", \"Address\": \"%s\",\n", syn.domain, syn.address);
      System.out.format("        \"Listener\": \"%s\",\n", syn.listener);
      System.out.println("        \"Peers\": [");
  
      boolean firstPeer = true;
      for (String peer : syn.peers)
      {
        if (firstPeer)
        {
          System.out.format("            \"%s\"\n", peer);
          firstPeer = false;
        }
        else
        {
          System.out.format("          , \"%s\"\n", peer);
        }
      }
      
      System.out.println("        ]");
      System.out.println("      }");
    }

    System.out.println("  ]");
    System.out.println("}");
  }
}
