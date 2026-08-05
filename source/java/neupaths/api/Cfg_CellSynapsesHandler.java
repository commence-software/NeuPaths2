// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashSet;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell}'s synapse configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellSynapsesHandler extends Cfg_ConfigHandler
{
  Cfg_CellSynapsesHandler (String xPath)
  {
    super(xPath);
    
    synapseNames = new HashSet<>();
  }
  
  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("SYNAPSE"))
    {
      synapseNames.add(Cfg_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_SynapsesHandlerInt handler = (Cfg_SynapsesHandlerInt) parentHandler;
    handler.addSynapses(synapseNames);
  }

  private HashSet<String> synapseNames;
}
