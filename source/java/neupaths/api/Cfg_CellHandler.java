// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Base handler for parsing a {@link Cell} configuration.
 *
 * @author Aaron Caraveo
 */
abstract class Cfg_CellHandler extends Cfg_ConfigHandler
    implements Cfg_PropertiesHandlerInt,
               Cfg_SynapsesHandlerInt,
               Cfg_ActivatorsHandlerInt
{
  Cfg_CellHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    name = null;
    properties = new LinkedList<>();
    synapseNames = new HashSet<>();
    activators = new LinkedList<>();
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

  public void addProperty (Cfg_CellPropertyHandler property)
  {
    properties.add(property);
  }
  
  public void addSynapse (String synapseName)
  {
    synapseNames.add(synapseName);
  }

  public void addActivator (Cfg_CellActivatorHandler activator)
  {
    activators.add(activator);
  }
  
  String name;
  LinkedList<Cfg_CellPropertyHandler> properties;
  HashSet<String> synapseNames;
  LinkedList<Cfg_CellActivatorHandler> activators;
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
