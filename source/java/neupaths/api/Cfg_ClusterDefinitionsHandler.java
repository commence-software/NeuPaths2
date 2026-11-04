// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing list of cluster definition file names.
 *
 * @author Aaron Caraveo
 */
final class Cfg_ClusterDefinitionsHandler extends Cfg_ConfigHandler
{
  Cfg_ClusterDefinitionsHandler (String xPath)
  {
    super(null, xPath);

    clusterDefinitionFiles = new LinkedList<>();
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("CLUSTER_DEFINITION"))
    {
      clusterDefinitionFiles.add(Cfg_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell_System/Cluster_Definitions configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String            definitionFile,
                       Cfg_ConfigHandler parentHandler)
  {
    Cfg_CellSystemHandler handler = (Cfg_CellSystemHandler) parentHandler;
    handler.clusterDefinitionFiles = clusterDefinitionFiles;
  }
  
  LinkedList<String> clusterDefinitionFiles;
}
