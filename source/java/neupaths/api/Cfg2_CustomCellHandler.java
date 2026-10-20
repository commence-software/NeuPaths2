// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a custom {@link LogicCell} or {@link LoadBalancedCell}
 * configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg2_CustomCellHandler extends Cfg2_CellHandler
{
  Cfg2_CustomCellHandler ()
  {
    super(CellType.CUSTOM, "/Custom_Cell");

    className = null;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("CLASS"))
    {
      className = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("NAME"))
    {
      name = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("PROPERTIES"))
    {
      Cfg2_CellPropertiesHandler handler = new Cfg2_CellPropertiesHandler(cellType, xPath + "/Properties");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("SYNAPSES"))
    {
      Cfg2_CellSynapsesHandler handler = new Cfg2_CellSynapsesHandler(cellType, xPath + "/Synapses");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("ACTIVATORS"))
    {
      Cfg2_CellActivatorsHandler handler = new Cfg2_CellActivatorsHandler(cellType, xPath + "/Activators");
      Cfg2_Utils.processElement(definitionFile, this, handler, node);
    }
    else if (nodeName.equals("LOGGING_ENABLED"))
    {
      loggingEnabled = Boolean.parseBoolean(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("TRACE_LOGGING_ENABLED"))
    {
      traceLoggingEnabled = Boolean.parseBoolean(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("DEBUG_LOGGING_ENABLED"))
    {
      debugLoggingEnabled = Boolean.parseBoolean(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("RUNTIME_LOGGING_ENABLED"))
    {
      runtimeLoggingEnabled = Boolean.parseBoolean(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("PROPAGATE_GLOBAL_SUBSCRIPTIONS"))
    {
      propagateGlobalSubscriptions =
        Boolean.parseBoolean(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("SUBSCRIPTION_REFRESH_INTERVAL_MS"))
    {
      subscriptionRefreshInterval = Long.parseLong(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("DUPLICATE_DETECTION_INTERVAL_MS"))
    {
      duplicateDetectionInterval = Long.parseLong(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("SUBSCRIPTION_TRACE_INTERVAL_MS"))
    {
      subscriptionTraceInterval = Long.parseLong(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("PULSE_INTERVAL_MS"))
    {
      pulseInterval = Long.parseLong(Cfg2_Utils.getNodeText(node));
    }
    else if (nodeName.equals("CRYPTO_KEY_FILE"))
    {
      cryptoKeyFile = Cfg2_Utils.getNodeText(node);
    }
    else
    {
      throw new NeuPathsException("Unexpected Logic_Cell configuration tag '" +
                                  nodeName + "' in file " + definitionFile);
    }
  }

  String className;
}
