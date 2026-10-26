// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link LoadControllerCell} configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_LoadControllerCellHandler extends Cfg_CellHandler
    implements Cfg_ReceptorsHandlerInt,
               Cfg_SubscriptionsHandlerInt
{
  Cfg_LoadControllerCellHandler ()
  {
    super(CellType.LOAD_CONTROLLER, "/Load_Controller_Cell");

    receptors = new LinkedList<>();
    subscriptions = new LinkedList<>();
    cellNamePrefix = null;
    cellDomain = null;
  }

  public void processNode (String definitionFile, Node node)
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
    else if (nodeName.equals("RECEPTORS"))
    {
      Cfg_CellReceptorsHandler handler = new Cfg_CellReceptorsHandler(cellType, xPath + "/Receptors");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("SUBSCRIPTIONS"))
    {
      Cfg_CellSubscriptionsHandler handler = new Cfg_CellSubscriptionsHandler(cellType, xPath + "/Subscriptions");
      Cfg_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("CELL_NAME_PREFIX"))
    {
      cellNamePrefix = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("CELL_DOMAIN"))
    {
      cellDomain = Cfg_Utils.getNodeText(node);
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
    else if (nodeName.equals("PULSE_INTERVAL_MS"))
    {
      pulseInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
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

  public void addReceptor (Cfg_CellReceptorHandler receptor)
  {
    receptors.add(receptor);
  }
  
  public void addSubscription (Cfg_CellSubscriptionHandler subscription)
  {
    subscriptions.add(subscription);
  }
  
  LinkedList<Cfg_CellReceptorHandler> receptors;
  LinkedList<Cfg_CellSubscriptionHandler> subscriptions;
  String cellNamePrefix;
  String cellDomain;
}
