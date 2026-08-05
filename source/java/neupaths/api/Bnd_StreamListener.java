// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * A binder for connection-oriented listeners (TCP and Unix).
 *
 * @author Aaron Caraveo
 */
final class Bnd_StreamListener extends Bnd_Binder
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  Bnd_StreamListener
    (String              cellName,
     UUID                cellInstanceID,
     Syn_Name            synName,
     SubscriptionSpecSet subscriptions)
    throws Excp_Binder
  {
    super(cellName,
          cellInstanceID,
          synName,
          subscriptions);

    //-------------------------------------------------------------------------
    // Create synapse
    //-------------------------------------------------------------------------

    try
    {
      listenAddress = Syn_Factory.createAddress(synName);

      if (listenAddress.getValue() == null)
      {
        throw new Excp_Binder(cellName,
                              cellInstanceID,
                              synName,
                              "Parameter 'synName' is invalid");
      }

      boolean isStream =
          listenAddress.getSynapseType() == Syn_Type.STREAM;
      
      if ( !(isStream && listenAddress.getSynapseMode() == Syn_Mode.LISTENER) )
      {
        throw new Excp_Binder(cellName,
                              cellInstanceID,
                              synName,
                              "Synapse characteristics invalid");
      }

      listenSynapse = Syn_Factory.createSynapse(listenAddress);
    }
    catch (Excp_SynapseFatal tfe)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Could not create listen synapse",
                            tfe);
    }

    //-------------------------------------------------------------------------
    // Initialize threads
    //-------------------------------------------------------------------------

    listenThread = null;
    terminateListener = new SafeBoolean();
    terminatePeers = new SafeBoolean();
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

  @Override
  void
  start ()
  {
    if (getState() == CellState.OFFLINE)
    {
      super.start();

      // Clear thread indicators
      terminateListener.clear();
      terminatePeers.clear();

      // Start listen thread
      listenThread = new ListenThread();
      listenThread.start();

      setState(CellState.DEGRADED);
    }
  }
  
  @Override
  void
  stop ()
  {
    if (getState() != CellState.OFFLINE)
    {
      if (listenThread != null)
      {
        // Tell listen thread to terminate
        terminateListener.set();

        // Interrupt listen thread in case it's sleeping or waiting on a semaphore
        listenThread.interrupt();
      }

      // Tell peer threads to terminate
      terminatePeers.set();

      // Interrupt peer threads in case they are blocked
      interruptPeers();
      
      super.stop();
      
      setState(CellState.OFFLINE);
    }
  }

  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  //===========================================================================
  //  THREADS
  //===========================================================================

  //---------------------------------------------------------------------------
  // ListenThread
  //
  // Listens for join requests from Stream peers.
  //---------------------------------------------------------------------------
  private final class ListenThread extends Thread
  {
    ListenThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Stream Listener (" +
            synName + ")");
      
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
      Syn_Synapse peerSynapse = null;
      Msg_NeuPaths msg = null;
      Msg_JoinAcknowledge joinAckMsg = null;
      Bnd_PeerInfo peerInfo = null;
      
      try
      {
        // Open the listen synapse (bind to listen address)
        listenSynapse.open(listenAddress);
      }
      catch (Excp_SynapseFatal tfe)
      {
        nucleus.getCell().logEvent(EventType.ERROR,
                                   getName(),
                                   "Could not open listen synapse",
                                   tfe);

        // Terminate thread
        listenThread = null;
        return;
      }

      setState(CellState.ONLINE);
      
      ListenLoop:
      while (terminateListener.isNotSet())
      {
        //-----------------------------------------------------------------
        //  Accept a connection
        //-----------------------------------------------------------------
        try
        {
          // Wait for a peer to connect
          peerSynapse = listenSynapse.accept();

          nucleus.getCell().logEvent(EventType.RUNTIME,
                                     getName(),
                                     "RUNTIME,BINDER_STREAM_LISTENER,LISTEN,1,Accepted connection from " + peerSynapse.getRemoteName());
        }
        catch (Excp_SynapseFatal tfe)
        {
          if (terminateListener.isNotSet())
          {
            nucleus.getCell().logEvent(EventType.ERROR,
                                       getName(),
                                       "Listen synapse failed on accept",
                                       tfe);

            // Sleep and try again
            try {Thread.sleep(1000);} catch (InterruptedException ie) {/*ignore*/}
            continue ListenLoop;
          }
        }

        //-----------------------------------------------------------------
        //  Wait for join request
        //-----------------------------------------------------------------
        if (terminateListener.isNotSet())
        {
          try
          {
            msg = receiveMessage(terminateListener,
                                 peerSynapse,
                                 getName());
          }
          catch (Excp_SynapseFatal tfe)
          {
            if (terminateListener.isNotSet())
            {
              nucleus.getCell().logEvent(EventType.ERROR,
                                         getName(),
                                         "Listen synapse failed on receive",
                                         tfe);

              // Close synapse and continue
              closeSynapse(peerSynapse, getName());
              continue ListenLoop;
            }
          }
        }

        //-----------------------------------------------------------------
        //  Process join request
        //-----------------------------------------------------------------
        if (terminateListener.isNotSet())
        {
          try
          {
            // Only join requests are accepted by the listener
            if (msg instanceof Msg_JoinRequest)
            {
              Msg_JoinRequest joinReqMsg = (Msg_JoinRequest) msg;

              nucleus.getCell().logEvent(EventType.RUNTIME,
                                         getName(),
                                         "RUNTIME,BINDER_STREAM_LISTENER,LISTEN,2,JoinReq received from " + peerSynapse.getRemoteName());
              nucleus.getCell().logEvent(EventType.RUNTIME,
                                         getName(),
                                         "RUNTIME,BINDER_STREAM_LISTENER,LISTEN,3," + joinReqMsg.toString());

              // The peer must be in the same domain as the listener
              if (joinReqMsg.synapseDomain.equals(listenSynapse.getDomain()))
              {
                if (joinReqMsg.joinPhase == Msg_JoinPhase.PHASE_1)
                {
                  // Record peer info received in join request
                  peerInfo =
                      new Bnd_PeerInfo(joinReqMsg.binderInstanceID,
                                       joinReqMsg.cellInstanceID,
                                       joinReqMsg.cellName,
                                       joinReqMsg.synapseDomain,
                                       peerSynapse.getInstanceID(),  // fromPeerSynapseInstanceID
                                       null,                         // toPeerSynapseInstanceID
                                       peerSynapse,                  // fromPeerSynapse
                                       null,                         // toPeerSynapse
                                       null);                        // peerThread
                  setPeerInfo(peerSynapse.getInstanceID(),
                              peerInfo,
                              getName());

                  // Create join acknowledgement message
                  joinAckMsg =
                      new Msg_JoinAcknowledge(Msg_JoinPhase.PHASE_1,
                                              instanceID,
                                              cellInstanceID,
                                              cellName,
                                              joinReqMsg.binderInstanceID,  // requesterInstanceID
                                              peerSynapse.getInstanceID(),  // sendSynapseInstanceID
                                              peerSynapse.getLocalName(),   // sendSynapseName
                                              null,                         // receiveSynapseInstanceID
                                              null);                        // receiveSynapseName

                  nucleus.getCell().logEvent(EventType.RUNTIME,
                                             getName(),
                                             "RUNTIME,BINDER_STREAM_LISTENER,LISTEN,4," + joinAckMsg.toString());

                  try
                  {
                    // Send the join acknowledgement message
                    sendMessage(peerSynapse, joinAckMsg, getName());
                  }
                  catch (Excp_SynapseFatal tfe)
                  {
                    nucleus.getCell().logEvent(EventType.ERROR,
                                               getName(),
                                               "Could not send on peer synapse",
                                               tfe);

                    // Terminate peer and continue
                    terminatePeer(peerInfo, getName());
                    continue ListenLoop;
                  }
                }
                else  // PHASE_2
                {
                  // Get copy of current peer info
                  peerInfo = getPeerInfo(joinReqMsg.sendSynapseInstanceID,
                                         getName());

                  if (peerInfo != null)
                  {
                    // Store toSynapseInstanceID
                    peerInfo.toPeerSynapseInstanceID = peerSynapse.getInstanceID();

                    // Store peerSynapse
                    peerInfo.toPeerSynapse = peerSynapse;

                    // Create and store peerThread
                    peerInfo.peerThread =
                        new PeerThread(joinReqMsg.sendSynapseInstanceID);

                    // Update peer info (replace current with new instance)
                    setPeerInfo(joinReqMsg.sendSynapseInstanceID,
                                peerInfo,
                                getName());

                    // Start the peer thread
                    peerInfo.peerThread.start();

                    // Create join acknowledgement message
                    joinAckMsg =
                        new Msg_JoinAcknowledge(Msg_JoinPhase.PHASE_2,
                                                instanceID,
                                                cellInstanceID,
                                                cellName,
                                                joinReqMsg.binderInstanceID,       // requesterInstanceID
                                                joinReqMsg.sendSynapseInstanceID,  // sendSynapseInstanceID
                                                joinReqMsg.sendSynapseName,        // sendSynapseName
                                                peerSynapse.getInstanceID(),       // receiveSynapseInstanceID
                                                peerSynapse.getLocalName());       // receiveSynapseName

                    nucleus.getCell().logEvent(EventType.RUNTIME,
                                               getName(),
                                               "RUNTIME,BINDER_STREAM_LISTENER,LISTEN,5," + joinAckMsg.toString());

                    try
                    {
                      // Send the join acknowledgement message
                      sendMessage(peerSynapse, joinAckMsg, getName());
                    }
                    catch (Excp_SynapseFatal tfe)
                    {
                      nucleus.getCell().logEvent(EventType.ERROR,
                                                 getName(),
                                                 "Could not send on peer synapse",
                                                 tfe);

                      // Terminate peer and continue
                      terminatePeer(peerInfo, getName());
                      continue ListenLoop;
                    }

                    nucleus.handlePeerJoin(joinReqMsg.cellName,
                                           joinReqMsg.sendSynapseInstanceID,
                                           joinReqMsg.synapseDomain, //peerInfo.synapseDomain,
                                           instanceID);
                  }
                  else
                  {
                    nucleus.getCell().logEvent(EventType.ERROR,
                                               getName(),
                                               "Join failed, peer " +
                                               joinReqMsg.sendSynapseInstanceID +
                                               " unknown");
                  }  // if (peerInfo != null)
                }  // if PHASE_1
              }
              else
              {
                nucleus.getCell().logEvent(EventType.ERROR,
                                           getName(),
                                           "Domains do not match: peer=" +
                                           joinReqMsg.synapseDomain + " listener=" +
                                           listenSynapse.getDomain());
              }
            }
            else
            {
              nucleus.getCell().logEvent(EventType.ERROR,
                                         getName(),
                                         "Invalid message received from " + peerSynapse.getRemoteName());
            }  // if join request
          }
          catch (Excp_SynapseFatal tfe)
          {
            nucleus.getCell().logEvent(EventType.ERROR,
                                       getName(),
                                       "Peer synapse failed",
                                       tfe);          
          }
        }  // if (terminateListener.isNotSet())
      }  // while (terminateListener.isNotSet())

      listenSynapse.close();
      listenThread = null;
      
      setState(CellState.OFFLINE);
      
    }  // end run
    
  }  // end class ListenThread
  
  //---------------------------------------------------------------------------
  // PeerThread
  //
  // Enqueues messages from a Stream peer.
  //---------------------------------------------------------------------------
  private final class PeerThread extends Thread
  {
    PeerThread (UUID peerSynapseInstanceID)
    {
      super(cellName + " (" + cellInstanceID + ") Stream Listener Peer (" +
            peerSynapseInstanceID + ")");
      
      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
          gracefulTerminate.equals("N"))
      {
        setDaemon(true);
      }
      
      this.peerSynapseInstanceID = peerSynapseInstanceID;
    }
    
    @Override
    public
    void
    run ()
    {
      Msg_NeuPaths msg = null;

      boolean peerLeft = false;
      
      Bnd_PeerInfo peerInfo =
          getPeerInfo(peerSynapseInstanceID, getName());

      if (peerInfo != null)
      {
        ReceiveLoop:
        while (terminatePeers.isNotSet())
        {
          //---------------------------------------------------------------
          //  Get next message
          //---------------------------------------------------------------
          try
          {
            msg = receiveMessage(terminatePeers,
                                 peerInfo.fromPeerSynapse,
                                 getName());
          }
          catch (Excp_SynapseFatal tfe)
          {
            nucleus.getCell().logEvent(EventType.ERROR,
                                       getName(),
                                       "Peer synapse failed on receive",
                                       tfe);
            
            break ReceiveLoop;
          }

          //---------------------------------------------------------------
          //  Enqueue the message
          //---------------------------------------------------------------
          if (terminatePeers.isNotSet())
          {
            // If leave msg, terminate the PeerThread
            if (msg instanceof Msg_Leave)
            {
              peerLeft = true;
              break ReceiveLoop;
            }

            // Enqueue the message and announce it
            nucleus.receiveMessage(msg);
          }  // if (terminatePeers.isNotSet())          
        }  // ReceiveLoop: while (terminatePeers.isNotSet())

        // There are three ways to exit the ReceiveLoop:
        // (1) The terminatePeers flag is set (we are shutting down)
        // (2) A fatal synapse error occurs
        // (3) Our peer leaves the cell network
        //
        // Only send a Leave message when we terminated (cases 1 & 2)
        if (!peerLeft)
        {
          Msg_Leave leaveMsg = new Msg_Leave(cellInstanceID,
                                             cellName,
                                             instanceID);
          send(peerInfo.fromPeerSynapse.getInstanceID(), leaveMsg);
        }
        
        terminatePeer(peerInfo, getName());

        nucleus.handlePeerLeave(peerInfo.fromPeerSynapse.getInstanceID());
      }
      else
      {
        nucleus.getCell().logEvent(EventType.ERROR,
                                   getName(),
                                   "Peer " + peerSynapseInstanceID + " unknown");
      }  // if peerInfo != null
    }  // end run

    private final UUID peerSynapseInstanceID;

  }  // end class PeerThread
  
  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private Syn_Address listenAddress;
  private Syn_Synapse listenSynapse;
  
  private ListenThread listenThread;
  private SafeBoolean terminateListener;
  private SafeBoolean terminatePeers;
}
