// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for {@link Cell} transmitters.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellTransmittersHandler extends Cfg_ConfigHandler
    implements Cfg_TransmittersHandlerInt
{
  Cfg_CellTransmittersHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);

    transmitters = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("TRANSMITTER"))
    {
      Cfg_CellTransmitterHandler handler = new Cfg_CellTransmitterHandler(cellType, xPath + "/Transmitter");
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
    Cfg_TransmittersHandlerInt handler = (Cfg_TransmittersHandlerInt) parentHandler;
    
    for (Cfg_CellTransmitterHandler transmitter : transmitters)
    {
      handler.addTransmitter(transmitter);
    }
  }

  public void addTransmitter (Cfg_CellTransmitterHandler transmitter)
  {
    transmitters.add(transmitter);
  }
  
  LinkedList<Cfg_CellTransmitterHandler> transmitters;
}
