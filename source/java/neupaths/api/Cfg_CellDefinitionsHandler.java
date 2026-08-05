// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing list of cell definition file names.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellDefinitionsHandler extends Cfg_ConfigHandler
{
  Cfg_CellDefinitionsHandler (String xPath)
  {
    super(xPath);

    cellDefinitionFiles = new LinkedList<>();
  }

  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("CELL_DEFINITION"))
    {
      cellDefinitionFiles.addLast(Cfg_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell_Cluster/Cell_Definitions configuration tag '" +
                                     nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_CellClusterHandler handler = (Cfg_CellClusterHandler) parentHandler;
    handler.cellDefinitionFiles = cellDefinitionFiles;
  }
  
  LinkedList<String> cellDefinitionFiles;
}
