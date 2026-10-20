// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashSet;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell} synapse configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg2_CellSynapsesHandler extends Cfg2_ConfigHandler
{
  Cfg2_CellSynapsesHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    synapseNames = new HashSet<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("SYNAPSE"))
    {
      synapseNames.add(Cfg2_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String             definitionFile,
                       Cfg2_ConfigHandler parentHandler)
  {
    Cfg2_SynapsesHandlerInt handler = (Cfg2_SynapsesHandlerInt) parentHandler;

    for (String synapseName : synapseNames)
    {
      handler.addSynapse(synapseName);
    }
  }

  HashSet<String> synapseNames;
}
