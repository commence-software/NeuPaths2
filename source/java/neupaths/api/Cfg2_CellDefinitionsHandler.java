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
final class Cfg2_CellDefinitionsHandler extends Cfg2_ConfigHandler
{
  Cfg2_CellDefinitionsHandler (String xPath)
  {
    super(null, xPath);

    cellDefinitionFiles = new LinkedList<>();
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("CELL_DEFINITION"))
    {
      cellDefinitionFiles.add(Cfg2_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell_Cluster/Cell_Definitions configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String             definitionFile,
                       Cfg2_ConfigHandler parentHandler)
  {
    Cfg2_CellClusterHandler handler = (Cfg2_CellClusterHandler) parentHandler;
    handler.cellDefinitionFiles = cellDefinitionFiles;
  }
  
  LinkedList<String> cellDefinitionFiles;
}
