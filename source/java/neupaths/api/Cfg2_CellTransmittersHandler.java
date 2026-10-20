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
final class Cfg2_CellTransmittersHandler extends Cfg2_ConfigHandler
    implements Cfg2_TransmittersHandlerInt
{
  Cfg2_CellTransmittersHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);

    transmitters = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("TRANSMITTER"))
    {
      Cfg2_CellTransmitterHandler handler = new Cfg2_CellTransmitterHandler(cellType, xPath + "/Transmitter");
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
    Cfg2_TransmittersHandlerInt handler = (Cfg2_TransmittersHandlerInt) parentHandler;
    
    for (Cfg2_CellTransmitterHandler transmitter : transmitters)
    {
      handler.addTransmitter(transmitter);
    }
  }

  public void addTransmitter (Cfg2_CellTransmitterHandler transmitter)
  {
    transmitters.add(transmitter);
  }
  
  LinkedList<Cfg2_CellTransmitterHandler> transmitters;
}
