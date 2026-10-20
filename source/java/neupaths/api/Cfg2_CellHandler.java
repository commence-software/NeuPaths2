// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Base handler for parsing a {@link Cell} configuration.
 *
 * @author Aaron Caraveo
 */
abstract class Cfg2_CellHandler extends Cfg2_ConfigHandler
    implements Cfg2_PropertiesHandlerInt,
               Cfg2_SynapsesHandlerInt,
               Cfg2_ActivatorsHandlerInt
{
  Cfg2_CellHandler (CellType cellType, String xPath)
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

  public void addProperty (Cfg2_CellPropertyHandler property)
  {
    properties.add(property);
  }
  
  public void addSynapse (String synapseName)
  {
    synapseNames.add(synapseName);
  }

  public void addActivator (Cfg2_CellActivatorHandler activator)
  {
    activators.add(activator);
  }
  
  String name;
  LinkedList<Cfg2_CellPropertyHandler> properties;
  HashSet<String> synapseNames;
  LinkedList<Cfg2_CellActivatorHandler> activators;
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
