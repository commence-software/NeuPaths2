// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.UUID;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * The base class for NeuPaths activator types.  Activators consume and produce
 * stimuli.  Each activator advertises the stimuli it wants to receive on its
 * receptors using subscriptions.  An activator may send stimuli on its transmitters.
 * <p>
 * Specializations of this class must implement the {@link #evaluate} method
 * and can optionally override the {@link #start} and {@link #stop} methods.
 * </p>
 * <p>
 * An activator evaluates when all of its receptors contain stimuli.  The
 * {@link #getStimulus} method is used from within {@link #evaluate} to process
 * the stimuli.  Each time {@link #evaluate} is called, the stimuli on the
 * Activator's receptors are consumed, whether or not they are used during
 * evaluation.
 * </p>
 * <p>
 * Activator specializations can be declared using a single expression.
 * For example:
 * </p>
 * <p>
 * <ul>
 * <pre> public class WorldHelloActivator extends Activator
 * {
 *   public WorldHelloActivator ()
 *   {
 *     super("WorldHelloActivator",
 *           new ReceptorSpec[] {
 *             new ReceptorSpec("Hail",
 *                              ReceptorMode.NON_BUFFERED,
 *                              DateStimulus.TYPE_ID)
 *           },
 *           new TransmitterSpec[] {
 *             new TransmitterSpec("Salutation",
 *                                 WorldHelloSalutation.TYPE_ID,
 *                                 StimulusTrace.ENABLED)
 *           },
 *           new LogicSubscriptionSpec[] {
 *             new LogicSubscriptionSpec("HailInjector",
 *                                       "Hello",
 *                                       "Hail",
 *                                       "Request",
 *                                       TransactionFilter.DISABLED)});
 *   }
 *
 *   protected void evaluate ()
 *   {
 *     ...
 *   }
 * }
 * 
 * Activator worldHelloActivator = new WorldHelloActivator();</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Note:</b> Activators declared in Cell Definition files (see
 * {@link CellFactory}) must use the single expression method and provide a
 * zero-argument constructor.  The activator class and constructor must be
 * {@code public}.
 * </p>
 * 
 * @see ReceptorSpec
 * @see TransmitterSpec
 * @see LogicSubscriptionSpec
 * @see LogicLoopbackSubscriptionSpec
 * @see LogicMapSubscriptionSpec
 * 
 * @author Aaron Caraveo
 */
public abstract class Activator
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code Activator} object.
   * 
   * @param name            This activator's name at runtime.
   * @param receptors       The receptors this activator receives stimuli on.
   * @param transmitters    The transmitters this activator emits stimuli on.
   *                        If the transmitter array is empty (null or empty
   *                        array), the activator does not emit stimuli.
   * @param subscriptions   The subscriptions this activator advertises.
   */
  protected
  Activator
    (String                  name,
     ReceptorSpec[]          receptors,
     TransmitterSpec[]       transmitters,
     LogicSubscriptionSpec[] subscriptions)
  {
    if (name == null)
    {
      throw new NeuPathsException("Activator: Parameter 'name' is required");
    }

    if (receptors == null)
    {
      throw new NeuPathsException("Activator " + name + ": Parameter 'receptors' is required");
    }

    if (receptors.length == 0)
    {
      throw new NeuPathsException("Activator " + name + ": At least one receptor is required");
    }
    
    if (transmitters == null)
    {
      transmitters = new TransmitterSpec[0];
    }

    if (subscriptions == null)
    {
      throw new NeuPathsException("Activator " + name + ": Parameter 'subscriptions' is required");
    }

    if (subscriptions.length == 0)
    {
      throw new NeuPathsException("Activator " + name + ": At least one subscription is required");
    }
    
    this.name = name;
    
    receptorSpecs = new ReceptorSpecSet(receptors);
    
    try
    {
      this.receptors = new Rx_Collection(receptorSpecs);
    }
    catch (Excp_Receptor re)
    {
      throw new NeuPathsException("Activator " + name + ": Failed to create receptor collection", re);
    }
    
    try
    {
      TransmitterSpecSet transmitterSpecs = new TransmitterSpecSet(transmitters);
      
      this.transmitters = new Tx_Collection(transmitterSpecs);
    }
    catch (Excp_Transmitter te)
    {
      throw new NeuPathsException("Activator " + name + ": Failed to create transmitter collection", te);
    }

    subscriptionSpecs = new LogicSubscriptionSpecSet(subscriptions);
    
    evaluateReceptors = null;
    transactionHistory = new HashSet<>();
    
    cell = null;

    transactionHistoryWindowMs = new SafeLong(30_000L);  /* Default 30 seconds */
  }
  
  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

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
    T value = null;

    if (cell != null)
      value = (T) cell.getProperty(name);

    return value;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets a cell property.
   * 
   * @param name  The propery's name.
   * @param value The property's value.
   */
  public final
  void
  setProperty (String name, Object value)
  {
    if (cell != null)
      cell.setProperty(name, value);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets cell properties in a batch.
   * 
   * @param properties The property dictionary to copy.
   */
  public final
  void
  setProperties (PropertySet properties)
  {
    if (cell != null)
      cell.setProperties(properties);
  }
  
  //===========================================================================
  //  PROTECTED METHODS
  //===========================================================================

  /**
   * Returns name of this activator.
   * 
   * @return The activator's name at runtime.
   */
  protected final
  String
  getName ()
  {
    return name;
  }

  /**
   * Returns name of cell hosting this activator.
   * 
   * @return The hosting cell's name at runtime.
   */
  protected final
  String
  getCellName ()
  {
    String cellName = "";
    
    if (cell != null)
      cellName = cell.getName();
    
    return cellName;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Starts stimuli processing.  Override this method to initialize the
   * activator prior to processing.
   */
  protected
  void
  start ()
  {
    // override to initialize Activator prior to evaluation
  }

  //---------------------------------------------------------------------------
  
  /**
   * Evaluates stimuli once all receptors contain stimuli.  Must be implemented in
   * a specialized class.
   */
  protected abstract void evaluate ();

  //---------------------------------------------------------------------------
  
  /**
   * Terminates stimuli processing.  Override this method to clean up after
   * processing terminates.
   */
  protected
  void
  stop ()
  {
    // override to shutdown resources created/initialized in start
  }

  //---------------------------------------------------------------------------
  
  /**
   * Retrieves the evaluation's transaction ID.
   * 
   * @return             The current transaction ID.
   */
  protected final
  UUID
  getTransactionID ()
  {
    if (evaluateReceptors == null)
    {
      throw new NeuPathsException("Activator internal error");
    }

    return evaluateReceptors.getTransactionID();
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Retrieves the stimulus from the specified receptor.
   * 
   * @param <T>          A stimulus type derived from {@link Stimulus}.
   * @param receptorName The receptor from which to retrieve the stimulus.
   * @return             The stimulus.
   */
  protected final
  <T extends Stimulus> T
  getStimulus (String receptorName)
  {
    T s = null;
    
    if (evaluateReceptors == null)
    {
      throw new NeuPathsException("Activator internal error");
    }

    try
    {
      s = evaluateReceptors.getStimulus(receptorName);
    }
    catch (Excp_Receptor re)
    {
      throw new NeuPathsException(re);
    }
    
    return s;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Places the stimulus on the specified transmitter.  A transmitter may emit
   * multiple stimuli during an evaluation.
   * 
   * @param transmitterName The transmitter over which to transmit the stimulus.
   * @param stimulus        The stimulus.
   * @param transactionID   The transaction with which to associate the
   *                        stimulus.  A {@code null} value indicates the
   *                        stimulus is <b>not</b> part of a transaction.
   */
  protected final
  void
  setStimulus
    (String   transmitterName,
     Stimulus stimulus,
     UUID     transactionID)
  {
    try
    {
      transmitters.setStimulus(transmitterName, stimulus);
      stimulus.setTransactionID(transactionID);
      stimulus.setProducerCellID(cell.getInstanceID());
      stimulus.setProducerCellName(cell.getName());
      stimulus.setProducerTransmitterName(transmitterName);
    }
    catch (Excp_Transmitter te)
    {
      throw new NeuPathsException(te);
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Places the stimulus on the specified transmitter.  The stimulus will not
   * be associated with a transaction.  A transmitter may emit multiple stimuli
   * during an evaluation.
   * 
   * @param transmitterName The transmitter over which to transmit the stimulus.
   * @param stimulus        The stimulus.
   */
  protected final
  void
  setStimulus (String transmitterName, Stimulus stimulus)
  {
    setStimulus(transmitterName, stimulus, null);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new transaction and optionally associates a response transaction.
   * The transaction is visible to all activators in the same cell.
   *
   * @param stimulusID            The instance ID of the stimulus that
   *                              triggered/initiated the new transaction.
   * @param responseTransactionID The transaction ID to be used when emitting
   *                              a response stimulus.  A {@code null} value
   *                              indicates there is no response transaction.
   *
   * @return                      The new transaction ID.
   */
  protected final
  UUID
  createTransaction (UUID stimulusID, UUID responseTransactionID)
  {
    UUID transactionID = UUID.randomUUID();

    setStimulus(cell.getInstanceID() + "-C",
                new Stim_CreateTransaction(transactionID,
                                           stimulusID,
                                           responseTransactionID));
    
    return transactionID;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new transaction without a response transaction.  The transaction
   * is visible to all activators in the same cell.
   *
   * @param stimulusID            The instance ID of the stimulus that
   *                              triggered/initiated the new transaction.
   *
   * @return The new transaction ID.
   */
  protected final
  UUID
  createTransaction (UUID stimulusID)
  {
    return createTransaction(stimulusID, null);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Releases resources associated with a transaction.  Use this method after
   * all response stimuli have been sent.
   * 
   * @param transactionID The transaction to terminate.
   */
  protected final
  void
  terminateTransaction (UUID transactionID)
  {
    setStimulus(cell.getInstanceID() + "-T",
                new Stim_TerminateTransaction(transactionID));
  }
  
  //---------------------------------------------------------------------------

  /**
   * Indicates if this activator's cell was the originator of the specified
   * transaction.
   * 
   * @param transactionID The transaction to query.  Specifying {@code null}
   *                      is not considered an error, but {@code true} will be
   *                      returned.
   * @return              {@code true} if this activator or another activator
   *                      in the same cell started the transaction, {@code false}
   *                      otherwise.
   */
  protected final
  boolean
  isTransactionOriginator (UUID transactionID)
  {
    boolean transactionOriginator = false;
    
    if (transactionID == null)
    {
      transactionOriginator = true;
    }
    else
    {
      Rx_TransactionInfo t = receptors.getTransactionInfo(transactionID);

      if (t != null && cell.getInstanceID().equals(t.originatorID))
      {
        transactionOriginator = true;
      }
    }
    
    return transactionOriginator;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Retrieves the response transaction ID for the specified transaction.
   * <p>
   * The {@code isTransactionOriginator}, {@code getResponseTransactionID} and
   * {@code terminateTransaction} methods are designed to work in mainline code
   * whether or not transactions are being used.
   * </p>
   * <p>
   * For example, the following code would work even if no transactions were
   * in use:
   * </p>
   * <pre>
   *   DateStimulus d = getStimulus("Date");
   * 
   *   UUID transID = d.getTransactionID();
   * 
   *   if (isTransactionOriginator(transID)
   *   {
   *     setStimulus("SystemDate", getResponseTransactionID(transID);
   *   }
   * </pre>
   * <p>
   * In this example, {@code d.getTransactionID()} would return {@code null},
   * prompting {@code isTransactionOriginator(transID)} to return {@code true}.
   * Finally, the {@code setStimulus} call would provide {@code null} for the
   * transaction ID (because providing {@code null} to {@code getResponseTransactionID}
   * always returns {@code null}), which is semantically the same as not providing
   * a transaction ID at all.
   * </p>
   * 
   * @param transactionID The transaction to query.  Specifying {@code null}
   *                      is not considered an error, but {@code null} will be
   *                      returned.
   * @return              The response transaction ID or {@code null} if
   *                      no response transaction is associated.
   */
  protected final
  UUID
  getResponseTransactionID (UUID transactionID)
  {
    UUID responseTransactionID = null;
    Rx_TransactionInfo t = receptors.getTransactionInfo(transactionID);
    
    if (t != null)
    {
      responseTransactionID = t.responseTransactionID;
    }
    
    return responseTransactionID;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets the transaction history window duration.
   * <p>
   * The default is 30,000 milliseconds (30 seconds.)  The minimum is 10
   * milliseconds.  Values less than the minimum will be automatically
   * changed to the minimum value.
   * </p>
   * 
   * @param millisecs The window of time maintained in the transaction history.
   */
  protected final
  void
  setTransactionHistoryWindow (long millisecs)
  {
    long ms = millisecs;

    if (ms < 10L)
      ms = 10L;

    transactionHistoryWindowMs.setValue(ms);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Logs an event with details.
   * 
   * @param type    The event type.
   * @param details The event's details.
   */
  protected final
  void
  logEvent (EventType type, String details)
  {
    if (type != EventType.RUNTIME)
    {
      cell.logEvent(type,
                    "Activator " + name + " in " + cell.getName() +
                    " (" + cell.getInstanceID() + ")",
                    details);
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Logs an event with details associated with an exception.
   * 
   * @param type    The event type.
   * @param details The event's details.
   * @param cause   The exception details to be reported.
   */
  protected final
  void
  logEvent (EventType type, String details, Throwable cause)
  {
    if (type != EventType.RUNTIME)
    {
      cell.logEvent(type,
                    "Activator " + name + " in " + cell.getName() +
                    " (" + cell.getInstanceID() + ")",
                    details,
                    cause);
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Logs an event associated with an exception.
   * 
   * @param type  The event type.
   * @param cause The exception details to be reported.
   */  
  protected final
  void
  logEvent (EventType type, Throwable cause)
  {
    if (type != EventType.RUNTIME)
    {
      cell.logEvent(type,
                    "Activator " + name + " in " + cell.getName() +
                    " (" + cell.getInstanceID() + ")",
                    cause);
    }
  }
  
  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  void
  activate ()
  {
    evaluate();
  }
  
  //---------------------------------------------------------------------------
  
  final
  void
  updateLoopbackSubscriptions ()
  {
    for (SubscriptionSpec subscription : subscriptionSpecs)
    {
      if (subscription.isLoopback())
      {
        subscription.setCellName(cell.getName());
      }
    }
  }
  
  //---------------------------------------------------------------------------
  
  final
  LogicSubscriptionSpecSet
  getSubscriptions ()
  {
    return subscriptionSpecs;
  }
  
  //---------------------------------------------------------------------------
  
  final
  boolean
  isInterested (Stimulus stimulus)
  {
    boolean interested = false;

    Nuc_SubscriptionInfo producerInfo = new Nuc_SubscriptionInfo(stimulus);
    
    for (SubscriptionSpec subscription : subscriptionSpecs)
    {
      if (producerInfo.matches(subscription))
      {
        interested = true;
        break;
      }  // if subscription matches (regex)
    }  // for each subscription
    
    return interested;
  }
  
  //---------------------------------------------------------------------------

  final
  boolean
  isDuplicate (UUID    transactionID,
               UUID    typeID,
               boolean filterTransactions)
  {
    boolean duplicate = false;

    if (transactionID != null && filterTransactions)
    {
      Rx_TransactionInfo tinfo = receptors.getTransactionInfo(transactionID);

      long currentTime = System.currentTimeMillis();
      HistoryEntry newEntry = new HistoryEntry(transactionID,
                                               tinfo.originatorID,
                                               tinfo.stimulusID,
                                               typeID,
                                               currentTime);
  
      LinkedList<HistoryEntry> toBePurged = new LinkedList<>();
  
      // Identify entries to be purged (older than window interval)
      for (HistoryEntry entry : transactionHistory)
      {
        if (currentTime - entry.timestamp >
            transactionHistoryWindowMs.getValue())
        {
          toBePurged.add(entry);
        }
      }
  
      // Purge old entries
      for (HistoryEntry entry : toBePurged)
      {
        transactionHistory.remove(entry);
      }
  
      // Now check if transaction-specific stimulus has already been received
      // in the recent past
      if (transactionHistory.contains(newEntry))
      {
        duplicate = true;
      }
      else
      {
        transactionHistory.add(newEntry);
      }
    } // if transaction != null

    return duplicate;
  }
  
  //---------------------------------------------------------------------------
  
  final
  void
  evaluateStimulus (Stimulus stimulus)
  {
    cell.logEvent(EventType.RUNTIME,
                  "Activator " + name + " in " + cell.getName() +
                  " (" + cell.getInstanceID() + ")",
                  "RUNTIME;ACTIVATOR;EVALUATE;1;" + stimulus.getInstanceID() + ";" + stimulus);
    
    try
    {
      if (stimulus instanceof Stim_CreateTransaction)
      {
        Stim_CreateTransaction createTrans = (Stim_CreateTransaction) stimulus;

        receptors.addTransaction(createTrans.transactionID,
                                 receptorSpecs,
                                 cell.getInstanceID(),
                                 createTrans.stimulusID,
                                 createTrans.responseTransactionID,
                                 true); // created locally
      }
      else if (stimulus instanceof Stim_TerminateTransaction)
      {
        Stim_TerminateTransaction termTrans = (Stim_TerminateTransaction) stimulus;

        receptors.removeTransaction(termTrans.transactionID);
      }
      else
      {
        Nuc_SubscriptionInfo producerInfo = new Nuc_SubscriptionInfo(stimulus);
        UUID transactionID = stimulus.getTransactionID();

        Rx_Transaction transaction = receptors.getTransaction(transactionID);

        if (transaction == null)
        {
          transaction =
              receptors.addTransaction(transactionID,
                                       receptorSpecs,
                                       stimulus.getProducerCellID(),
                                       stimulus.getInstanceID(),
                                       null,   // responseTransactionID
                                       false); // not created locally
        }

        SubscriptionLoop:
        for (SubscriptionSpec subscription : subscriptionSpecs)
        {
          if ( producerInfo.matches(subscription) &&
               !isDuplicate(transactionID,
                            stimulus.getTypeID(),
                            subscription.filterTransactionResults()) )
          {
            if (subscription.getType() == SubscriptionType.MAP)
            {
              transaction.setStimulus(stimulus.getProducerTransmitterName(), stimulus);
            }
            else
            {
              transaction.setStimulus(subscription.getReceptorName(), stimulus);
            }

            if (transaction.isComplete())
            {
              if (cell.isRuntimeLoggingEnabled())
              {
                String receptorDepths = "";
                
                for (Rx_Receptor r : receptors.getTransaction(null))
                {
                  receptorDepths += (r.getName() + "[" + r.depth() + "]  ");
                }
          
                cell.logEvent(EventType.RUNTIME,
                              "Activator " + name + " in " + cell.getName() +
                              " (" + cell.getInstanceID() + ")",
                              "Receptor Depths(" + cell.getName() + "/" + name + ")  " + receptorDepths);
              }

              // Take a snapshot
              evaluateReceptors = transaction.getSnapshot();

              // Evaluate the stimuli
              activate();

              // Delete the completed transaction
              receptors.removeTransaction(transactionID);

              // Transmit the output stimuli
              for (String t : transmitters)
              {
                while (transmitters.hasStimulus(t))
                {
                  cell.transmitStimulus(transmitters.getStimulus(t),
                                        transmitters.isTraceEnabled(t));
                }  // while transmitter has stimuli
              }  // for each transmitter
            }  // if transaction is complete
          }  // if subscription matches (regex)
        }  // for each subscription
      }  // if stimulus instanceof Stim_CreateTransaction
    }
    catch (Excp_Receptor re)
    {
      throw new NeuPathsException(re);
    }
    catch (Excp_Transmitter te)
    {
      throw new NeuPathsException(te);
    }
  }
  
  //---------------------------------------------------------------------------
  
  final
  void
  setCell (Cell cell)
  {
    this.cell = cell;
    
    try
    {
      subscriptionSpecs.add(this.cell.getInstanceID() + "-C",
                            "CreateTransaction");
      subscriptionSpecs.add(this.cell.getInstanceID() + "-T",
                            "TerminateTransaction");
      
      transmitters.insertTransmitter(
          new TransmitterSpec(this.cell.getInstanceID() + "-C",
                              Stim_CreateTransaction.TYPE_ID));
      transmitters.insertTransmitter(
          new TransmitterSpec(this.cell.getInstanceID().toString() + "-T",
                              Stim_TerminateTransaction.TYPE_ID));
    }
    catch (Excp_Transmitter te)
    {
      // Ignore.  Cannot happen.
    }
  }
  
  //---------------------------------------------------------------------------
  
  final
  Cell
  getCell ()
  {
    return cell;
  }

  //---------------------------------------------------------------------------

  final
  void
  addReceptor (ReceptorSpec receptor)
  {
    try
    {
      receptorSpecs.add(receptor);
      
      receptors.addReceptors(new ReceptorSpecSet(receptor));
    }
    catch (Excp_Receptor re)
    {
      throw new NeuPathsException(re);
    }
  }
  
  //---------------------------------------------------------------------------

  final
  void
  addTransmitter (TransmitterSpec transmitter)
  {
    try
    {
      transmitters.addTransmitters(new TransmitterSpecSet(transmitter));
    }
    catch (Excp_Transmitter te)
    {
      throw new NeuPathsException(te);
    }
  }
  
  //---------------------------------------------------------------------------

  final
  void
  addSubscription (LogicSubscriptionSpec subscription)
  {
    subscriptionSpecs.add(subscription);
  }
  
  //---------------------------------------------------------------------------
  
  final
  void
  clearTransactions ()
  {
    receptors.clear();
  }
  
  //---------------------------------------------------------------------------

  // For sending a stimulus outside of normal evaluation
  final
  void
  postStimulus (Stimulus stimulus, String transmitterName, UUID transactionID)
  {
    stimulus.setProducerCellID(cell.getInstanceID());
    stimulus.setProducerCellName(cell.getName());
    stimulus.setProducerTransmitterName(transmitterName);
    stimulus.setTransactionID(transactionID);

    cell.transmitStimulus(stimulus, true);
  }
  
  //---------------------------------------------------------------------------

  // For sending stimuli outside of normal evaluation
  final
  void
  postStimuli () throws Excp_Transmitter
  {
    for (String t : transmitters)
    {
      while (transmitters.hasStimulus(t))
      {
        cell.transmitStimulus(transmitters.getStimulus(t),
                              transmitters.isTraceEnabled(t));
      }  // while transmitter has stimuli
    }  // for each transmitter
  }
  
  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private class HistoryEntry
  {
    HistoryEntry (UUID transactionID,
                  UUID originatorID,
                  UUID stimulusID,
                  UUID typeID,
                  long timestamp)
    {
      this.transactionID = transactionID;
      this.originatorID = originatorID;
      this.stimulusID = stimulusID;
      this.typeID = typeID;
      this.timestamp = timestamp;
    }

    @Override
    public
    int
    hashCode ()
    {
      String concat = transactionID.toString() +
                      originatorID +
                      stimulusID +
                      typeID;

      return concat.hashCode();
    }

    @Override
    public
    String
    toString ()
    {
      String image = "[" +
                     transactionID + "/" +
                     originatorID + "/" +
                     stimulusID + "/" +
                     typeID + "/" +
                     timestamp + "]";

      return image;
    }

    @Override
    public
    boolean
    equals (Object obj)
    {
      if (obj == null)
      {
        return false;
      }
  
      if (getClass() != obj.getClass())
      {
        return false;
      }
  
      final HistoryEntry other = (HistoryEntry) obj;

      if ((this.transactionID == null) ?
          (other.transactionID != null) :
          !this.transactionID.equals(other.transactionID))
      {
        return false;
      }

      if ((this.originatorID == null) ?
          (other.originatorID != null) :
          !this.originatorID.equals(other.originatorID))
      {
        return false;
      }

      if ((this.stimulusID == null) ?
          (other.stimulusID != null) :
          !this.stimulusID.equals(other.stimulusID))
      {
        return false;
      }

      if ((this.typeID == null) ?
          (other.typeID != null) :
          !this.typeID.equals(other.typeID))
      {
        return false;
      }

      return true;
    }

    UUID transactionID;
    UUID originatorID;
    UUID stimulusID;
    UUID typeID;
    long timestamp;
  }

  ReceptorSpecSet receptorSpecs;
  LogicSubscriptionSpecSet subscriptionSpecs;
  
  private String name;
  private Rx_Collection receptors;
  private Tx_Collection transmitters;
  private Rx_Transaction evaluateReceptors;
  private HashSet<HistoryEntry> transactionHistory;

  private Cell cell;

  private SafeLong transactionHistoryWindowMs;
}
