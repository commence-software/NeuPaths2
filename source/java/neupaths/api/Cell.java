// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import neupaths.util.PropertySet;
import neupaths.stim.DateStimulus;

/**
 * The base class for NeuPaths cell types.  Provides accessor methods for common
 * cell characteristics and methods to control the event logging facilities.
 * <h2>Logging Facilities</h2>
 * <p>
 * User-level logging ({@link EventType} values {@code AUDIT 1-9},
 * {@code INFORMATION}, {@code WARNING} and {@code ERROR}) is enabled for cells by
 * default.  Runtime, Trace and Debug logging are disabled by default.  The {@code DEBUG}
 * and {@code TRACE} event types can also be used by NeuPaths API clients.  The
 * {@link Activator} {@code logEvent} methods can be used in API client code,
 * but event stimuli will not be produced unless the corresponding logging is
 * enabled.
 * </p>
 * <h2>Performance Tuning</h2>
 * <p>
 * A cell system is a dynamic entity.  Since a system can change at any
 * time, it is necessary to periodically advertise subscriptions.  A cell's
 * subscription refresh interval determines how frequently the cell advertises
 * its subscriptions.  In a large system, subscription refreshes can add
 * significantly to system traffic.  Use the {@link #setSubscriptionRefreshInterval}
 * method to tune a cell's subscription refresh interval.
 * </p>
 * <p>
 * Complex cell systems may contain redundant communication paths.  This can
 * lead to delivery of duplicate stimuli.  A cell must maintain a journal of
 * recently received stimuli in order to detect duplicates.  Such a journal can
 * consume resources.  Use the {@link #setDuplicateDetectionInterval}
 * method to tune a cell's duplicate detection interval.
 * </p>
 * <h2>Pulse Generator</h2>
 * <p>
 * Every cell contains a pulse generator that can produce periodic signals.
 * Pulse generation is disabled by default, but can be enabled by calling
 * {@link #setPulseInterval}.  To act upon pulses, include a specialization
 * of {@link PulsedActivator} in a cell's activator list.
 * </p>
 * 
 * @see Activator
 * @see EventType
 * @see PulsedActivator
 * 
 * @author Aaron Caraveo
 */
