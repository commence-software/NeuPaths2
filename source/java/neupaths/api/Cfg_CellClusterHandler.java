// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for a Cell_Cluster configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellClusterHandler extends Cfg_ConfigHandler
{
  Cfg_CellClusterHandler ()
  {
    super("Cell_Cluster");
    
    clusterName = null;
    cellDefinitionFiles = null;
    loggingEnabled = true;
    loggingSpecified = false;
    traceLoggingEnabled = false;
    traceLoggingSpecified = false;
    debugLoggingEnabled = false;
    debugLoggingSpecified = false;
    runtimeLoggingEnabled = false;
    runtimeLoggingSpecified = false;
    propagateGlobalSubscriptions = true;
    propagateGlobalSpecified = false;
    subscriptionRefreshInterval = 1500L;
    subscriptionRefreshSpecified = false;
    duplicateDetectionInterval = 1000L;
    duplicateDetectionSpecified = false;
    subscriptionTraceInterval = 0L;
    subscriptionTraceSpecified = false;
    pulseInterval = 0L;
    pulseSpecified = false;
    cryptoKeyFile = null;
  }

  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("NAME"))
    {
      clusterName = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("CELL_DEFINITIONS"))
    {
      Cfg_CellDefinitionsHandler handler = new Cfg_CellDefinitionsHandler(xPath + "/Cell_Definitions");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else if (nodeName.equals("LOGGING_ENABLED"))
    {
      loggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
      loggingSpecified = true;
    }
    else if (nodeName.equals("TRACE_LOGGING_ENABLED"))
    {
      traceLoggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
      traceLoggingSpecified = true;
    }
    else if (nodeName.equals("DEBUG_LOGGING_ENABLED"))
    {
      debugLoggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
      debugLoggingSpecified = true;
    }
    else if (nodeName.equals("RUNTIME_LOGGING_ENABLED"))
    {
      runtimeLoggingEnabled = Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
      runtimeLoggingSpecified = true;
    }
    else if (nodeName.equals("PROPAGATE_GLOBAL_SUBSCRIPTIONS"))
    {
      propagateGlobalSubscriptions =
        Boolean.parseBoolean(Cfg_Utils.getNodeText(node));
      propagateGlobalSpecified = true;
    }
    else if (nodeName.equals("SUBSCRIPTION_REFRESH_INTERVAL_MS"))
    {
      subscriptionRefreshInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
      subscriptionRefreshSpecified = true;
    }
    else if (nodeName.equals("DUPLICATE_DETECTION_INTERVAL_MS"))
    {
      duplicateDetectionInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
      duplicateDetectionSpecified = true;
    }
    else if (nodeName.equals("SUBSCRIPTION_TRACE_INTERVAL_MS"))
    {
      subscriptionTraceInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
      subscriptionTraceSpecified = true;
    }
    else if (nodeName.equals("PULSE_INTERVAL_MS"))
    {
      pulseInterval = Long.parseLong(Cfg_Utils.getNodeText(node));
      pulseSpecified = true;
    }
    else if (nodeName.equals("CRYPTO_KEY_FILE"))
    {
      cryptoKeyFile = Cfg_Utils.getNodeText(node);
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell_Cluster configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }

  String clusterName;
  LinkedList<String> cellDefinitionFiles;
  boolean loggingEnabled;
  boolean loggingSpecified;
  boolean traceLoggingEnabled;
  boolean traceLoggingSpecified;
  boolean debugLoggingEnabled;
  boolean debugLoggingSpecified;
  boolean runtimeLoggingEnabled;
  boolean runtimeLoggingSpecified;
  boolean propagateGlobalSubscriptions;
  boolean propagateGlobalSpecified;
  long subscriptionRefreshInterval;
  boolean subscriptionRefreshSpecified;
  long duplicateDetectionInterval;
  boolean duplicateDetectionSpecified;
  long subscriptionTraceInterval;
  boolean subscriptionTraceSpecified;
  long pulseInterval;
  boolean pulseSpecified;
  String cryptoKeyFile;
}
