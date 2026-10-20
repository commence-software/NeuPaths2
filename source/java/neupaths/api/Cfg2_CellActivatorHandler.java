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
final class Cfg2_CellActivatorHandler extends Cfg2_ConfigHandler
    implements Cfg2_ReceptorsHandlerInt,
               Cfg2_TransmittersHandlerInt,
               Cfg2_SubscriptionsHandlerInt
{
  Cfg2_CellActivatorHandler (CellType cellType, String xPath)
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

    if (nodeName.equals("NAME"))
    {
      name = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("CLASS"))
    {
      className = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("RECEPTORS"))
    {
      Cfg2_CellReceptorsHandler handler = new Cfg2_CellReceptorsHandler(cellType, xPath + "/Receptors");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("TRANSMITTERS"))
    {
      Cfg2_CellTransmittersHandler handler = new Cfg2_CellTransmittersHandler(cellType, xPath + "/Transmitters");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("SUBSCRIPTIONS"))
    {
      Cfg2_CellSubscriptionsHandler handler = new Cfg2_CellSubscriptionsHandler(cellType, xPath + "/Subscriptions");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("TRANSACTION_HISTORY_WINDOW_MS"))
    {
      transactionHistoryWindow = Long.parseLong(Cfg2_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell/Activators/Activator configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String             definitionFile,
                       Cfg2_ConfigHandler parentHandler)
  {
    Cfg2_ActivatorsHandlerInt handler = (Cfg2_ActivatorsHandlerInt) parentHandler;
    handler.addActivator(this);
  }

  public void addReceptor (Cfg2_CellReceptorHandler receptor)
  {
    receptors.add(receptor);
  }
  
  public void addTransmitter (Cfg2_CellTransmitterHandler transmitter)
  {
    transmitters.add(transmitter);
  }
  
  public void addSubscription (Cfg2_CellSubscriptionHandler subscription)
  {
    subscriptions.add(subscription);
  }
  
  String name;
  String className;
  LinkedList<Cfg2_CellReceptorHandler> receptors;
  LinkedList<Cfg2_CellTransmitterHandler> transmitters;
  LinkedList<Cfg2_CellSubscriptionHandler> subscriptions;
  long transactionHistoryWindow;
}
