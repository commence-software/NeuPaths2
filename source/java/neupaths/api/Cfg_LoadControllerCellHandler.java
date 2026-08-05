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
final class Cfg_LoadControllerCellHandler extends Cfg_CellHandler implements Cfg_ReceptorsHandlerInt, Cfg_SubscriptionsHandlerInt
{
  Cfg_LoadControllerCellHandler ()
  {
    super(CellType.LOAD_CONTROLLER, "/Load_Controller_Cell");

    receptors = new ReceptorSpecSet();
    subscriptions = new SubscriptionSpecSet();
    cellNamePrefix = null;
    cellDomain = null;
  }

  public void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("SYNAPSES"))
    {
      Cfg_CellSynapsesHandler handler = new Cfg_CellSynapsesHandler(xPath + "/Synapses");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("RECEPTORS"))
    {
      Cfg_CellReceptorsHandler handler = new Cfg_CellReceptorsHandler(xPath + "/Receptors");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("SUBSCRIPTIONS"))
    {
      Cfg_CellSubscriptionsHandler handler = new Cfg_CellSubscriptionsHandler(xPath + "/Subscriptions", cellType);
      Cfg_Utils.processElement(configFile, this, handler, node);
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
                                  nodeName + "' in file " + configFile);
    }
  }

  public void addReceptor (ReceptorSpec receptorSpec)
  {
    receptors.add(receptorSpec);
  }
  
  public void addSubscription (SubscriptionSpec subscriptionSpec)
  {
    subscriptions.add(subscriptionSpec);
  }

  ReceptorSpecSet receptors;
  SubscriptionSpecSet subscriptions;
  String cellNamePrefix;
  String cellDomain;
}
