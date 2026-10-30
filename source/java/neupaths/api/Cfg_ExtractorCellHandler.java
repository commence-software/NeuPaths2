// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Handler for parsing an {@link ExtractorCell} configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_ExtractorCellHandler extends Cfg_CellHandler
    implements Cfg_SubscriptionsHandlerInt
{
  Cfg_ExtractorCellHandler ()
  {
    super(CellType.EXTRACTOR, "/Extractor_Cell");

    subscription = null;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("SYNAPSES"))
    {
      Cfg_CellSynapsesHandler handler = new Cfg_CellSynapsesHandler(cellType, xPath + "/Synapses");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("SUBSCRIPTION"))
    {
      Cfg_CellSubscriptionHandler handler = new Cfg_CellSubscriptionHandler(cellType, xPath + "/Subscription", SubscriptionType.EXTRACTOR);
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("LOGGING_ENABLED"))
    {
      loggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("TRACE_LOGGING_ENABLED"))
    {
      traceLoggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("DEBUG_LOGGING_ENABLED"))
    {
      debugLoggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("RUNTIME_LOGGING_ENABLED"))
    {
      runtimeLoggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("PROPAGATE_GLOBAL_SUBSCRIPTIONS"))
    {
      propagateGlobalSubscriptions =
        Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("SUBSCRIPTION_REFRESH_INTERVAL_MS"))
    {
      subscriptionRefreshInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("DUPLICATE_DETECTION_INTERVAL_MS"))
    {
      duplicateDetectionInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("SUBSCRIPTION_TRACE_INTERVAL_MS"))
    {
      subscriptionTraceInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("TRANSACTION_HISTORY_WINDOW_MS"))
    {
      transactionHistoryWindow = Long.parseLong(Cfg_Utils.getNodeText(node));
    }
    else if (nodeName.equals("CRYPTO_KEY_FILE"))
    {
      cryptoKeyFile = Cfg_Utils.getNodeText(node);
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }

  public void addSubscription (Cfg_CellSubscriptionHandler subscription)
  {
    if (this.subscription == null)
    {
      this.subscription = subscription;
    }
    else
    {
      throw new NeuPathsException("Extractor cell subscription already specified");
    }
  }
  
  Cfg_CellSubscriptionHandler subscription;  
  long transactionHistoryWindow;
}
