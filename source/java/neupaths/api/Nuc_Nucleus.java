// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The cell nucleus.
 *
 * @author Aaron Caraveo
 */
final class Nuc_Nucleus
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  Nuc_Nucleus
    (String          cellName,
     UUID            cellInstanceID,
     HashSet<String> synapseNames,
     byte[]          cryptoKey)
    throws Excp_Nucleus
  {
    //-------------------------------------------------------------------------
    // Validate parameters
    //-------------------------------------------------------------------------
    
    if (cellName == null)
    {
      throw new Excp_Nucleus("Parameter 'cellName' is required");
    }

    if (cellInstanceID == null)
    {
      throw new Excp_Nucleus("Parameter 'cellInstanceID' is required");
    }

    if (synapseNames == null)
    {
      throw new Excp_Nucleus(cellName,
                             cellInstanceID,
                             "Parameter 'synapseNames' is required");
    }

    this.cellName = cellName;
    this.cellInstanceID = cellInstanceID;

    subscriptions = new SubscriptionSpecSet();
    state = Nuc_State.OFFLINE;

    //-------------------------------------------------------------------------
    // Create binders
    //-------------------------------------------------------------------------
    
    binders = new HashMap<>();
    
    try
    {
      Bnd_Binder binder = null;
      for (String synapseName : synapseNames)
      {
        binder = Bnd_Factory.createBinder(cellName,
                                          cellInstanceID,
                                          synapseName,
                                          subscriptions);

        binder.setNucleus(this);
        binders.put(binder.getInstanceID(), binder);
      }
    }
    catch (Excp_Binder be)
    {
      throw new Excp_Nucleus(cellName,
                             cellInstanceID,
                             "Could not create binder",
                             be);
    }

    //-------------------------------------------------------------------------
    // Initialize message processing
    //-------------------------------------------------------------------------

    recvQueue = new LinkedList<>();
    recvQueueMutex = new ReentrantLock(true);
    recvSemaphore = new Semaphore(0, true);

    xmitQueue = new LinkedList<>();
    xmitQueueMutex = new ReentrantLock(true);
    xmitSemaphore = new Semaphore(0, true);

    forwardedSubscriptions = new HashMap<>();
    forwardedSubscriptionsMutex = new ReentrantLock(true);

    stimuliHistory = new HashMap<>();
    stimuliHistoryMutex = new ReentrantLock(true);

    //-------------------------------------------------------------------------
    // Initialize peer processing
    //-------------------------------------------------------------------------

    peerMap = new HashMap<>();
    peerMutex = new ReentrantLock(true);

    subscriptionMap = new HashMap<>();
    subscriptionMutex = new ReentrantLock(true);

    // Add Cdet Cycle Detection subscription to every cell for routing
    subscriptionMap.put(new Nuc_SubscriptionInfo(".*",  // any cell
                                                 Cdet.CDET_DETECTION_TRANSMITTER,
                                                 Syn.GLOBAL_DOMAIN),
                        new HashSet<>());
    
    //-------------------------------------------------------------------------
    // Initialize cipher
    //-------------------------------------------------------------------------

    try
    {
      String disableCipher = System.getenv("NEUPATHS_DISABLE_CIPHER");
      
      if (disableCipher == null ||
          disableCipher.equals("n") ||
          disableCipher.equals("N"))
      {
        if (cryptoKey != null)
        {
          cipher = Cryp_Factory.createCipher(cryptoKey);
        }
        else
        {
          cipher = new Cryp_Stim();
        }
      }
      else
      {
        cipher = new Cryp_Null();
      }
    }
    catch (Excp_Cipher ce)
    {
      throw new Excp_Nucleus(cellName,
                             cellInstanceID,
                             "Could not create cipher",
                             ce);
    }
    
    //-------------------------------------------------------------------------
    // Initialize threads
    //-------------------------------------------------------------------------

    recvThread = null;
    xmitThread = null;
    historyThread = null;
    subTraceThread = null;

    terminate = new SafeBoolean();

    cell = null;
    
    duplicateDetectionIntervalMs = new SafeLong(1000L);  // Default interval of 1 sec
    subscriptionRefreshIntervalMs = new SafeLong(1500L); // Default refresh of 1.5 sec
    subscriptionTraceIntervalMs = new SafeLong(0L);      // Disabled by default

    propagateGlobalSubs = new SafeBoolean(true);         // Enabled by default
  }
  
  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  //===========================================================================
  //  PROTECTED METHODS
  //===========================================================================

  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  synchronized
  CellState
  getState ()
  {
    CellState rollupState = CellState.OFFLINE;
    
    if (state == Nuc_State.ONLINE)
    {
      int onlineBinders = 0;
      
      for (Bnd_Binder binder : binders.values())
      {
        if (binder.getState() == CellState.ONLINE)
        {
          onlineBinders++;
        }
      }
      
      if (onlineBinders == binders.values().size())
      {
        rollupState = CellState.ONLINE;
      }
      else
      {
        rollupState = CellState.DEGRADED;
      }
    }
    
    return rollupState;
  }
  
  //---------------------------------------------------------------------------

  void
  setDuplicateDetectionInterval (long millisecs)
  {
    long ms = millisecs;
    
    if (ms < 250L)
      ms = 250L;
    
    duplicateDetectionIntervalMs.setValue(ms);
  }
  
  //---------------------------------------------------------------------------
  
  void
  setSubscriptionRefreshInterval (long millisecs)
  {
    subscriptionRefreshIntervalMs.setValue(millisecs);
    
    for (Bnd_Binder binder : binders.values())
    {
      binder.setSubscriptionRefreshInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------
  
  void
  setSubscriptionTraceInterval (long millisecs)
  {
    long ms = millisecs;
    
    if (ms < 0L)
      ms = 0L;
    
    subscriptionTraceIntervalMs.setValue(ms);
  }
  
  //---------------------------------------------------------------------------
  
  void
  enableGlobalSubscriptionPropagation ()
  {
    propagateGlobalSubs.set();
  }

  //---------------------------------------------------------------------------
  
  void
  disableGlobalSubscriptionPropagation ()
  {
    propagateGlobalSubs.clear();
  }

  //---------------------------------------------------------------------------
  
  synchronized
  void
  publishSubscriptions ()
  {
    for (Bnd_Binder binder : binders.values())
    {
      binder.sendSubscriptions(cellName + " (" +
                               cellInstanceID + ") Nucleus Main");
    }    
  }

  //---------------------------------------------------------------------------

  void
  start ()
  {
    if (state == Nuc_State.OFFLINE)
    {
      // Clear terminate indicator
      terminate.clear();

      // Start receive thread
      recvThread = new ReceiveThread();
      recvThread.start();

      // Start transmit thread
      xmitThread = new TransmitThread();
      xmitThread.start();

      // Start history thread
      historyThread = new StimuliHistoryThread();
      historyThread.start();

      // Start subscription trace thread
      if (subscriptionTraceIntervalMs.getValue() > 0L)
      {
        subTraceThread = new SubscriptionTraceThread();
        subTraceThread.start();
      }

      // Start the binders
      for (Bnd_Binder binder : binders.values())
      {
        binder.start();
      }
      
      state = Nuc_State.ONLINE;
    }
  }
  
  //---------------------------------------------------------------------------

  void
  stop ()
  {
    if (state == Nuc_State.ONLINE)
    {
      // Stop the binders
      for (Bnd_Binder binder : binders.values())
      {
        binder.stop();
      }
      
      // Tell threads to terminate
      terminate.set();

      // Interrupt threads in case they're sleeping
      recvThread.interrupt();
      xmitThread.interrupt();
      historyThread.interrupt();
      if (subTraceThread != null)
      {
        subTraceThread.interrupt();
        subTraceThread = null;
      }

      recvThread = null;
      recvSemaphore.drainPermits();
      
      xmitThread = null;
      xmitSemaphore.drainPermits();

      historyThread = null;

      state = Nuc_State.OFFLINE;
    }
  }

  //---------------------------------------------------------------------------

  // Use after construction to add subscriptions for each Activator
  void
  addSubscriptions (SubscriptionSpecSet subs)
  {
    subscriptions.add(subs);
  }
  
  //---------------------------------------------------------------------------

  // Use after construction to add subscriptions for each Activator
  void
  addSubscriptions (SubscriptionSpec[] subs)
  {
    subscriptions.add(subs);
  }
  
  //---------------------------------------------------------------------------

  // Use after construction to add subscriptions for each Activator
  void
  addSubscription (SubscriptionSpec sub)
  {
    subscriptions.add(sub);
  }
  
  //---------------------------------------------------------------------------

  void
  setCell (Cell cell)
  {
    this.cell = cell;
  }
  
  //---------------------------------------------------------------------------

  Cell
  getCell ()
  {
    return cell;
  }
  
  //---------------------------------------------------------------------------

  void
  handlePeerJoin
    (String  peerCellName,
     UUID    synapseInstanceID,
     String  synapseDomain,
     UUID    binderInstanceID)
  {
    Nuc_SubscriptionInfo cdetSub =
        new Nuc_SubscriptionInfo(".*",
                                 Cdet.CDET_DETECTION_TRANSMITTER,
                                 Syn.GLOBAL_DOMAIN);

    cell.logEvent(EventType.RUNTIME,
                  cellName + " (" + cellInstanceID + ") Nucleus Main",
                  "RUNTIME;NUCLEUS;JOIN;1;" + synapseInstanceID + " in domain " + synapseDomain + " joined");
    
    peerMutex.lock();
    try
    {
      Nuc_PeerInfo peerInfo = new Nuc_PeerInfo(peerCellName,
                                               synapseDomain,
                                               binderInstanceID);
      
      peerMap.put(synapseInstanceID,
                  peerInfo);

      // Add peer to Cdet Cycle Detection subscription
      HashSet<UUID> peerList = subscriptionMap.get(cdetSub);

      if (peerList != null)
        peerList.add(synapseInstanceID);
      
      // Add Cdet Cycle Detection subscription to the peer
      peerInfo.subscriptions.add(cdetSub);
    }
    finally
    {
      peerMutex.unlock();
    }
  }
  
  //---------------------------------------------------------------------------

  void
  handlePeerLeave (UUID synapseInstanceID)
  {
    peerMutex.lock();
    try
    {
      peerMap.remove(synapseInstanceID);
    }
    finally
    {
      peerMutex.unlock();
    }

    subscriptionMutex.lock();
    try
    {
      for (HashSet<UUID> peerList : subscriptionMap.values())
      {
        peerList.remove(synapseInstanceID);
      }  // for each subscription
    }
    finally
    {
      subscriptionMutex.unlock();
    }
  }
  
  //---------------------------------------------------------------------------

  void
  receiveMessage (Msg_NeuPaths msg)
  {
    recvQueueMutex.lock();
    try
    {
      recvQueue.addLast(msg);
    }
    finally
    {
      recvQueueMutex.unlock();
    }
    
    recvSemaphore.release();
  }

  //---------------------------------------------------------------------------

  void
  transmitMessage (UUID synapseInstanceID, Msg_NeuPaths msg)
  {
    xmitQueueMutex.lock();
    try
    {
      xmitQueue.addLast(new Nuc_Transmit(synapseInstanceID, msg));
    }
    finally
    {
      xmitQueueMutex.unlock();
    }
    
    xmitSemaphore.release();
  }

  //---------------------------------------------------------------------------

  void
  receiveStimulus (Stimulus stimulus, boolean trace)
  {
    try
    {
      Msg_Stimulus stimulusMsg =
          new Msg_Stimulus(stimulus.getProducerCellName(),
                           stimulus.getProducerTransmitterName(),
                           stimulus.getTypeName(),
                           stimulus.getTypeID(),
                           stimulus.getInstanceID(),
                           stimulus.getTransactionID(),
                           cipher.encrypt(stimulus));

      if (trace)
        stimulusMsg.enableTrace();

      receiveMessage(stimulusMsg);
    }
    catch (Excp_Cipher ce)
    {
      cell.logEvent(EventType.ERROR,
                    cellName + " (" + cellInstanceID + ") Nucleus Main",
                    ce);
    }
  }
  
  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  private String
  formatStimulusTrace (Msg_Stimulus s)
  {
    boolean first = true;
    String trace = "";
    
    if (s.traceEnabled)
    {
      for (Stim_Trace t : s.trace)
      {
        if (first)
        {
          trace += t.cellName;
          first = false;
        }
        else
        {
          trace += (" => " + t.cellName);
        }

        if (t.domainName != null)
        {
          trace += " (" + t.domainName + ")";
        }
      }
    }
    
    return trace;
  }

  //---------------------------------------------------------------------------

  private void
  logStimulusIngressTrace (Msg_Stimulus s)
  {
    if (s.traceEnabled)
    {
      String transactionInfo = "";

      if (s.transactionID != null)
      {
        transactionInfo = " in transaction " + s.transactionID;
      }
      
      String traceInfo = formatStimulusTrace(s);
      
      cell.logEvent(EventType.TRACE,
                    cellName + " (" + cellInstanceID + ") INGRESS",
                    "Stimulus trace for type '" + s.typeName +
                    "' (" + s.instanceID + ") on transmitter '" +
                    s.producerTransmitterName + "'" + transactionInfo +
                    ": " + traceInfo);
    }
  }  // end logStimulusIngressTrace

  //---------------------------------------------------------------------------

  private void
  logStimulusEgressTrace (Msg_Stimulus s, UUID peerSynapseInstanceID)
  {
    if (s.traceEnabled)
    {
      String transactionInfo = "";

      if (s.transactionID != null)
      {
        transactionInfo = " in transaction " + s.transactionID;
      }
      
      String traceInfo = formatStimulusTrace(s);
      
      cell.logEvent(EventType.TRACE,
                    cellName + " (" + cellInstanceID + ") EGRESS",
                    "Stimulus trace for type '" + s.typeName +
                    "' (" + s.instanceID + ") on synapse " +
                    peerSynapseInstanceID + transactionInfo +
                    ": " + traceInfo);
    }
  }  // end logStimulusEgressTrace

  //---------------------------------------------------------------------------

  private
  boolean
  alreadySubscribed
    (UUID                 synapseInstanceID,
     Nuc_SubscriptionInfo subscription)
  {
    boolean alreadySubscribed = false;
    
    subscriptionMutex.lock();
    try
    {
      HashSet<UUID> peerList = subscriptionMap.get(subscription);

      if (peerList != null)
      {
        alreadySubscribed = peerList.contains(synapseInstanceID);
      }
    }
    finally
    {
      subscriptionMutex.unlock();
    }
    
    return alreadySubscribed;
  }
  
  //---------------------------------------------------------------------------

  private
  boolean
  alreadyForwarded
    (UUID                  synapseInstanceID,
     ForwardedSubscription subscription)
  {
    boolean alreadyForwarded = false;
    Long currentTime = System.currentTimeMillis();
    HashSet<ForwardedSubscription> toBePurged = new HashSet<ForwardedSubscription>();
    
    forwardedSubscriptionsMutex.lock();
    try
    {
      HashSet<ForwardedSubscription> subList =
        forwardedSubscriptions.get(synapseInstanceID);
      
      if (subList != null)
      {
        // Identify entries to be purged
        for (ForwardedSubscription sub : subList)
        {
          if (currentTime - sub.timestamp > (subscriptionRefreshIntervalMs.getValue() / 2L))
            toBePurged.add(sub);
        }
        
        // Purge old entries
        for (ForwardedSubscription sub : toBePurged)
        {
          subList.remove(sub);
        }
        
        // Now check if subscription was already forwarded
        if (subList.contains(subscription))
        {
          alreadyForwarded = true;
        }
      }
    }
    finally
    {
      forwardedSubscriptionsMutex.unlock();
    }
    
    return alreadyForwarded;
  }

  //---------------------------------------------------------------------------

  private
  void
  subscribe (Msg_Subscription msg, String threadName)
  {
    Nuc_SubscriptionInfo sub = new Nuc_SubscriptionInfo(msg);
    ForwardedSubscription fwdSub = new ForwardedSubscription(sub);

    // Update nucleus subscriptionMap
    subscriptionMutex.lock();
    try
    {
      HashSet<UUID> peerList = subscriptionMap.get(sub);

      if (peerList == null)
      {
        cell.logEvent(EventType.RUNTIME,
                      threadName + "[subscribe]",
                      "RUNTIME;NUCLEUS;SUBSCRIBE;1;" + cellName + ";" + msg.instanceID + ";" + sub + ";first peer");

        peerList = new HashSet<UUID>();

        subscriptionMap.put(sub, peerList);
      }

      // Add subscription to nucleus subscription map
      // (HashSet ignores duplicates)
      peerList.add(msg.arrivalSynapseInstanceID);
    }
    finally
    {
      subscriptionMutex.unlock();
    }

    // Update peer's subscription list and forward the request
    peerMutex.lock();
    try
    {
      Nuc_PeerInfo peerInfo = peerMap.get(msg.arrivalSynapseInstanceID);

      if (peerInfo != null)
      {
        cell.logEvent(EventType.RUNTIME,
                      threadName + "[subscribe]",
                      "RUNTIME;NUCLEUS;SUBSCRIBE;2;" + cellName + ";" + msg.instanceID + ";" + sub + ";peer: " + peerInfo);

        peerInfo.subscriptions.add(sub);

        UUID    targetPeerSynapseInstanceID = null;
        String  targetPeerSynapseDomain = null;

        for (Map.Entry<UUID, Nuc_PeerInfo> peer : peerMap.entrySet())
        {
          targetPeerSynapseInstanceID = peer.getKey();
          targetPeerSynapseDomain = peer.getValue().synapseDomain;

          // Forward if all of the following conditions are met:
          // 1. Subscription is not for this cell.
          // 2. Target peer is not the sender of the subscription.
          // 3. Target peer is in the subscription domain OR
          //    subscription domain is Global and propagation is allowed.
          // 4. Target peer has not already subscribed for same thing.
          // 5. Subscription has not aready been forwarded to target peer.
          if ( !cellName.equals(sub.cellName) &&
               !targetPeerSynapseInstanceID.equals(msg.arrivalSynapseInstanceID) &&
               ( targetPeerSynapseDomain.equals(sub.domain) ||
                 (sub.domain.equals(Syn.GLOBAL_DOMAIN) &&
                  propagateGlobalSubs.isSet()) ) &&
               !alreadySubscribed(targetPeerSynapseInstanceID, sub) &&
               !alreadyForwarded(targetPeerSynapseInstanceID, fwdSub) )
          {
            transmitMessage(targetPeerSynapseInstanceID, msg);
            
            // Keep track of forwarded subscription
            forwardedSubscriptionsMutex.lock();
            try
            {
              HashSet<ForwardedSubscription> subList =
                  forwardedSubscriptions.get(targetPeerSynapseInstanceID);
              
              if (subList == null)
              {
                subList = new HashSet<ForwardedSubscription>();
                forwardedSubscriptions.put(targetPeerSynapseInstanceID, subList);
              }
              
              subList.add(fwdSub);
            }
            finally
            {
              forwardedSubscriptionsMutex.unlock();
            }
          }
        }
      }
      else
      {
        cell.logEvent(EventType.ERROR,
                      threadName + "[subscribe]",
                      "Peer not found: " + msg.arrivalSynapseInstanceID);
      }
    }
    finally
    {
      peerMutex.unlock();
    }
  }  // end subscribe
  
  //---------------------------------------------------------------------------

  private
  void
  routeStimulus (Msg_Stimulus msg, String threadName)
  {
    Nuc_SubscriptionInfo producerInfo = new Nuc_SubscriptionInfo(msg);
    HashSet<UUID> peerList = new HashSet<UUID>();
    
    if (!msg.typeID.equals(EventStimulus.TYPE_ID))
    {
      cell.logEvent(EventType.RUNTIME,
                    threadName + "[routeStimulus]",
                    "RUNTIME;NUCLEUS;ROUTE;1;Producer: " + producerInfo);
    }

    // Route stimulus to peers
    subscriptionMutex.lock();
    try
    {
      for (Map.Entry<Nuc_SubscriptionInfo, HashSet<UUID>> sub : subscriptionMap.entrySet())
      {
        // Check if subscription matches (supports regular expressions)
        if (producerInfo.matches(sub.getKey()))
        {
          if (!msg.typeID.equals(EventStimulus.TYPE_ID))
          {
            cell.logEvent(EventType.RUNTIME,
                          threadName + "[routeStimulus]",
                          "RUNTIME;NUCLEUS;ROUTE;2;Subscription Match: " + sub.getKey());
          }

          // Iterate over all peers interested in stimulus
          PeerLoop:
          for (UUID peerSynapseInstanceID : sub.getValue())
          {
            // Skip the peer if it's the sender
            if (peerSynapseInstanceID.equals(msg.arrivalSynapseInstanceID))
              continue PeerLoop;

            peerMutex.lock();
            try
            {
              Nuc_PeerInfo peerInfo = peerMap.get(peerSynapseInstanceID);

              if (peerInfo != null)
              {
                // Iterate over peer's subscriptions looking for an exact match
                for (Nuc_SubscriptionInfo peerSub : peerInfo.subscriptions)
                {
                  if (!msg.typeID.equals(EventStimulus.TYPE_ID))
                  {
                    cell.logEvent(EventType.RUNTIME,
                                  threadName + "[routeStimulus]",
                                  "RUNTIME;NUCLEUS;ROUTE;3;Peer Subscription: " + peerSub);
                  }

                  // If subscription matches exactly and message not sent to peer yet
                  // (only forward Event stimuli over originating domain)
                  if (peerSub.equals(sub.getKey()) &&
                      !peerList.contains(peerSynapseInstanceID) &&
                      (!msg.typeID.equals(EventStimulus.TYPE_ID) ||
                       (msg.typeID.equals(EventStimulus.TYPE_ID) &&
                        (msg.arrivalDomain == null ||
                         peerInfo.synapseDomain.equals(msg.arrivalDomain)))))
                  {
                    if (!msg.typeID.equals(EventStimulus.TYPE_ID))
                    {
                      cell.logEvent(EventType.RUNTIME,
                                    threadName + "[routeStimulus]",
                                    "RUNTIME;NUCLEUS;ROUTE;4;Peer Subscription Matches!");

                      cell.logEvent(EventType.RUNTIME,
                                    threadName + "[routeStimulus]",
                                    "RUNTIME;NUCLEUS;ROUTE;5;Sending to " + peerInfo.cellName +
                                    " (" + peerSynapseInstanceID + ")");
                    }

                    transmitMessage(peerSynapseInstanceID, msg);

                    logStimulusEgressTrace(msg, peerSynapseInstanceID);

                    peerList.add(peerSynapseInstanceID);
                  }  // if subscription matches (exactly)
                }  // for each subscription
              }  // if peerInfo != null
            }
            finally
            {
              peerMutex.unlock();
            }
          }  // for each peer
        }  // if subscription matches (regex)
      }  // for each subscription
    }
    finally
    {
      subscriptionMutex.unlock();
    }
  }  // end routeStimulus
  
  //---------------------------------------------------------------------------
  
  private
  void
  processStimulus (Msg_Stimulus msg, String threadName)
  {
    Nuc_SubscriptionInfo producerInfo = new Nuc_SubscriptionInfo(msg);

    if (!msg.typeID.equals(EventStimulus.TYPE_ID))
    {
      cell.logEvent(EventType.RUNTIME,
                    threadName + "[processStimulus]",
                    "RUNTIME;NUCLEUS;PROCESS;1;" + cellName + ";" + msg + ";" + formatStimulusTrace(msg));
    }
    
    try
    {
      // Hand off stimulus to Cell if there's a subscription
      for (SubscriptionSpec subscription : subscriptions)
      {
        if (!msg.typeID.equals(EventStimulus.TYPE_ID))
        {
          cell.logEvent(EventType.RUNTIME,
                        threadName + "[processStimulus]",
                        "RUNTIME;NUCLEUS;PROCESS;2;sub=" + subscription);
        }
        
        // If Cell is interested
        if (producerInfo.matches(subscription))
        {
          logStimulusIngressTrace(msg);

          Stimulus s = (Stimulus) cipher.decrypt(msg.value);
          
          s.setConsumerCellName(cellName);

          if (s instanceof Stim_CycleDetection)
          {
            Stim_CycleDetection cdet = (Stim_CycleDetection) s;
            cdet.trace = msg.trace;
          }
          
          cell.receiveStimulus(s);

          // No need to check the rest of the subscriptions
          break;
        }  // if subscription matches (regex)
      }  // for each subscription
    }
    catch (Excp_Cipher ce)
    {
      cell.logEvent(EventType.ERROR,
                    threadName + "[processStimulus]",
                    ce);
    }      
  }  // end processStimulus
  
  //===========================================================================
  //  THREADS
  //===========================================================================

  //---------------------------------------------------------------------------
  // ReceiveThread
  //
  // Processes incoming messages.
  //---------------------------------------------------------------------------
  private final class ReceiveThread extends Thread
  {
    ReceiveThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Nucleus Receive");
      
      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
          gracefulTerminate.equals("n") ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
    }
    
    @Override
    public void run ()
    {
      Msg_NeuPaths msg = null;
      
      ProcessLoop:
      while (terminate.isNotSet())
      {
        // Wait for message
        try
        {
          recvSemaphore.acquire();
        }
        catch (InterruptedException ie)
        {
          continue ProcessLoop;
        }

        // Get message from queue
        recvQueueMutex.lock();
        try
        {
          msg = recvQueue.pollFirst();
        }
        finally
        {
          recvQueueMutex.unlock();
        }

        // Process message
        if (msg instanceof Msg_Subscription)
        {
          Msg_Subscription subscribeMsg = (Msg_Subscription) msg;
          subscribe(subscribeMsg, getName());
        }
        else if (msg instanceof Msg_Stimulus)
        {
          Msg_Stimulus stimulusMsg = (Msg_Stimulus) msg;
          
          stimulusMsg.addTrace(new Stim_Trace(cellName, msg.arrivalDomain));

          if (!isDuplicate(stimulusMsg))
          {
            routeStimulus(stimulusMsg, getName());
            
            // Do not process Cycle Detection stimuli.  They
            // are processed when a duplicate is received, which is
            // an indication of a cycle.
            if (!stimulusMsg.typeID.equals(Stim_CycleDetection.TYPE_ID))
              processStimulus(stimulusMsg, getName());
          }
          else  // duplicate stimulus received
          {
            if (stimulusMsg.typeID.equals(Stim_CycleDetection.TYPE_ID))
            {
              if (hasCycle(stimulusMsg))
              {
                cell.logEvent(EventType.RUNTIME,
                              getName(),
                              "RUNTIME;NUCLEUS;RECEIVE;Cycle detected: " + stimulusMsg + ", Trace: " + formatStimulusTrace(stimulusMsg));
                processStimulus(stimulusMsg, getName());
              }
              else
              {
                routeStimulus(stimulusMsg, getName());
              }
            }
            else
            {
              cell.logEvent(EventType.WARNING,
                            getName(),
                            "Duplicate received: " + stimulusMsg + ", Trace: " + formatStimulusTrace(stimulusMsg));
            }
          }
        }
        else
        {
          // Ignore all other messages
          cell.logEvent(EventType.WARNING,
                        getName(),
                        "Ignoring mesage: " + msg);
        }
      }  // ProcessLoop: while (terminate.isNotSet())
      
    }  // end run
    
    private boolean isDuplicate (Msg_Stimulus msg)
    {
      boolean duplicate = false;
      long currentTime = System.currentTimeMillis();

      stimuliHistoryMutex.lock();
      try
      {
        // Check if stimulus was already received in recent past
        if (stimuliHistory.containsKey(msg.instanceID))
        {
          duplicate = true;
        }
        else
        {
          stimuliHistory.put(msg.instanceID, currentTime);
        }
      }
      finally
      {
        stimuliHistoryMutex.unlock();
      }
      
      return duplicate;
    }

    private boolean hasCycle (Msg_Stimulus msg)
    {
      boolean hasCycle = false;
      int visits = 0;
      
      for (Stim_Trace t : msg.trace)
      {
        if (t.cellName.equals(cellName))
          visits++;
        
        if (visits > 1)
        {
          hasCycle = true;
          break;
        }
      }
      
      return hasCycle;
    }

  }  // end class ReceiveThread
  
  //---------------------------------------------------------------------------
  // TransmitThread
  //
  // Processes outgoing messages.
  //---------------------------------------------------------------------------
  private final class TransmitThread extends Thread
  {
    TransmitThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Nucleus Transmit");

      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");

      if (gracefulTerminate == null ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
    }

    @Override
    public void run ()
    {
      Nuc_Transmit msgEntry = null;
      
      ProcessLoop:
      while (terminate.isNotSet())
      {
        // Wait for message
        try
        {
          xmitSemaphore.acquire();
        }
        catch (InterruptedException ie)
        {
          continue ProcessLoop;
        }

        // Get message from queue
        xmitQueueMutex.lock();
        try
        {
          msgEntry = xmitQueue.pollFirst();
        }
        finally
        {
          xmitQueueMutex.unlock();
        }
        
        peerMutex.lock();
        try
        {
          Nuc_PeerInfo peerInfo = peerMap.get(msgEntry.synapseInstanceID);

          if (peerInfo != null)
          {
            Bnd_Binder binder = binders.get(peerInfo.binderInstanceID);
            binder.send(msgEntry.synapseInstanceID, msgEntry.msg);
          }
          else
          {
            cell.logEvent(EventType.ERROR,
                          getName(),
                          "Peer not found: " + msgEntry.synapseInstanceID);
          }
        }
        finally
        {
          peerMutex.unlock();
        }
        
      }  // ProcessLoop: while (terminate.isNotSet())
      
    }  // end run
    
  }  // end class TransmitThread

  //---------------------------------------------------------------------------
  // StimuliHistoryThread
  //
  // Periodically purges the stimuliHistory map.
  //---------------------------------------------------------------------------
  private final class StimuliHistoryThread extends Thread
  {
    StimuliHistoryThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Nucleus Stimuli History");

      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");

      if (gracefulTerminate == null ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
    }

    @Override
    public void run ()
    {
      while (terminate.isNotSet())
      {
        try
        {
          Thread.sleep(duplicateDetectionIntervalMs.getValue());

          stimuliHistoryMutex.lock();
          try
          {
            long currentTime = System.currentTimeMillis();
            LinkedList<UUID> toBePurged = new LinkedList<UUID>();

            // Identify entries to be purged (older than window interval)
            for (Map.Entry<UUID, Long> entry : stimuliHistory.entrySet())
            {
              if (currentTime - entry.getValue() > duplicateDetectionIntervalMs.getValue())
                toBePurged.add(entry.getKey());
            }

            // Purge old entries
            for (UUID instanceID : toBePurged)
            {
              stimuliHistory.remove(instanceID);
            }
          }
          finally
          {
            stimuliHistoryMutex.unlock();
          }
        }
        catch (InterruptedException ie)
        {
          // Ignore
        }
      }  // while terminate.isNotSet()

    }  // end run

  }  // end class StimuliHistoryThread

  //---------------------------------------------------------------------------
  // SubscriptionTraceThread
  //
  // Periodically reports the subscriptions registered with the Nucleus.
  //---------------------------------------------------------------------------
  private final class SubscriptionTraceThread extends Thread
  {
    SubscriptionTraceThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Nucleus Subscription Trace");
      
      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
    }
    
    @Override
    public void run ()
    {
      while (terminate.isNotSet())
      {
        try
        {
          if (subscriptionTraceIntervalMs.getValue() > 0L)
          {
            Thread.sleep(subscriptionTraceIntervalMs.getValue());
            
            subscriptionMutex.lock();
            try
            {
              String listOfSubscriptions = "";
  
              for (Nuc_SubscriptionInfo sub : subscriptionMap.keySet())
              {
                if (!sub.transmitterName.equals(Cdet.CDET_DETECTION_TRANSMITTER) &&
                    !sub.transmitterName.equals(Evt.EVENT_TRANSMITTER))
                {
                  listOfSubscriptions += ("\n  " + sub);
                }
              }  // for each subscription
  
              cell.logEvent(EventType.TRACE,
                            getName(),
                            "Registered Subscriptions: " + listOfSubscriptions);
            }
            finally
            {
              subscriptionMutex.unlock();
            }
          }
          else
          {
            Thread.sleep(1000L);
          }
        }
        catch (InterruptedException ie)
        {
          // Ignore
        }
      }  // while terminate.isNotSet()
      
    }  // end run
    
  }  // end class SubscriptionTraceThread

  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private String cellName;
  private UUID cellInstanceID;
  private SubscriptionSpecSet subscriptions;
  private Nuc_State state;
  
  private HashMap<UUID, Bnd_Binder> binders;
  
  private LinkedList<Msg_NeuPaths> recvQueue;
  private ReentrantLock recvQueueMutex;
  private Semaphore recvSemaphore;

  private LinkedList<Nuc_Transmit> xmitQueue;
  private ReentrantLock xmitQueueMutex;
  private Semaphore xmitSemaphore;

  private HashMap<UUID, Nuc_PeerInfo> peerMap;
  private ReentrantLock peerMutex;
  
  private HashMap<Nuc_SubscriptionInfo, HashSet<UUID>> subscriptionMap;
  private ReentrantLock subscriptionMutex;

  private class ForwardedSubscription
  {
    ForwardedSubscription (Nuc_SubscriptionInfo sub)
    {
      this.sub = sub;
      this.timestamp = System.currentTimeMillis();
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

      final ForwardedSubscription other = (ForwardedSubscription) obj;

      if (!sub.equals(other.sub))
      {
        return false;
      }

      return true;
    }

    @Override
    public
    int
    hashCode ()
    {
      return sub.hashCode();
    }
    
    Nuc_SubscriptionInfo sub;
    long timestamp;
  }

  private HashMap<UUID, HashSet<ForwardedSubscription>> forwardedSubscriptions;
  private ReentrantLock forwardedSubscriptionsMutex;

  private HashMap<UUID, Long> stimuliHistory;
  private ReentrantLock stimuliHistoryMutex;

  private ReceiveThread recvThread;
  private TransmitThread xmitThread;
  private StimuliHistoryThread historyThread;
  private SubscriptionTraceThread subTraceThread;
  
  private SafeBoolean terminate;

  private Cryp_Cipher cipher;
  
  private Cell cell;

  private SafeLong duplicateDetectionIntervalMs;
  private SafeLong subscriptionRefreshIntervalMs;
  private SafeLong subscriptionTraceIntervalMs;

  private SafeBoolean propagateGlobalSubs;
}