public class Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  Cell
    (CellType    type,
     String      name,
     PropertySet properties,
     String[]    synapseNames,
     Activator[] activators,
     byte[]      cryptoKey)
  {
    //-------------------------------------------------------------------------
    // Validate parameters
    //-------------------------------------------------------------------------
    
    if (type == null)
    {
      throw new NeuPathsException("Parameter 'type' is required");
    }

    if (name == null)
    {
      throw new NeuPathsException(type,
                                  "Parameter 'name' is required");
    }

    if (properties == null)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Parameter 'properties' is required");
    }

    if (synapseNames == null)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Parameter 'synapseNames' is required");
    }

    if (synapseNames.length == 0)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "At least one synapse name is required");
    }

    if (activators == null)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Parameter 'activators' is required");
    }

    if (activators.length == 0)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "At least one activator is required");
    }

    instanceID = UUID.randomUUID();
    
    this.type = type;
    this.name = name;
    this.properties = properties;
    this.activators = new LinkedList<>();
    this.cryptoKey = cryptoKey;
    
    HashSet<String> synapseNameSet = new HashSet<>();
    
    for (String synapse : synapseNames)
    {
      if (synapse == null)
      {
        throw new NeuPathsException(type,
                                    name,
                                    "Null synapse name specified");
      }
      
      synapseNameSet.add(synapse);
    }
    
    try
    {
      nucleus = new Nuc_Nucleus(name,
                                instanceID,
                                synapseNameSet,
                                cryptoKey);
      
      nucleus.setCell(this);
    }
    catch (Excp_Nucleus ne)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Could not create nucleus",
                                  ne);
    }

    for (Activator activator : activators)
    {
      if (activator == null)
      {
        throw new NeuPathsException(type,
                                    name,
                                    "Null activator specified");
      }
      
      activator.setCell(this);
      activator.updateLoopbackSubscriptions();
      nucleus.addSubscriptions(activator.getSubscriptions());
      this.activators.add(new ActivatorInfo(activator));
    }

    // Add Cycle Detection activator to all cells
    Actv_CycleDetection cdet = new Actv_CycleDetection();
    cdet.setCell(this);
    cdet.updateLoopbackSubscriptions();
    nucleus.addSubscriptions(cdet.getSubscriptions());
    this.activators.add(new ActivatorInfo(cdet));

    terminateActivators = new SafeBoolean();
    activatorsPaused = new SafeBoolean();
    
    loggingEnabled = new SafeBoolean(true);      // default: enabled
    runtimeLoggingEnabled = new SafeBoolean();   // default: disabled
    traceLoggingEnabled = new SafeBoolean();     // default: disabled
    debugLoggingEnabled = new SafeBoolean();     // default: disabled
    debugOutputEnabled = new SafeBoolean();      // default: disabled

    subscriptionRefreshIntervalMs = new SafeLong(1500L); // Default refresh of 1.5 sec
    
    pulseIntervalMs = new SafeLong(0L);          // Disabled by default
    terminatePulses = new SafeBoolean();
    pulseThread = null;

    // Initialize state data
    initialize();
  }
  
  //---------------------------------------------------------------------------
  
  Cell
    (CellType    type,
     String      name,
     PropertySet properties,
     String      synapseName,
     Activator   activator,
     byte[]      cryptoKey)
  {
    this(type,
         name,
         properties,
         new String[] { synapseName },
         new Activator[] { activator },
         cryptoKey);
  }
  
  //---------------------------------------------------------------------------

  Cell
    (CellType    type,
     String      name,
     PropertySet properties,
     String      synapseName,
     Activator[] activators,
     byte[]      cryptoKey)
  {
    this(type,
         name,
         properties,
         new String[] { synapseName },
         activators,
         cryptoKey);
  }
  
  //---------------------------------------------------------------------------

  Cell
    (CellType    type,
     String      name,
     PropertySet properties,
     String[]    synapseNames,
     Activator   activator,
     byte[]      cryptoKey)
  {
    this(type,
         name,
         properties,
         synapseNames,
         new Activator[] { activator },
         cryptoKey);
  }
  
  //---------------------------------------------------------------------------
  
  Cell
    (CellType    type,
     String      name,
     PropertySet properties,
     String[]    synapseNames,
     byte[]      cryptoKey)
  {
    //-------------------------------------------------------------------------
    // Validate parameters
    //-------------------------------------------------------------------------
    
    if (type == null)
    {
      throw new NeuPathsException("Parameter 'type' is required");
    }

    if (name == null)
    {
      throw new NeuPathsException(type,
                                  "Parameter 'name' is required");
    }

    if (properties == null)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Parameter 'properties' is required");
    }

    if (synapseNames == null)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Parameter 'synapseNames' is required");
    }

    if (synapseNames.length == 0)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "At least one synapse name is required");
    }

    instanceID = UUID.randomUUID();
    
    this.type = type;
    this.name = name;
    this.properties = properties;
    this.cryptoKey = cryptoKey;
    
    HashSet<String> synapseNameSet = new HashSet<>();
    
    for (String s : synapseNames)
    {
      if (s == null)
      {
        throw new NeuPathsException(type,
                                    name,
                                    "Null synapse name specified");
      }
      
      synapseNameSet.add(s);
    }
    
    try
    {
      nucleus = new Nuc_Nucleus(name,
                                instanceID,
                                synapseNameSet,
                                cryptoKey);
      
      nucleus.setCell(this);
    }
    catch (Excp_Nucleus ne)
    {
      throw new NeuPathsException(type,
                                  name,
                                  "Could not create nucleus",
                                  ne);
    }

    activators = new LinkedList<>();

    // Add Cycle Detection activator to all cells
    Actv_CycleDetection cdet = new Actv_CycleDetection();
    cdet.setCell(this);
    cdet.updateLoopbackSubscriptions();
    nucleus.addSubscriptions(cdet.getSubscriptions());
    activators.add(new ActivatorInfo(cdet));

    terminateActivators = new SafeBoolean();
    activatorsPaused = new SafeBoolean();
    
    loggingEnabled = new SafeBoolean(true);      // default: enabled
    runtimeLoggingEnabled = new SafeBoolean();   // default: disabled
    traceLoggingEnabled = new SafeBoolean();     // default: disabled
    debugLoggingEnabled = new SafeBoolean();     // default: disabled
    debugOutputEnabled = new SafeBoolean();      // default: disabled

    subscriptionRefreshIntervalMs = new SafeLong(1500L); // Default refresh of 1.5 sec
    
    pulseIntervalMs = new SafeLong(0L);          // Disabled by default
    terminatePulses = new SafeBoolean();
    pulseThread = null;

    // Initialize state data
    initialize();
  }
  
  //---------------------------------------------------------------------------

  Cell
    (CellType    type,
     String      name,
     PropertySet properties,
     String      synapseName,
     byte[]      cryptoKey)
  {
    this(type,
         name,
         properties,
         new String[] { synapseName },
         cryptoKey);
  }
  
  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  /**
   * Returns name of this cell.
   * 
   * @return The cell's name at runtime.
   */
  public final
  String
  getName ()
  {
    return name;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Returns instance ID of this cell.
   * 
   * @return The cell's instance ID.
   */
  public final
  UUID
  getInstanceID ()
  {
    return instanceID;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Returns the type of this cell.
   * 
   * @return The cell's type.
   */
  public final
  CellType
  getType ()
  {
    return type;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Returns the runtime state of this cell.
   * 
   * @return The cell's runtime state.
   */
  public final
  CellState
  getState ()
  {
    CellState currState = nucleus.getState();

    if ( (currState == CellState.ONLINE || currState == CellState.DEGRADED) &&
         activatorsPaused.isSet() )
    {
      currState = CellState.PAUSED;
    }

    return currState;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Retrieves a cell property.
   * 
   * @param name The property's name.
   * @return     The property's value.
   */
  public final
  <T> T
  getProperty (String name)
  {
    return (T) properties.get(name);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets a property for this cell.
   * 
   * @param name  The propery's name.
   * @param value The property's value.
   */
  public final
  void
  setProperty (String name, Object value)
  {
    properties.set(name, value);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets properties for this cell in a batch.
   * 
   * @param properties The property dictionary to copy.
   */
  public final
  void
  setProperties (PropertySet properties)
  {
    for (Map.Entry<String, Object> entry : properties)
    {
      this.properties.set(entry.getKey(), entry.getValue());
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Indicates if cell will produce event stimuli.
   * 
   * @return {@code true} if logging is enabled, {@code false} otherwise.
   */
  public final
  boolean
  isLoggingEnabled ()
  {
    return loggingEnabled.isSet();
  }
  
  //---------------------------------------------------------------------------

  /**
   * Enables the cell logging facility.
   */  
  public
  void
  enableLogging ()
  {
    loggingEnabled.set();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables the cell logging facility.
   */
  public final
  void
  disableLogging ()
  {
    loggingEnabled.clear();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if {@link EventType#RUNTIME} event stimuli will be produced by cell.
   * 
   * @return {@code true} if Runtime logging is enabled, {@code false} otherwise.
   */
  public final
  boolean
  isRuntimeLoggingEnabled ()
  {
    return runtimeLoggingEnabled.isSet();
  }
  
  //---------------------------------------------------------------------------

  /**
   * Enables {@link EventType#RUNTIME} event logging for cell.
   */  
  public final
  void
  enableRuntimeLogging ()
  {
    runtimeLoggingEnabled.set();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables {@link EventType#RUNTIME} event logging for cell.
   */
  public final
  void
  disableRuntimeLogging ()
  {
    runtimeLoggingEnabled.clear();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if {@link EventType#TRACE} event stimuli will be produced by cell.
   * 
   * @return {@code true} if Trace logging is enabled, {@code false} otherwise.
   */
  public final
  boolean
  isTraceLoggingEnabled ()
  {
    return traceLoggingEnabled.isSet();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables {@link EventType#TRACE} event logging for cell.
   */  
  public final
  void
  enableTraceLogging ()
  {
    traceLoggingEnabled.set();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables {@link EventType#TRACE} event logging for cell.
   */  
  public final
  void
  disableTraceLogging ()
  {
    traceLoggingEnabled.clear();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Indicates if {@link EventType#DEBUG} event stimuli will be produced by cell.
   * 
   * @return {@code true} if Debug logging is enabled, {@code false} otherwise.
   */
  public final
  boolean
  isDebugLoggingEnabled ()
  {
    return debugLoggingEnabled.isSet();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables {@link EventType#DEBUG} event logging for cell.
   */  
  public final
  void
  enableDebugLogging ()
  {
    debugLoggingEnabled.set();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables {@link EventType#DEBUG} event logging for cell.
   */  
  public final
  void
  disableDebugLogging ()
  {
    debugLoggingEnabled.clear();
  }
  
  //---------------------------------------------------------------------------

  /**
   * Convenience method to enable {@link EventType#RUNTIME},
   * {@link EventType#TRACE} and {@link EventType#DEBUG} logging.
   */
  public final
  void
  enableSystemLogging ()
  {
    runtimeLoggingEnabled.set();
    traceLoggingEnabled.set();
    debugLoggingEnabled.set();
  }
  
  //---------------------------------------------------------------------------

  /**
   * Convenience method to disable {@link EventType#RUNTIME},
   * {@link EventType#TRACE} and {@link EventType#DEBUG} logging.
   */
  public final
  void
  disableSystemLogging ()
  {
    runtimeLoggingEnabled.clear();
    traceLoggingEnabled.clear();
    debugLoggingEnabled.clear();
  }
  
  //---------------------------------------------------------------------------

  /**
   * Results in event stimuli being reported to the processes' standard output.
   */
  public final
  void
  enableDebugOutputLogging ()
  {
    debugOutputEnabled.set();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables reporting of event stimuli to the processes' standard output.
   */
  public final
  void
  disableDebugOutputLogging ()
  {
    debugOutputEnabled.clear();
  }
  
  //---------------------------------------------------------------------------

  /**
   * Allows propagation of subscriptions in the global domain.
   */
  public final
  void
  enableGlobalSubscriptionPropagation ()
  {
    nucleus.enableGlobalSubscriptionPropagation();
  }

  //---------------------------------------------------------------------------

  /**
   * Disables propagation of subscriptions in the global domain.
   */
  public final
  void
  disableGlobalSubscriptionPropagation ()
  {
    nucleus.disableGlobalSubscriptionPropagation();
  }

  //---------------------------------------------------------------------------

  /**
   * Commences processing and routing of stimuli.
   */
  public final
  void
  start ()
  {
    if (getState() == CellState.OFFLINE)
    {
      // Start the router
      nucleus.start();
      
      // Clear terminate flags
      terminateActivators.clear();
      terminatePulses.clear();
  
      if (activators != null)
      {
        // Start the activators
        try
        {
          for (ActivatorInfo a : activators)
          {
            a.startupThread = new StartupThread(a);
            a.startupThread.start();
          }
        }
        catch (Throwable te)
        {
          throw new NeuPathsException(type,
                                         name,
                                         "Activator initialization failed",
                                         te);
        }
      }
  
      // Start the pulse generator
      if (pulseIntervalMs.getValue() > 0L)
      {
        pulseThread = new PulseThread();
        pulseThread.start();
      }
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Pauses processing of stimuli.
   */
  public final
  void
  pause ()
  {
    CellState currState = nucleus.getState();

    if ( (currState == CellState.ONLINE || currState == CellState.DEGRADED) &&
         activatorsPaused.isNotSet() )
    {
      activatorsPaused.set();
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Resumes processing of stimuli.
   */
  public final
  void
  resume ()
  {
    CellState currState = nucleus.getState();

    if ( (currState == CellState.ONLINE || currState == CellState.DEGRADED) &&
         activatorsPaused.isSet() )
    {
      activatorsPaused.clear();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Terminates processing and routine of stimuli.
   */
  public final
  void
  stop ()
  {
    if (getState() != CellState.OFFLINE)
    {
      if (pulseThread != null)
      {
        terminatePulses.set();
        pulseThread.interrupt();
        pulseThread = null;
      }
      
      if (activators != null)
      {
        // Tell activator threads to terminate
        terminateActivators.set();
  
        // Stop the activators
        try
        {
          for (ActivatorInfo a : activators)
          {
            if (a.startupThread != null)
            {
              a.startupThread.interrupt();
              a.startupThread = null;
            }
            
            if (a.activatorThread != null)
            {
              a.activatorThread.interrupt();
              a.activatorThread = null;
            }
            
            a.activator.stop();
            a.activator.clearTransactions();
          }
        }
        catch (Throwable te)
        {
          /* ignore */
        }
      }
      
      // Stop the router
      nucleus.stop();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Sets the duplicate detection interval.
   * <p>
   * The default is 1000 milliseconds (1 second.)  The minimum is 250
   * milliseconds.  Values less than the minimum will be automatically
   * changed to the minimum value.
   * </p>
   * 
   * @param millisecs Duration of interval in milliseconds
   */
  public final
  void
  setDuplicateDetectionInterval (long millisecs)
  {
    nucleus.setDuplicateDetectionInterval(millisecs);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets the subscription refresh interval.
   * <p>
   * The default is 1500 milliseconds (1.5 seconds.)  Values less than or
   * equal to zero will disable automatic subscription refreshes.  When
   * disabled, the user must manually invoke {@link #publishSubscriptions}
   * to advertise the cell's subscriptions.
   * </p>
   * 
   * @param millisecs Duration of interval in milliseconds
   */
  public final
  void
  setSubscriptionRefreshInterval (long millisecs)
  {
    long ms = millisecs;
    
    if (ms < 0L)
      ms = 0L;
    
    subscriptionRefreshIntervalMs.setValue(ms);
    
    nucleus.setSubscriptionRefreshInterval(ms);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets the subscription trace interval.
   * <p>
   * When enabled, the cell's nucleus reports the subscriptions it has
   * registered in an {@link EventType#TRACE} log event each interval.
   * Values less than or equal to zero disable subscription tracing.
   * The value is zero by default, meaning this feature must be
   * specifically enabled.
   * </p>
   * 
   * @param millisecs Duration of interval in milliseconds
   */
  public final
  void
  setSubscriptionTraceInterval (long millisecs)
  {
    nucleus.setSubscriptionTraceInterval(millisecs);
  }
  
  //---------------------------------------------------------------------------

  /**
   * Sets the pulse generator interval.
   * <p>
   * When enabled, produces a periodic pulse that can be acted upon using
   * a specialized {@link PulsedActivator}.  Values less than or equal to
   * zero disable pulse generation.  The value is zero by default, meaning
   * this feature must be specicially enabled.
   * </p>
   *
   * @param millisecs Duration of pulse interval in milliseconds
   */
  public final
  void
  setPulseInterval (long millisecs)
  {
    long ms = millisecs;
    
    if (ms < 0L)
      ms = 0L;

    if (ms == 0L && pulseThread != null)
    {
      terminatePulses.set();
      pulseThread.interrupt();
      pulseThread = null;
    }

    pulseIntervalMs.setValue(ms);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Advertises the cell's subscriptions to the cell system.  This method can
   * be used when subscription refresh has been turned off.  It should only be
   * invoked after the cell has been started.
   */
  public final
  void
  publishSubscriptions ()
  {
    nucleus.publishSubscriptions();
  }
  
  //===========================================================================
  //  PROTECTED METHODS
  //===========================================================================

  /**
   * Initializes cell state data.  Override this method to initialize the
   * cell prior to starting.
   */
  protected
  void
  initialize ()
  {
    // override to initialize Cell at construction
  }
    
  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  Nuc_Nucleus
  getNucleus ()
  {
    return nucleus;
  }
  
  void
  addSubscription (SubscriptionSpec subscription)
  {
    nucleus.addSubscription(subscription);
  }
  
  //---------------------------------------------------------------------------
  
  void
  addSubscriptions (SubscriptionSpec[] subscriptions)
  {
    nucleus.addSubscriptions(subscriptions);
  }
  
  //---------------------------------------------------------------------------
  
  void
  addActivator (Activator activator)
  {
    activator.setCell(this);
    activator.updateLoopbackSubscriptions();
    nucleus.addSubscriptions(activator.getSubscriptions());
    activators.add(new ActivatorInfo(activator));
  }
  
  //---------------------------------------------------------------------------
  
  void
  receiveStimulus (Stimulus stimulus)
  {
    for (ActivatorInfo a : activators)
    {
      if (a.activator.isInterested(stimulus) && a.activatorThread != null)
      {
        a.activatorThread.announceStimulus(stimulus);
      }
    }  // for each activator
  }
  
  //---------------------------------------------------------------------------
  
  void
  transmitStimulus
  (Stimulus stimulus, boolean trace)
  {
    nucleus.receiveStimulus(stimulus, trace);
  }
  
  //---------------------------------------------------------------------------
  
  synchronized
  void
  logEvent (EventType type, String source, String details, Throwable cause)
  {
    if (loggingEnabled.isSet())
    {
      String allDetails = details;
      if (cause != null)
      {
        allDetails += "\n=> " + cause;

        allDetails += "\nTrace:";
        for (StackTraceElement e : cause.getStackTrace())
        {
          allDetails += "\n  " + e;
        }
      }

      EventStimulus event = new EventStimulus(type, source, allDetails);
      event.setProducerCellID(instanceID);
      event.setProducerCellName(name);
      event.setProducerTransmitterName(Evt.EVENT_TRANSMITTER);
      
      if ( (type != EventType.RUNTIME && type != EventType.TRACE && type != EventType.DEBUG) ||
           (type == EventType.RUNTIME && runtimeLoggingEnabled.isSet()) ||
           (type == EventType.TRACE && traceLoggingEnabled.isSet()) ||
           (type == EventType.DEBUG && debugLoggingEnabled.isSet()) )
      {
        transmitStimulus(event, false);

        if (debugOutputEnabled.isSet())
          System.out.print(event);
      }
    }
  }
  
  //---------------------------------------------------------------------------
  
  void
  logEvent (EventType type, String source, String details)
  {
    logEvent(type, source, details, null);
  }
  
  //---------------------------------------------------------------------------
  
  void
  logEvent (EventType type, String source, Throwable cause)
  {
    logEvent(type, source, "Caused by", cause);
  }

  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  //===========================================================================
  //  THREADS
  //===========================================================================

  //---------------------------------------------------------------------------
  // StartupThread
  //---------------------------------------------------------------------------
  final class StartupThread extends Thread
  {
    StartupThread (ActivatorInfo info)
    {
      super(name + " (" + instanceID + ") Startup for " + info.activator.getName());

      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
          gracefulTerminate.equals("n") ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }

      this.info = info;
    }
    
    @Override
    public
    void
    run ()
    {
      long subRefreshInt = subscriptionRefreshIntervalMs.getValue();
      long startupDelay = (subRefreshInt > 0L) ? (long)(subRefreshInt * 1.1) : 0L;

      if (startupDelay > 0L)
      {
        try
        {
          Thread.sleep(startupDelay);
          
          info.activator.start();
          info.activatorThread = new ActivatorThread(info.activator);
          info.activatorThread.start();
          try
          {
            info.activator.postStimuli();
          }
          catch (Excp_Transmitter te)
          {
            logEvent(EventType.ERROR, getName(), "Failed to post stimuli", te);
          }
        }
        catch (InterruptedException ie)
        {
          // ignore interruptions
        }
      }
    }

    ActivatorInfo info;
  }
  
  //---------------------------------------------------------------------------
  // PulseThread
  //
  // Produces a periodic pulse internal to the cell.
  //---------------------------------------------------------------------------
  final class PulseThread extends Thread
  {
    PulseThread ()
    {
      super(name + " (" + instanceID + ") Pulse");

      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
          gracefulTerminate.equals("n") ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
    }

    @Override
    public
    void
    run ()
    {
      while (terminatePulses.isNotSet())
      {
        try
        {
          Thread.sleep(pulseIntervalMs.getValue());

          Stimulus signal = new DateStimulus();
          signal.setProducerCellID(instanceID);
          signal.setProducerCellName(name);
          signal.setProducerTransmitterName(PULSE_TRANSMITTER);
      
          transmitStimulus(signal, true);
        }
        catch (InterruptedException ie)
        {
          // Ignore
        }
      }
    }
  }
  
  //---------------------------------------------------------------------------
  // ActivatorThread
  //
  // Evaluates stimuli upon receipt.
  //---------------------------------------------------------------------------
  final class ActivatorThread extends Thread
  {
    ActivatorThread (Activator activator)
    {
      super(name + " (" + instanceID + ") Activator " + activator.getName());

      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
          gracefulTerminate.equals("n") ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
      
      setPriority(Thread.NORM_PRIORITY + 2);

      this.activator = activator;
      
      stimuliSemaphore = new Semaphore(0, true);
      stimuliQueue = new LinkedList<Stimulus>();
      stimuliMutex = new ReentrantLock(true);
    }
    
    void
    announceStimulus (Stimulus stimulus)
    {
      stimuliMutex.lock();
      try
      {
        stimuliQueue.addLast(stimulus);
      }
      finally
      {
        stimuliMutex.unlock();
      }
      
      stimuliSemaphore.release();
    }

    private
    Stimulus acceptStimulus ()
      throws InterruptedException
    {
      Stimulus stimulus = null;

      stimuliSemaphore.acquire();

      stimuliMutex.lock();
      try
      {
        stimulus = stimuliQueue.pollFirst();
      }
      finally
      {
        stimuliMutex.unlock();
      }

      return stimulus;
    }

    @Override
    public
    void
    run ()
    {
      Stimulus stimulus = null;

      while (terminateActivators.isNotSet())
      {
        try
        {
          if (activatorsPaused.isSet())
          {
            Thread.sleep(500);
          }
          else
          {
            // Get next stimulus
            stimulus = acceptStimulus();
    
            try
            {
              activator.evaluateStimulus(stimulus);
            }
            catch (Throwable t)
            {
              logEvent(EventType.ERROR,
                       getName(),
                       "Stimuli evaluation failed",
                       t);
            }
          }
        }
        catch (InterruptedException ie)
        {
          // Ignore
        }
      }  // while (terminateActivators.isNotSet())

      stimuliSemaphore.drainPermits();
      stimuliQueue.clear();

    }  // end run

    private Activator activator;
    private Semaphore stimuliSemaphore;
    private LinkedList<Stimulus> stimuliQueue;
    private ReentrantLock stimuliMutex;
  }
  
  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private CellType type;
  private String name;
  private UUID instanceID;
  private PropertySet properties;
  private byte[] cryptoKey;
  private Nuc_Nucleus nucleus;

  /**
   * Used internally to maintain information about Activators.
   */
  final class ActivatorInfo
  {
    ActivatorInfo (Activator activator)
    {
      this.activator = activator;
      startupThread = null;
      activatorThread = null;
    }
    
    Activator activator;
    StartupThread startupThread;
    ActivatorThread activatorThread;
  }
  
  LinkedList<ActivatorInfo> activators;
  
  SafeBoolean terminateActivators;
  SafeBoolean activatorsPaused;
  
  SafeBoolean loggingEnabled;
  private SafeBoolean runtimeLoggingEnabled;
  private SafeBoolean traceLoggingEnabled;
  private SafeBoolean debugLoggingEnabled;
  private SafeBoolean debugOutputEnabled;

  private SafeLong subscriptionRefreshIntervalMs;
  
  private SafeLong pulseIntervalMs;
  private SafeBoolean terminatePulses;
  private PulseThread pulseThread;

  static final String PULSE_TRANSMITTER = "195d6fe8-772c-4db9-b986-294b3b0d4ebd";
}
