// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell} activator configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellActivatorsHandler extends Cfg_ConfigHandler
    implements Cfg_ActivatorsHandlerInt
{
  Cfg_CellActivatorsHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    activators = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("ACTIVATOR"))
    {
      Cfg_CellActivatorHandler handler = new Cfg_CellActivatorHandler(cellType, xPath + "/Activator");
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
    Cfg_ActivatorsHandlerInt handler = (Cfg_ActivatorsHandlerInt) parentHandler;
    
    for (Cfg_CellActivatorHandler activator : activators)
    {
      handler.addActivator(activator);
    }
  }

  public void addActivator (Cfg_CellActivatorHandler activator)
  {
    activators.add(activator);
  }

  LinkedList<Cfg_CellActivatorHandler> activators;
}
