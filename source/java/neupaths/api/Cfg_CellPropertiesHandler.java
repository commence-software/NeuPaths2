// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing {@link Cell} properties.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellPropertiesHandler extends Cfg_ConfigHandler
    implements Cfg_PropertiesHandlerInt
{
  Cfg_CellPropertiesHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    properties = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("PROPERTY"))
    {
      Cfg_CellPropertyHandler handler = new Cfg_CellPropertyHandler(cellType, xPath + "/Property");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String             definitionFile,
                       Cfg_ConfigHandler parentHandler)
  {
    Cfg_PropertiesHandlerInt handler = (Cfg_PropertiesHandlerInt) parentHandler;
    
    for (Cfg_CellPropertyHandler property : properties)
    {
      handler.addProperty(property);
    }
  }
  
  public void addProperty (Cfg_CellPropertyHandler property)
  {
    properties.add(property);
  }
  
  LinkedList<Cfg_CellPropertyHandler> properties;
}
