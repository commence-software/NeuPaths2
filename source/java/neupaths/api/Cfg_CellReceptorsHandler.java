// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for {@link Cell} receptors.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellReceptorsHandler extends Cfg_ConfigHandler
    implements Cfg_ReceptorsHandlerInt
{
  Cfg_CellReceptorsHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);

    receptors = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("RECEPTOR"))
    {
      Cfg_CellReceptorHandler handler = new Cfg_CellReceptorHandler(cellType, xPath + "/Receptor");
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
    Cfg_ReceptorsHandlerInt handler = (Cfg_ReceptorsHandlerInt) parentHandler;
    
    for (Cfg_CellReceptorHandler receptor : receptors)
    {
      handler.addReceptor(receptor);
    }
  }

  public void addReceptor (Cfg_CellReceptorHandler receptor)
  {
    receptors.add(receptor);
  }
  
  LinkedList<Cfg_CellReceptorHandler> receptors;
}
