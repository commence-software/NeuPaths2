// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell}'s properties.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellPropertiesHandler extends Cfg_ConfigHandler
{
  Cfg_CellPropertiesHandler (String xPath)
  {
    super(xPath);
    
    properties = new LinkedList<>();
  }
  
  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("PROPERTY"))
    {
      Cfg_CellPropertyHandler handler = new Cfg_CellPropertyHandler(xPath + "/Property");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_CellHandler handler = (Cfg_CellHandler) parentHandler;
    
    for (Cfg_CellPropertyHandler context : properties)
    {
      handler.properties.set(context.name, context.value);
    }
  }
  
  LinkedList<Cfg_CellPropertyHandler> properties;
}
