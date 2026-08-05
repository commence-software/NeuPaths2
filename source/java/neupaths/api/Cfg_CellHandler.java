// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;

/**
 * Base handler for parsing a {@link Cell} configuration.
 *
 * @author Aaron Caraveo
 */
abstract class Cfg_CellHandler extends Cfg_ConfigHandler implements Cfg_SynapsesHandlerInt
{
  Cfg_CellHandler (CellType type, String xPath)
  {
    super(xPath);
    
    cellType = type;

    name = null;
    properties = new PropertySet();
    loggingEnabled = true;
    traceLoggingEnabled = false;
    debugLoggingEnabled = false;
    runtimeLoggingEnabled = false;
    propagateGlobalSubscriptions = true;
    subscriptionRefreshInterval = 1500L;
    duplicateDetectionInterval = 1000L;
    subscriptionTraceInterval = 0L;
    pulseInterval = 0L;
    cryptoKeyFile = null;
  }

  public void addSynapses (HashSet<String> synapseNames)
  {
    this.synapseNames = synapseNames;
  }

  CellType cellType;

  String name;
  PropertySet properties;
  HashSet<String> synapseNames;
  boolean loggingEnabled;
  boolean traceLoggingEnabled;
  boolean debugLoggingEnabled;
  boolean runtimeLoggingEnabled;
  boolean propagateGlobalSubscriptions;
  long subscriptionRefreshInterval;
  long duplicateDetectionInterval;
  long subscriptionTraceInterval;
  long pulseInterval;
  String cryptoKeyFile;
}
