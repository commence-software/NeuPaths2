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
final class Cfg_CellActivatorHandler extends Cfg_ConfigHandler
    implements Cfg_ReceptorsHandlerInt,
               Cfg_TransmittersHandlerInt,
               Cfg_SubscriptionsHandlerInt
{
  Cfg_CellActivatorHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);

    name = null;
    className = null;
    receptors = new LinkedList<>();
    transmitters = new LinkedList<>();
    subscriptions = new LinkedList<>();
    transactionHistoryWindow = 30_000L;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("CLASS"))
    {
      className = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("RECEPTORS"))
    {
      Cfg_CellReceptorsHandler handler = new Cfg_CellReceptorsHandler(cellType, xPath + "/Receptors");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("TRANSMITTERS"))
    {
      Cfg_CellTransmittersHandler handler = new Cfg_CellTransmittersHandler(cellType, xPath + "/Transmitters");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("SUBSCRIPTIONS"))
    {
      Cfg_CellSubscriptionsHandler handler = new Cfg_CellSubscriptionsHandler(cellType, xPath + "/Subscriptions");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("TRANSACTION_HISTORY_WINDOW_MS"))
    {
      transactionHistoryWindow = Long.parseLong(Cfg_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell/Activators/Activator configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String             definitionFile,
                       Cfg_ConfigHandler parentHandler)
  {
    Cfg_ActivatorsHandlerInt handler = (Cfg_ActivatorsHandlerInt) parentHandler;
    handler.addActivator(this);
  }

  public void addReceptor (Cfg_CellReceptorHandler receptor)
  {
    receptors.add(receptor);
  }
  
  public void addTransmitter (Cfg_CellTransmitterHandler transmitter)
  {
    transmitters.add(transmitter);
  }
  
  public void addSubscription (Cfg_CellSubscriptionHandler subscription)
  {
    subscriptions.add(subscription);
  }
  
  String name;
  String className;
  LinkedList<Cfg_CellReceptorHandler> receptors;
  LinkedList<Cfg_CellTransmitterHandler> transmitters;
  LinkedList<Cfg_CellSubscriptionHandler> subscriptions;
  long transactionHistoryWindow;
}
