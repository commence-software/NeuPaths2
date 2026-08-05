// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a specialized {@link LogicCell} or {@link LoadBalancedCell}
 * configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_SpecializedCellHandler extends Cfg_CellHandler implements Cfg_ActivatorsHandlerInt
{
  Cfg_SpecializedCellHandler ()
  {
    super(CellType.LOGIC, "/Specialized_Cell");

    className = null;
    activators = new LinkedList<>();
  }

  void processNode (String configFile, Node node)
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
    else if (nodeName.equals("PROPERTIES"))
    {
      Cfg_CellPropertiesHandler handler = new Cfg_CellPropertiesHandler(xPath + "/Properties");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("SYNAPSES"))
    {
      Cfg_CellSynapsesHandler handler = new Cfg_CellSynapsesHandler(xPath + "/Synapses");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("ACTIVATORS"))
    {
      Cfg_CellActivatorsHandler handler = new Cfg_CellActivatorsHandler(xPath + "/Activators");
      Cfg_Utils.processElement(configFile, this, handler, node);
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
      throw new NeuPathsException("Unexpected Logic_Cell configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }

  public void addActivator (Activator activator)
  {
    activators.addLast(activator);
  }

  String className;
  LinkedList<Activator> activators;
}
