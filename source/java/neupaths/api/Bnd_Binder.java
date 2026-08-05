// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.UUID;

/**
 * Base class for all binders.
 *
 * @author Aaron Caraveo
 */
abstract class Bnd_Binder
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  Bnd_Binder
    (String              cellName,
     UUID                cellInstanceID,
     Syn_Name            synName,
     SubscriptionSpecSet subscriptions)
    throws Excp_Binder
  {
    //-------------------------------------------------------------------------
    // Validate parameters
    //-------------------------------------------------------------------------
    
    if (cellName == null)
    {
      throw new Excp_Binder("Parameter 'cellName' is required");
    }

    if (cellInstanceID == null)
    {
      throw new Excp_Binder("Parameter 'cellInstanceID' is required");
    }

    if (synName == null)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            "Parameter 'synName' is required");
    }

    if (subscriptions == null)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Parameter 'subscriptions' is required");
    }

    instanceID = UUID.randomUUID();
    state = CellState.OFFLINE;
    
    this.cellName = cellName;
    this.cellInstanceID = cellInstanceID;
    this.synName = synName;
    this.subscriptions = subscriptions;
    
    //-------------------------------------------------------------------------
    // Initialize peer processing
    //-------------------------------------------------------------------------

    peerInfoMap = new HashMap<UUID, Bnd_PeerInfo>();
    peerInfoMutex = new ReentrantLock(true);

    //-------------------------------------------------------------------------
    // Initialize threads
    //-------------------------------------------------------------------------

    subscriptionRefreshThread = null;

    subscriptionRefreshIntervalMs = new SafeLong(1500L); // Default refresh of 1 sec

    terminate = new SafeBoolean();
    
    sendMutex = new ReentrantLock(true);
    
    nucleus = null;
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

  void
  setSubscriptionRefreshInterval (long millisecs)
  {
    long ms = millisecs;
    
    if (ms < 0L)
      ms = 0L;
    
    subscriptionRefreshIntervalMs.setValue(ms);
  }
  
  UUID
  getInstanceID ()
  {
    return instanceID;
  }
  
  synchronized
  CellState
  getState ()
  {
    return state;
  }
  
  synchronized
  void
  setState (CellState state)
  {
    this.state = state;
  }
  
  void
  start ()
  {
    // Clear thread indicators
    terminate.clear();

    // Start subscription refresh thread
    if (subscriptionRefreshIntervalMs.getValue() > 0L)
    {
      subscriptionRefreshThread = new SubscriptionRefreshThread();
      subscriptionRefreshThread.start();
    }
  }
  
  void
  stop ()
  {
    // Tell subscription refresh thread to terminate
    terminate.set();

    if (subscriptionRefreshThread != null)
    {
      // Interrupt subscription refresh thread in case it's sleeping
      subscriptionRefreshThread.interrupt();

      subscriptionRefreshThread = null;
    }

    // Clear peer map
    peerInfoMutex.lock();
    try
    {
      peerInfoMap.clear();
    }
    finally
    {
      peerInfoMutex.unlock();
    }
  }

  void
  send (UUID synapseInstanceID, Msg_NeuPaths msg)
  {
    String threadName = cellName + " (" + cellInstanceID + ") on " + synName;

    if (getState() == CellState.ONLINE)
    {
      Bnd_PeerInfo peerInfo = getPeerInfo(synapseInstanceID, threadName);

      if (peerInfo != null)
      {
        sendMutex.lock();
        try
        {
          msg.departureSynapseInstanceID = peerInfo.toPeerSynapse.getInstanceID();
          peerInfo.toPeerSynapse.send(msg);
        }
        catch (Excp_SynapseFatal tfe)
        {
          nucleus.getCell().logEvent(EventType.ERROR,
                                     threadName,
                                     "Failed to send message on synapse " + synapseInstanceID,
                                     tfe);
        }
        finally
        {
          sendMutex.unlock();
        }
      }
      else
      {
        nucleus.getCell().logEvent(EventType.ERROR,
                                   threadName,
                                   "Peer " + synapseInstanceID + " unknown");
      }  // if peerInfo != null
    }
    else
    {
      nucleus.getCell().logEvent(EventType.ERROR,
                                 threadName,
                                 "Message dropped. Binder " + instanceID + " is offline");
    }
  }
  
  void
  setNucleus (Nuc_Nucleus nucleus)
  {
    this.nucleus = nucleus;
  }
  
  //---------------------------------------------------------------------------
  
  protected void sendMessage (Syn_Synapse  sendSynapse,
                              Msg_NeuPaths msg,
                              String       threadName)
      throws Excp_SynapseFatal
  {
    sendMutex.lock();
    try
    {
      msg.departureSynapseInstanceID = sendSynapse.getInstanceID();
      sendSynapse.send(msg);
    }
    finally
    {
      sendMutex.unlock();
    }
  }

  //---------------------------------------------------------------------------

  protected Msg_NeuPaths receiveMessage (SafeBoolean guard,
                                         Syn_Synapse receiveSynapse,
                                         String      threadName)
      throws Excp_SynapseFatal
  {
    Msg_NeuPaths msg = null;
    
    while (guard.isNotSet())
    {
      try
      {
        // Read the message
        // Cast may result in ClassCastException (caught below)
        msg = (Msg_NeuPaths) receiveSynapse.receive();
        msg.arrivalSynapseInstanceID = receiveSynapse.getInstanceID();
        msg.arrivalDomain = receiveSynapse.getDomain();

        // Exit loop because receive succeeded
        break;
      }
      catch (ClassCastException cce)
      {
        nucleus.getCell().logEvent(EventType.ERROR,
                                   threadName + "[receiveMessage]",
                                   "Illegal message received from " + receiveSynapse.getRemoteName(),
                                   cce);
      }
      catch (Excp_SynapseNonFatal tnfe)
      {
        if (tnfe.getCause() != null &&
            tnfe.getCause() instanceof Excp_Cipher)
        {
          nucleus.getCell().logEvent(EventType.ERROR,
                                     threadName + "[receiveMessage]",
                                     tnfe);
        }
        else
        {
          nucleus.getCell().logEvent(EventType.WARNING,
                                     threadName + "[receiveMessage]",
                                     tnfe);

          // Sleep a bit to prevent spin loop
          try {Thread.sleep(1000);} catch (InterruptedException ie) {/*ignore*/}
        }
      }
    }  // while guard.isNotSet()
    
    return msg;
    
  }  // end receiveMessage
  
  //---------------------------------------------------------------------------

  protected void closeSynapse (Syn_Synapse synapse,
                               String      threadName)
  {
    if (synapse != null)
    {
      synapse.close();
    }
  }  // end closeSynapse
  
  //---------------------------------------------------------------------------

  protected Bnd_PeerInfo setPeerInfo (UUID           synapseInstanceID,
                                      Bnd_PeerInfo   peerInfo,
                                      String         threadName)
  {
    Bnd_PeerInfo oldValue = null;

    nucleus.getCell().logEvent(EventType.RUNTIME,
                               threadName + "[setPeerInfo]",
                               "RUNTIME,BINDER_BASE,PEER,1,cellInstanceID=" + peerInfo.cellInstanceID +
                               ", cellName=" + peerInfo.cellName +
                               ", peerKey=" + synapseInstanceID +
                               peerInfo.toString());

    peerInfoMutex.lock();
    try
    {
      oldValue = peerInfoMap.put(synapseInstanceID, peerInfo);
    }
    finally
    {
      peerInfoMutex.unlock();
    }

    return oldValue;
  }  // end setPeerInfo

  //---------------------------------------------------------------------------

  protected Bnd_PeerInfo getPeerInfo (UUID   synapseInstanceID,
                                      String threadName)
  {
    Bnd_PeerInfo value = null;

    peerInfoMutex.lock();
    try
    {
      value = new Bnd_PeerInfo(peerInfoMap.get(synapseInstanceID));
    }
    finally
    {
      peerInfoMutex.unlock();
    }

    return value;
  }  // end getPeerInfo

  //---------------------------------------------------------------------------

  protected Bnd_PeerInfo clearPeerInfo (UUID   synapseInstanceID,
                                        String threadName)
  {
    Bnd_PeerInfo value = null;

    peerInfoMutex.lock();
    try
    {
      value = peerInfoMap.remove(synapseInstanceID);
      
      if (value == null)
      {
        nucleus.getCell().logEvent(EventType.RUNTIME,
                                   threadName + "[clearPeerInfo]",
                                   "RUNTIME,BINDER_BASE,PEER,2,Removed " + synapseInstanceID);
      }
      else
      {
        nucleus.getCell().logEvent(EventType.RUNTIME,
                                   threadName + "[clearPeerInfo]",
                                   "RUNTIME,BINDER_BASE,PEER,3,Removed " + value.cellName);
      }
    }
    finally
    {
      peerInfoMutex.unlock();
    }

    return value;
  }  // end clearPeerInfo

  //---------------------------------------------------------------------------

  protected void terminatePeer (UUID   synapseInstanceID,
                                String threadName)
  {
    Bnd_PeerInfo peerInfo = getPeerInfo(synapseInstanceID,
                                        threadName + "[terminatePeer]");
    
    if (peerInfo != null)
    {
      closeSynapse(peerInfo.fromPeerSynapse,
                   threadName + "[terminatePeer]");
      closeSynapse(peerInfo.toPeerSynapse,
                   threadName + "[terminatePeer]");
    }
    
    clearPeerInfo(synapseInstanceID,
                  threadName + "[terminatePeer]");
  }
  
  //---------------------------------------------------------------------------

  protected void terminatePeer (Bnd_PeerInfo peerInfo,
                                String       threadName)
  {
    closeSynapse(peerInfo.fromPeerSynapse,
                 threadName + "[terminatePeer]");
    closeSynapse(peerInfo.toPeerSynapse,
                 threadName + "[terminatePeer]");

    if (peerInfo.fromPeerSynapseInstanceID != null)
    {
      clearPeerInfo(peerInfo.fromPeerSynapseInstanceID,
                    threadName + "[terminatePeer]");
    }
  }  // end terminatePeer
  
  //---------------------------------------------------------------------------
  
  protected void interruptPeers ()
  {
    peerInfoMutex.lock();
    try
    {
      for (Bnd_PeerInfo peerInfo : peerInfoMap.values())
      {
        if (peerInfo.peerThread != null)
          peerInfo.peerThread.interrupt();
      }
    }
    finally
    {
      peerInfoMutex.unlock();
    }
    
  }
  
  //---------------------------------------------------------------------------
  
  void sendSubscriptions (String threadName)
  {
    // Forward each subscription
    for (SubscriptionSpec subscription : subscriptions)
    {
      Msg_Subscription subscribeMsg =
          new Msg_Subscription(subscription.getCellName(),
                               subscription.getTransmitterName(),
                               subscription.getReceptorName(),
                               subscription.getDomain());

      peerInfoMutex.lock();
      try
      {
        // Send to each peer
        for (Bnd_PeerInfo peerInfo : peerInfoMap.values())
        {
          // The peer's toPeerSynapse could be null if the peer
          // hasn't completed the join process
          if ( peerInfo.toPeerSynapse != null &&
               (peerInfo.toPeerSynapse.getDomain().equals(subscription.getDomain()) ||
                subscription.getDomain().equals(Syn.GLOBAL_DOMAIN)) &&
               !subscription.isLoopback() &&
               !subscription.getTransmitterName().equals(Cdet.CDET_DETECTION_TRANSMITTER))
          {
            try
            {
              sendMessage(peerInfo.toPeerSynapse,
                          subscribeMsg,
                          threadName);
            }
            catch (Excp_SynapseFatal tfe)
            {
              nucleus.getCell().logEvent(EventType.ERROR,
                                         threadName,
                                         "Failed to send subscription",
                                         tfe);
            }
          }
        }  // for each peer
      }
      finally
      {
        peerInfoMutex.unlock();
      }
    }  // for each subscription
  }

  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  //===========================================================================
  //  THREADS
  //===========================================================================
  
  private final class SubscriptionRefreshThread extends Thread
  {
    SubscriptionRefreshThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Subscription Refresh");
      
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
      while (terminate.isNotSet())
      {
        try
        {
          if (subscriptionRefreshIntervalMs.getValue() > 0L)
          {
            Thread.sleep(subscriptionRefreshIntervalMs.getValue());
          
            if (Bnd_Binder.this.getState() == CellState.ONLINE)
              sendSubscriptions(getName());
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
    
  }  // end class SubscriptionRefreshThread

  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private SubscriptionRefreshThread subscriptionRefreshThread;
  private SafeBoolean terminate;
  private HashMap<UUID, Bnd_PeerInfo> peerInfoMap;
  private ReentrantLock peerInfoMutex;
  private ReentrantLock sendMutex;  
  private CellState state;
  private SafeLong subscriptionRefreshIntervalMs;

  UUID instanceID;
  String cellName;
  UUID cellInstanceID;
  Syn_Name synName;
  SubscriptionSpecSet subscriptions;
  Nuc_Nucleus nucleus;
}
