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
final class Cfg_CellSubscriptionsHandler extends Cfg_ConfigHandler
    implements Cfg_SubscriptionsHandlerInt
{
  Cfg_CellSubscriptionsHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);

    subscriptions = new LinkedList<>();
  }
  
  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    SubscriptionType subType = SubscriptionType.LOGIC;
    
    if (nodeName.equals("SUBSCRIPTION"))
    {
      switch (cellType)
      {
        case BRIDGE:
          subType = SubscriptionType.BRIDGE;
          break;
        case EXTRACTOR:
          subType = SubscriptionType.EXTRACTOR;
          break;
        case LOAD_BALANCED:
        case LOAD_CONTROLLER:
        case LOGIC:
        case CUSTOM:
          subType = SubscriptionType.LOGIC;
          break;
      }
      
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Subscription", subType);
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("BRIDGE_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Bridge_Subscription", SubscriptionType.BRIDGE);
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("EXTRACTOR_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Extractor_Subscription", SubscriptionType.EXTRACTOR);
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("LOGIC_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Logic_Subscription", SubscriptionType.LOGIC);
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("LOOPBACK_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Loopback_Subscription", SubscriptionType.LOOPBACK);
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("MAP_SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Map_Subscription", SubscriptionType.MAP);
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
    Cfg_SubscriptionsHandlerInt handler = (Cfg_SubscriptionsHandlerInt) parentHandler;
    
    for (Cfg_CellSubscriptionHandler subscription : subscriptions)
    {
      handler.addSubscription(subscription);
    }
  }

  public void addSubscription (Cfg_CellSubscriptionHandler subscription)
  {
    subscriptions.add(subscription);
  }
  
  LinkedList<Cfg_CellSubscriptionHandler> subscriptions;
}
