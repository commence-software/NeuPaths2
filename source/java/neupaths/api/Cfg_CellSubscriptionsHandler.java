// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a subscription configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellSubscriptionsHandler extends Cfg_ConfigHandler implements Cfg_SubscriptionHandlerInt
{
  Cfg_CellSubscriptionsHandler (String xPath, CellType cellType)
  {
    super(xPath);
    
    this.cellType = cellType;
    subscriptions = new LinkedList<>();
  }
  
  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("SUBSCRIPTION"))
    {
      SubscriptionType subType = SubscriptionType.LOGIC;
      
      switch (cellType)
      {
        case BRIDGE:
          subType = SubscriptionType.BRIDGE;
          break;
        case EXTRACTOR:
          subType = SubscriptionType.EXTRACTOR;
          break;
        case LOAD_CONTROLLER:
          subType = SubscriptionType.LOGIC;
          break;
        default:
          throw new NeuPathsException("Invalid cell type " + cellType + " for subscription in file " + configFile);
      }
      
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(xPath + "/Subscription", subType);
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("BRIDGE_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(xPath + "/Bridge_Subscription", SubscriptionType.BRIDGE);
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("EXTRACTOR_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(xPath + "/Extractor_Subscription", SubscriptionType.EXTRACTOR);
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("LOGIC_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(xPath + "/Logic_Subscription", SubscriptionType.LOGIC);
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("LOOPBACK_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(xPath + "/Loopback_Subscription", SubscriptionType.LOOPBACK);
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("MAP_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(xPath + "/Map_Subscription", SubscriptionType.MAP);
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
    Cfg_SubscriptionsHandlerInt handler = (Cfg_SubscriptionsHandlerInt) parentHandler;
    
    for (Cfg_CellSubscriptionHandler context : subscriptions)
    {
      switch (context.subType)
      {
        case BRIDGE:
          handler.addSubscription(
              new BridgeSubscriptionSpec(context.cellName,
                                         context.transmitterName,
                                         context.domain));
          break;
        case EXTRACTOR:
          handler.addSubscription(
              new ExtractorSubscriptionSpec(context.cellName,
                                            context.transmitterName,
                                            context.domain,
                                            context.filterTransactions));
          break;
        case LOGIC:
          handler.addSubscription(
              new LogicSubscriptionSpec(context.cellName,
                                        context.transmitterName,
                                        context.receptorName,
                                        context.domain,
                                        context.filterTransactions));
          break;
        case LOOPBACK:
          handler.addSubscription(
              new LogicLoopbackSubscriptionSpec(context.transmitterName,
                                                context.receptorName));
          break;
        case MAP:
          handler.addSubscription(
              new LogicMapSubscriptionSpec(context.cellName,
                                           context.transmitterName,
                                           context.domain,
                                           context.filterTransactions));
          break;
      }
    }
  }

  public void addSubscription (Cfg_CellSubscriptionHandler subscription)
  {
    subscriptions.add(subscription);
  }
  
  CellType cellType;
  LinkedList<Cfg_CellSubscriptionHandler> subscriptions;
}
