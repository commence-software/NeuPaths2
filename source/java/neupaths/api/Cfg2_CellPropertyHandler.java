// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Handler for a {@link Cell} property.
 *
 * @author Aaron Caraveo
 */
final class Cfg2_CellPropertyHandler extends Cfg2_ConfigHandler
{
  Cfg2_CellPropertyHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    name = null;
    value = null;
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("NAME"))
    {
      name = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("VALUE"))
    {
      value = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("TYPE"))
    {
      type = Cfg2_Utils.getNodeText(node);
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
    Cfg2_PropertiesHandlerInt handler = (Cfg2_PropertiesHandlerInt) parentHandler;
    handler.addProperty(this);
  }
  
  String name;
  String value;
  String type;
}
