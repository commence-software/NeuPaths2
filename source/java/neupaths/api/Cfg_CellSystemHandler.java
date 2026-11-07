// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for a Cell_System configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellSystemHandler extends Cfg_ConfigHandler
{
  Cfg_CellSystemHandler ()
  {
    super(null, "Cell_System");

    name = null;
    clusterDefinitionFiles = null;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("CLUSTER_DEFINITIONS"))
    {
      Cfg_ClusterDefinitionsHandler handler = new Cfg_ClusterDefinitionsHandler(xPath + "/Cluster_Definitions");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell_System configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }

  String name;
  LinkedList<String> clusterDefinitionFiles;
}
