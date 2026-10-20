// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for parsing an {@link Activator} receptor configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg2_CellReceptorHandler extends Cfg2_ConfigHandler
{
  Cfg2_CellReceptorHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    name = null;
    mode = ReceptorMode.NON_BUFFERED;
    stimulusClassName = null;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("NAME"))
    {
      name = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("MODE"))
    {
      try
      {
        mode = ReceptorMode.valueOf(Cfg2_Utils.getNodeText(node).toUpperCase());
      }
      catch (IllegalArgumentException iae)
      {
        throw new NeuPathsException("Illegal value for " + xPath + "/Mode '" +
                                    Cfg2_Utils.getNodeText(node) + "' in file " + definitionFile);
      }
    }
    else if (nodeName.equals("STIMULUS_CLASS"))
    {
      stimulusClassName = Cfg2_Utils.getNodeText(node);
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
    Cfg2_ReceptorsHandlerInt handler = (Cfg2_ReceptorsHandlerInt) parentHandler;

    handler.addReceptor(this);
  }

  String name;
  ReceptorMode mode;
  String stimulusClassName;
}
