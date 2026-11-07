// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.io.File;

public final class AnalyzeModel
{
  private AnalyzeModel ()
  {
    // Construction is not necessary
  }

  private static void printUsage ()
  {
    System.out.println("usage: java -classpath neupaths.jar neupaths.api.AnalyzeModel <s|c> <configTopDir> <defPathname>");
    System.out.println("    s             Cell System definition");
    System.out.println("    c             Cell Cluster definition");
    System.out.println("    configTopDir  Pathname of the top-level directory for the definition files");
    System.out.println("    defPathname   Pathname of system or cluster definition file relative to configTopDir");
  }
  
  public static void main (String args[])
  {
    if (args.length != 3)
    {
      printUsage();
      System.exit(1);
    }

    String definitionFile = args[1] + File.separator + args[2];
    
    LinkedHashMap<String, Mdl_Cell> mdlCells = null;
    LinkedHashSet<Mdl_Flow> mdlFlows = null;
    LinkedHashSet<Mdl_Syn> mdlSyns = null;
  
    if (args[0].toLowerCase().equals("s"))
    {
      Cfg_CellSystemParser parser = new Cfg_CellSystemParser(definitionFile);
      parser.validate();

      Mdl_System system = new Mdl_System(parser.handler.name);
      parser.model(system.cells, system.flows, system.syns);

      mdlCells = system.cells;
      mdlFlows = system.flows;
      mdlSyns = system.syns;
    }
    else if (args[0].toLowerCase().equals("c"))
    {
      Cfg_CellClusterParser parser = new Cfg_CellClusterParser(definitionFile);
      parser.validate();

      Mdl_Cluster cluster = new Mdl_Cluster(parser.handler.name);
      parser.model(cluster.cells, cluster.flows, cluster.syns);

      mdlCells = cluster.cells;
      mdlFlows = cluster.flows;
      mdlSyns = cluster.syns;
    }
    else
    {
      System.out.println("ERROR: Invalid definition type " + args[0]);
      printUsage();
      System.exit(1);
    }
    
    System.out.println("{");
    System.out.println("  \"Format\": \"neupaths-topology/1\",");
    System.out.format ("  \"Generated\": \"%s\",\n", new Date());
    System.out.format ("  \"Source\": \"%s\",\n", definitionFile);
    System.out.println("");

    System.out.println("  \"Cells\": [");

    boolean firstCell = true;
    for (Mdl_Cell cell : mdlCells.values())
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
    for (Mdl_Flow flow : mdlFlows)
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
    for (Mdl_Syn syn : mdlSyns)
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
