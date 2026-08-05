// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * NeuPaths cell type for extracting stimuli from a NeuPaths cell system.
 * 
 * @author Aaron Caraveo
 */
public class ExtractorCell extends Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code ExtractorCell} object with a single synapse.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param subscription  The subscription this cell advertises.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  ExtractorCell
    (String                    name,
     String                    synapseName,
     ExtractorSubscriptionSpec subscription,
     byte[]                    cryptoKey)
  {
    this(name,
         new String[] { synapseName },
         subscription,
         cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code ExtractorCell} object with multiple synapses.
   * 
   * @param name           The cell's name at runtime.  This name should be
   *                       unique across the entire cell system.
   * @param synapseNames   The synapses this cell listens on and/or
   *                       connects to.
   * @param subscription   The subscription this cell advertises.
   * @param cryptoKey      The stimulus encryption key.  Specify {@code null}
   *                       to disable encryption using a user-specified
   *                       key.  If disabled, the stimuli will still be
   *                       encrypted as part of NeuPaths protocol encryption.
   */
  public
  ExtractorCell
    (String                    name,
     String[]                  synapseNames,
     ExtractorSubscriptionSpec subscription,
     byte[]                    cryptoKey)
  {
    super(CellType.EXTRACTOR,
          name,
          new PropertySet(),
          synapseNames,
          cryptoKey);

    if (subscription == null)
    {
      throw new NeuPathsException(CellType.EXTRACTOR,
                                  name,
                                  "Parameter 'subscription' is required");
    }

    this.subscription = subscription;

    stimuliSemaphore = new Semaphore(0, true);
    stimuliQueue = new LinkedList<Stimulus>();
    stimuliMutex = new ReentrantLock(true);
    transactionHistory = new HashSet<>();
    
    addSubscription(subscription);

    transactionHistoryWindowMs = new SafeLong(30_000L);  /* Default 30 seconds */
  }

  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  /**
   * Extracts the next available stimulus from the cell system.  This method
   * blocks until a stimulus matching the subscription arrives.
   * 
   * @param <T> A stimulus type derived from {@code Stimulus}.
   * @return    The stimulus.
   */
  @SuppressWarnings("unchecked")
  public final
  <T extends Stimulus> T
  extract ()
  {
    T stimulus = null;
    
    while (terminateActivators.isNotSet())
    {
      try
      {
        stimulus = (T) acceptStimulus();

        Nuc_SubscriptionInfo subInfo = new Nuc_SubscriptionInfo(stimulus);

        if (subInfo.matches(subscription))
          break;
      }
      catch (InterruptedException ie)
      {
        // Try again
      }
    }
    
    return stimulus;
  }
  
  /**
   * Extracts the next available stimulus from the cell system.  This method
   * blocks until a stimulus matching the subscription arrives or the timeout
   * expires.
   * 
   * @param <T>        A stimulus type derived from {@code Stimulus}.
   * @param timeoutMs  The number of milliseconds to wait for a stimulus.
   * @return           The stimulus or {@code null} if request expired.
   */
  @SuppressWarnings("unchecked")
  public final
  <T extends Stimulus> T
  extract (long timeoutMs)
  {
    T stimulus = null;
    
    while (terminateActivators.isNotSet())
    {
      try
      {
        stimulus = (T) acceptStimulus(timeoutMs);

        if (stimulus == null)
          break;

        Nuc_SubscriptionInfo subInfo = new Nuc_SubscriptionInfo(stimulus);

        if (subInfo.matches(subscription))
          break;
      }
      catch (InterruptedException ie)
      {
        // Try again
      }
    }
    
    return stimulus;
  }
  
  //---------------------------------------------------------------------------

  /**
   * Extracts the next available stimulus in the specified transaction from
   * the cell system.  This method blocks until a stimulus matching the
   * subscription and transaction arrives.
   * 
   * @param <T>           A stimulus type derived from {@code Stimulus}.
   * @param transactionID The transaction to wait for.
   * @return              The stimulus.
   */  
  @SuppressWarnings("unchecked")
  public final
  <T extends Stimulus> T
  extractFromTransaction (UUID transactionID)
  {
    T stimulus = null;
    
    while (terminateActivators.isNotSet())
    {
      try
      {
        stimulus = (T) acceptStimulus();

        Nuc_SubscriptionInfo subInfo = new Nuc_SubscriptionInfo(stimulus);

        if ( subInfo.matches(subscription) &&
             transactionID.equals(stimulus.getTransactionID()) &&
             !isDuplicate(transactionID,
                          stimulus.getTypeID()) )
        {
          break;
        }
        else
        {
          logEvent(EventType.RUNTIME,
                   getName(),
                   "Extractor ignored: " +
                   stimulus.getTypeName() + "/" +
                   stimulus.getProducerCellName() + "/" +
                   stimulus.getProducerTransmitterName() + "/" +
                   stimulus.getTransactionID());
        }
      }
      catch (InterruptedException ie)
      {
        // Try again
      }
    }
    
    return stimulus;
  }
  
  //---------------------------------------------------------------------------

  /**
   * Extracts the next available stimulus in the specified transaction from
   * the cell system.  This method blocks until a stimulus matching the
   * subscription and transaction arrives or the timeout expires.
   * 
   * @param <T>           A stimulus type derived from {@code Stimulus}.
   * @param transactionID The transaction to wait for.
   * @param timeoutMs     The number of milliseconds to wait for a stimulus.
   * @return              The stimulus or {@code null} if request expired.
   */  
  @SuppressWarnings("unchecked")
  public final
  <T extends Stimulus> T
  extractFromTransaction (UUID transactionID, long timeoutMs)
  {
    T stimulus = null;
    
    while (terminateActivators.isNotSet())
    {
      try
      {
        stimulus = (T) acceptStimulus(timeoutMs);

        if (stimulus == null)
          break;

        Nuc_SubscriptionInfo subInfo = new Nuc_SubscriptionInfo(stimulus);

        if ( subInfo.matches(subscription) &&
             transactionID.equals(stimulus.getTransactionID()) &&
             !isDuplicate(transactionID,
                          stimulus.getTypeID()) )
        {
          break;
        }
        else
        {
          logEvent(EventType.RUNTIME,
                   getName(),
                   "Extractor ignored: " +
                   stimulus.getTypeName() + "/" +
                   stimulus.getProducerCellName() + "/" +
                   stimulus.getProducerTransmitterName() + "/" +
                   stimulus.getTransactionID());
        }
      }
      catch (InterruptedException ie)
      {
        // Try again
      }
    }
    
    return stimulus;
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
  public
  void
  setTransactionHistoryWindow (long millisecs)
  {
    long ms = millisecs;

    if (ms < 10L)
      ms = 10L;

    transactionHistoryWindowMs.setValue(ms);
  }

  //===========================================================================
  //  PROTECTED METHODS
  //===========================================================================

  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  @Override
  final
  void
  receiveStimulus (Stimulus stimulus)
  {
    super.receiveStimulus(stimulus);

    announceStimulus(stimulus);
  }
  
  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  final
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

  //---------------------------------------------------------------------------
  
  final
  Stimulus
  acceptStimulus ()
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

  //---------------------------------------------------------------------------
  
  final
  Stimulus
  acceptStimulus (long timeoutMs)
    throws InterruptedException
  {
    Stimulus stimulus = null;

    if (stimuliSemaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS))
    {
      stimuliMutex.lock();
      try
      {
        stimulus = stimuliQueue.pollFirst();
      }
      finally
      {
        stimuliMutex.unlock();
      }
    }

    return stimulus;
  }

  //---------------------------------------------------------------------------

  final
  boolean
  isDuplicate (UUID transactionID,
               UUID typeID)
  {
    boolean duplicate = false;

    if (transactionID != null && subscription.filterTransactionResults())
    {
      long currentTime = System.currentTimeMillis();
      HistoryEntry newEntry = new HistoryEntry(transactionID,
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
  
  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private class HistoryEntry
  {
    HistoryEntry (UUID transactionID,
                  UUID typeID,
                  long timestamp)
    {
      this.transactionID = transactionID;
      this.typeID = typeID;
      this.timestamp = timestamp;
    }

    @Override
    public
    int
    hashCode ()
    {
      String concat = transactionID.toString() + typeID;

      return concat.hashCode();
    }

    @Override
    public
    String
    toString ()
    {
      String image = "[" +
                     transactionID + "/" +
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

      if ((this.typeID == null) ?
          (other.typeID != null) :
          !this.typeID.equals(other.typeID))
      {
        return false;
      }

      return true;
    }

    UUID transactionID;
    UUID typeID;
    long timestamp;
  }

  private ExtractorSubscriptionSpec subscription;
  private Semaphore stimuliSemaphore;
  private LinkedList<Stimulus> stimuliQueue;
  private ReentrantLock stimuliMutex;
  private HashSet<HistoryEntry> transactionHistory;

  private SafeLong transactionHistoryWindowMs;
}
