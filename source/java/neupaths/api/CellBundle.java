// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Maintains a bundle of cells participating in a single domain.
 * <p>
 * This class is provided as a convenience for managing a group of cells.
 * The bundle is gated by a {@link BridgeCell} that manages
 * stimuli entering and leaving the bundle.  Each cell in the bundle
 * connects to the bridge and participates in the domain specified by the
 * {@code bundleSynapseName} synapse.
 * </p>
 * <p>
 * NOTE:  This class is superseded by {@link CellCluster} but may prove
 *        useful in the future.
 * </p>
 *
 * @author Aaron Caraveo
 */
class CellBundle
{
  /**
   * Allocates a new {@code CellBundle} object using specification arrays.
   * 
   * @param name                      The bundle's name at runtime.
   * @param bundleSynapseName         The synapse to which bundle members
   *                                  connect.  This synapse must be a Listener
   *                                  unless multicast is used.
   * @param additionalSynapseNames    An optional array of additional synapses
   *                                  this bundle listens on and/or connects
   *                                  to.  If the array is empty (null or empty
   *                                  array), the {@code bundleSynapseName} will
   *                                  be used for all bunlde communication.
   * @param subscriptions             The subscriptions this bundle
   *                                  advertises.  If the subscription array is
   *                                  empty (null or empty array), the bundle's
   *                                  {@code BridgeCell} operates as a
   *                                  {@link RouterCell}.
   * @param cryptoKey                 The stimulus encryption key.  Specify
   *                                  {@code null} to disable encryption using a
   *                                  user-specified key.  If disabled, the
   *                                  stimuli will still be encrypted as part of
   *                                  NeuPaths protocol encryption.
   */
  public
  CellBundle
    (String                   name,
     String                   bundleSynapseName,
     String[]                 additionalSynapseNames,
     BridgeSubscriptionSpec[] subscriptions,
     byte[]                   cryptoKey)
  {
    if (name == null)
    {
      throw new NeuPathsException("Bundle 'name' is required");
    }
    
    if (bundleSynapseName == null)
    {
      throw new NeuPathsException("Bundle 'bundleSynapseName' is required");
    }
    
    if (additionalSynapseNames == null)
    {
      additionalSynapseNames = new String[0];
    }
    
    try
    {
      Syn_Name bundlePeerName =
          new Syn_Name(bundleSynapseName);

      if (bundlePeerName.getType() != Syn_Type.MULTICAST)
      {
        if (bundlePeerName.getMode() != Syn_Mode.LISTENER)
          throw new NeuPathsException("Bundle 'bundleSynapseName' is not a Listener");
        
        bundlePeerName.setMode(Syn_Mode.PEER);
      }

      this.bundleSynapseName = bundlePeerName.getText();
    }
    catch (Excp_SynapseFatal sfe)
    {
      throw new NeuPathsException("bundleSynapseName invalid: " + sfe);
    }
    
    this.cryptoKey = cryptoKey;

    cells = new HashMap<>();
    
    bridgeSynapseNames = new HashSet<>();
    bridgeSynapseNames.add(bundleSynapseName);
    for (String as : additionalSynapseNames)
    {
      bridgeSynapseNames.add(as);
    }
    
    bridge = new BridgeCell(name + " (Bundle)",
                            bridgeSynapseNames.toArray(new String[0]),
                            subscriptions,
                            cryptoKey);
    
    cells.put(name, bridge);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a {@code LogicCell} in the bundle.
   * 
   * @param name      The cell's name at runtime.
   * @param activator The cell's activator.
   */
  public
  void
  createLogicCell
    (String    name,
     Activator activator)
  {
    LogicCell newCell =
        new LogicCell(name,
                      bundleSynapseName,
                      activator,
                      cryptoKey);

    cells.put(name, newCell);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a {@code LogicCell} in the bundle.
   * 
   * @param name       The cell's name at runtime.
   * @param activators The cell's activators.
   */
  public
  void
  createLogicCell
    (String      name,
     Activator[] activators)
  {
    LogicCell newCell =
        new LogicCell(name,
                      bundleSynapseName,
                      activators,
                      cryptoKey);

    cells.put(name, newCell);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates an {@code InjectorCell} in the bundle.
   * 
   * @param name        The injector's name at runtime.
   * @param transmitter The injector's transmitter.
   * @return            The new {@code InjectorCell} object.
   */
  public
  InjectorCell
  createInjectorCell
    (String          name,
     TransmitterSpec transmitter)
  {
    InjectorCell newInjector =
        new InjectorCell(name,
                         bundleSynapseName,
                         transmitter,
                         cryptoKey);

    cells.put(name, newInjector);
    
    return newInjector;
  }

  //---------------------------------------------------------------------------

  /**
   * Creates an {@code ExtractorCell} in the bundle.
   * 
   * @param name         The extractor's name at runtime.
   * @param subscription The extractor's subscription.
   * @return             The new {@code ExtractorCell} object.
   */
  public
  ExtractorCell
  createExtractorCell
    (String                    name,
     ExtractorSubscriptionSpec subscription)
  {
    ExtractorCell newExtractor =
        new ExtractorCell(name,
                          bundleSynapseName,
                          subscription,
                          cryptoKey);
    
    cells.put(name, newExtractor);
    
    return newExtractor;
  }

  //---------------------------------------------------------------------------

  /**
   * Creates an {@code EventCell} in the bundle.
   * 
   * @param name    The event cell's name at runtime.
   * @param spooler The event cell's {@code PrintStream} object.
   */
  public
  void
  createEventCell
    (String      name,
     PrintStream spooler)
  {
    EventCell newCell =
        new EventCell(name,
                      bundleSynapseName,
                      spooler,
                      cryptoKey);

    cells.put(name, newCell);
  }

  //---------------------------------------------------------------------------

  /**
   * Creates an {@code EventCell} in the bundle.
   * 
   * @param name      The event cell's name at runtime.
   * @param activator The event cell's activator.
   */
  public
  void
  createEventCell
    (String         name,
     EventActivator activator)
  {
    EventCell newCell =
        new EventCell(name,
                      bundleSynapseName,
                      activator,
                      cryptoKey);

    cells.put(name, newCell);
  }

  //---------------------------------------------------------------------------

  /**
   * Retrieves the bundle's name.
   * 
   * @return The bundle's name.
   */
  public
  String
  getName ()
  {
    return bridge.getName();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Retrieves the bundle's instance ID.
   * 
   * @return The bundle's instance ID.
   */
  public
  UUID
  getInstanceID ()
  {
    return bridge.getInstanceID();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Retrieves the bundle's cell type.
   * 
   * @return The bundle's cell type.
   */
  public
  CellType
  getType ()
  {
    return bridge.getType();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Retrieves the bundle's runtime state.
   * 
   * @return The bundle's runtime state.
   */
  public
  CellState
  getState ()
  {
    return bridge.getState();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if event logging is enabled for the bundle.
   * 
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  public
  boolean
  isLoggingEnabled ()
  {
    return bridge.isLoggingEnabled();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables event logging for the bundle.
   */
  public
  void
  enableLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables event logging for the bundle.
   */
  public
  void
  disableLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if Runtime event logging is enabled for the bundle.
   * 
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  public
  boolean
  isRuntimeLoggingEnabled ()
  {
    return bridge.isRuntimeLoggingEnabled();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables Runtime event logging for the bundle.
   */
  public
  void
  enableRuntimeLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableRuntimeLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Runtime event logging for the bundle.
   */
  public
  void
  disableRuntimeLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableRuntimeLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if Trace event logging is enabled for the bundle.
   * 
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  public
  boolean
  isTraceLoggingEnabled ()
  {
    return bridge.isTraceLoggingEnabled();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables Trace event logging for the bundle.
   */
  public
  void
  enableTraceLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableTraceLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Trace event logging for the bundle.
   */
  public
  void
  disableTraceLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableTraceLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if Debug event logging is enabled for the bundle.
   * 
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  public
  boolean
  isDebugLoggingEnabled ()
  {
    return bridge.isDebugLoggingEnabled();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables Debug event logging for the bundle.
   */
  public
  void
  enableDebugLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableDebugLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Debug event logging for the bundle.
   */
  public
  void
  disableDebugLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableDebugLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Enables Runtime, Trace and Debug event logging for the bundle.
   */
  public
  void
  enableSystemLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableSystemLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Disables Runtime, Trace and Debug event logging for the bundle.
   */
  public
  void
  disableSystemLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableSystemLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Enables Debug output logging for the bundle.  Event data will
   * be output to standard output.
   */
  public
  void
  enableDebugOutputLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableDebugOutputLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Debug output logging for the bundle.
   */
  public
  void
  disableDebugOutputLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableDebugOutputLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Starts the bundle.  Cells will be started in the order they were
   * added to the bundle.
   */
  public
  void
  start ()
  {
    for (Cell c : cells.values())
    {
      c.start();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Stops the bundle.  Cells will be stopped in the order they were
   * added to the bundle.
   */
  public
  void
  stop ()
  {
    for (Cell c : cells.values())
    {
      c.stop();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Sets the duplicate detection interval for all cells in the bundle.
   * 
   * @param millisecs The interval in milliseconds.
   */
  public
  void
  setDuplicateDetectionInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setDuplicateDetectionInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets the subscription refresh interval for all cells in the bundle.
   * 
   * @param millisecs The interval in milliseconds.
   */
  public
  void
  setSubscriptionRefreshInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setSubscriptionRefreshInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Sets the subscription trace interval for all cells in the bundle.
   * 
   * @param millisecs The interval in milliseconds.
   */
  public
  void
  setSubscriptionTraceInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setSubscriptionTraceInterval(millisecs);
    }
  }
    
  //---------------------------------------------------------------------------
  
  /**
   * Request all cells in the bundle to publish their subscriptions.
   */
  public
  void
  publishSubscriptions ()
  {
    for (Cell c : cells.values())
    {
      c.publishSubscriptions();
    }
  }

  /**
   * Retrieves a cell in the bundle by name.
   *
   * @param name The cell's name
   *
   * @return {@link Cell} object.
   */
  @SuppressWarnings("unchecked")
  public
  <T extends Cell> T getCell (String name)
  {
    return (T)(cells.get(name));
  }
  
  private String bundleSynapseName;
  private byte[] cryptoKey;
  private HashMap<String, Cell> cells;
  private HashSet<String> bridgeSynapseNames;
  private BridgeCell bridge;
}
