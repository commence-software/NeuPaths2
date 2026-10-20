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
final class Cfg2_CellActivatorsHandler extends Cfg2_ConfigHandler
    implements Cfg2_ActivatorsHandlerInt
{
  Cfg2_CellActivatorsHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    activators = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("ACTIVATOR"))
    {
      Cfg2_CellActivatorHandler handler = new Cfg2_CellActivatorHandler(cellType, xPath + "/Activator");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
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
    Cfg2_ActivatorsHandlerInt handler = (Cfg2_ActivatorsHandlerInt) parentHandler;
    
    for (Cfg2_CellActivatorHandler activator : activators)
    {
      handler.addActivator(activator);
    }
  }

  public void addActivator (Cfg2_CellActivatorHandler activator)
  {
    activators.add(activator);
  }

  LinkedList<Cfg2_CellActivatorHandler> activators;
}
