// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell} subscription.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellSubscriptionHandler extends Cfg_ConfigHandler
{
  Cfg_CellSubscriptionHandler (CellType         cellType,
                                String           xPath,
                                SubscriptionType subType)
  {
    super(cellType, xPath);
    
    this.subType = subType;
    
    cellName = "Unspecified";
    transmitterName = "Unspecified";
    receptorName = "Unspecified";
    domain = "Unspecified";
    filterTransactions = TransactionFilter.DISABLED;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("CELL_NAME") && subType != SubscriptionType.LOOPBACK)
    {
      cellName = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("TRANSMITTER_NAME"))
    {
      transmitterName = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("RECEPTOR_NAME") &&
             subType != SubscriptionType.BRIDGE &&
             subType != SubscriptionType.EXTRACTOR &&
             subType != SubscriptionType.MAP)
    {
      receptorName = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("DOMAIN") && subType != SubscriptionType.LOOPBACK)
    {
      domain = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("FILTER_TRANSACTIONS") &&
             subType != SubscriptionType.BRIDGE)
    {
      try
      {
        filterTransactions = TransactionFilter.valueOf(Cfg_Utils.getNodeText(node).toUpperCase());
      }
      catch (IllegalArgumentException iae)
      {
        throw new NeuPathsException("Illegal value for " + xPath + "/Filter_Transactions '" +
                                    Cfg_Utils.getNodeText(node) + "' in file " + definitionFile);
      }
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
    handler.addSubscription(this);
  }

  SubscriptionType subType;
  
  String cellName;
  String transmitterName;
  String receptorName;
  String domain;
  TransactionFilter filterTransactions;
}
