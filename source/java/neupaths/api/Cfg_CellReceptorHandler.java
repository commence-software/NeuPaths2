// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for parsing an {@link Activator}s receptor configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellReceptorHandler extends Cfg_ConfigHandler
{
  Cfg_CellReceptorHandler (String xPath)
  {
    super(xPath);
    
    name = null;
    mode = ReceptorMode.NON_BUFFERED;
    stimulusClassName = null;
  }

  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("MODE"))
    {
      try
      {
        mode = ReceptorMode.valueOf(Cfg_Utils.getNodeText(node).toUpperCase());
      }
      catch (IllegalArgumentException iae)
      {
        throw new NeuPathsException("Illegal value for " + xPath + "/Mode '" +
                                    Cfg_Utils.getNodeText(node) + "' in file " + configFile);
      }
    }
    else if (nodeName.equals("STIMULUS_CLASS"))
    {
      stimulusClassName = Cfg_Utils.getNodeText(node);
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                     nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_ReceptorHandlerInt handler = (Cfg_ReceptorHandlerInt) parentHandler;

    handler.addReceptor(this);
  }

  String name;
  ReceptorMode mode;
  String stimulusClassName;
}
