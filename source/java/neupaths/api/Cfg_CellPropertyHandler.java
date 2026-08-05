// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Handler for an {@link Cell} property.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellPropertyHandler extends Cfg_ConfigHandler
{
  Cfg_CellPropertyHandler (String xPath)
  {
    super(xPath);
    
    name = null;
    value = null;
  }
  
  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("VALUE"))
    {
      value = Cfg_Utils.getNodeText(node);
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_CellPropertiesHandler handler = (Cfg_CellPropertiesHandler) parentHandler;
    handler.properties.add(this);
  }
  
  String name;
  String value;
}
