// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Handler for parsing an {@link EventCell}'s configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_EventCellHandler extends Cfg_CellHandler
{
  Cfg_EventCellHandler ()
  {
    super(CellType.EVENT, "/Event_Cell");

    name = null;
    synapseName = null;
    outputFileName = null;
  }

  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("SYNAPSE"))
    {
      synapseName = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("OUTPUT_FILE"))
    {
      outputFileName = Cfg_Utils.getNodeText(node);
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

  String name;
  String synapseName;
  String outputFileName;
}
