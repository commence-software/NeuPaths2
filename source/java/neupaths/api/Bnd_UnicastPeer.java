// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * A binder for datagram-oriented peers (UDP).
 *
 * @author Aaron Caraveo
 */
final class Bnd_UnicastPeer extends Bnd_Binder
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  Bnd_UnicastPeer
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
    // Create synapses
    //-------------------------------------------------------------------------

    try
    {
      joinAddress = Syn_Factory.createAddress(synName);

      if (joinAddress == null ||
          joinAddress.getValue() == null)
      {
        throw new Excp_Binder(cellName,
                              cellInstanceID,
                              synName,
                              "Parameter 'synName' is invalid");
      }

      boolean isUnicast =
          joinAddress.getSynapseType() == Syn_Type.UNICAST;
      
      if ( !(isUnicast && joinAddress.getSynapseMode() == Syn_Mode.PEER) )
      {
        throw new Excp_Binder(cellName,
                              cellInstanceID,
                              synName,
                              "Synapse characteristics invalid");
      }

      // will get synapse type/mode/domain from joinAddress
      joinSynapse = Syn_Factory.createSynapse(joinAddress);
    }
    catch (Excp_SynapseFatal tfe)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Could not create join synapse",
                            tfe);
    }

    try
    {
      // will get synapse type/mode/domain from joinAddress
      sendSynapse = Syn_Factory.createSynapse(joinAddress);
    }
    catch (Excp_SynapseFatal tfe)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Could not create send synapse",
                            tfe);
    }

    try
    {
      // will get synapse type/mode/domain from joinAddress
      receiveSynapse = Syn_Factory.createSynapse(joinAddress);
    }
    catch (Excp_SynapseFatal tfe)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Could not create receive synapse",
                            tfe);
    }

    //-------------------------------------------------------------------------
    // Initialize threads
    //-------------------------------------------------------------------------

    joinThread = null;
    terminateJoin = new SafeBoolean();
    peerThread = null;
    terminatePeer = new SafeBoolean();
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
      terminateJoin.clear();
      terminatePeer.clear();

      // Start join thread
      joinThread = new JoinThread();
      joinThread.start();

      setState(CellState.DEGRADED);
    }
  }
  
  @Override
  void
  stop ()
  {
    if (getState() != CellState.OFFLINE)
    {
      if (joinThread != null)
      {
        // Tell join thread to terminate
        terminateJoin.set();

        // Interrupt join thread in case it's sleeping
        joinThread.interrupt();
      }

      // Tell peer thread to terminate
      terminatePeer.set();

      // Interrupt peer thread in case it is blocked
      peerThread.interrupt();

      super.stop();
      
      setState(CellState.OFFLINE);
    }
  }

  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  private void performJoin (String threadName) throws Excp_NeuPaths
  {
    Msg_JoinRequest joinReqMsg = null;
    Msg_JoinAcknowledge joinAckMsg = null;
    
    // Open join synapse
    joinSynapse.open(null);

    // Connect to listener
    joinSynapse.connect(joinAddress);

    // Open send synapse
    sendSynapse.open(null);

    // Open (and bind) the receive synapse
    receiveSynapse.open(null);

    //---------------------------------------------------------------------
    // Perform phase 1 of join
    //---------------------------------------------------------------------

    // Create join request message
    joinReqMsg =
        new Msg_JoinRequest(Msg_JoinPhase.PHASE_1,
                            instanceID,
                            cellInstanceID,
                            cellName,
                            sendSynapse.getDomain(),
                            sendSynapse.getInstanceID(), // sendSynapseInstanceID
                            sendSynapse.getLocalName(),  // sendSynapseName
                            null,                        // receiveSynapseInstanceID
                            null);                       // receiveSynapseName

    // Send Phase_1 join request
    sendMessage(joinSynapse, joinReqMsg, threadName);

    try
    {
      // Receive Phase_1 join acknowledgement
      joinAckMsg = (Msg_JoinAcknowledge) sendSynapse.receive();
    }
    catch (ClassCastException cce)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Illegal message received from " + sendSynapse.getRemoteName(),
                            cce);
    }

    if (joinAckMsg.joinPhase != Msg_JoinPhase.PHASE_1)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Join sequence error");
    }

    if ( !joinAckMsg.requesterInstanceID.equals(instanceID) )
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Invalid join acknowledgement");
    }
    
    // Connect to peer
    sendSynapse.connect(Syn_Factory.createAddress(joinAckMsg.sendSynapseName));

    //---------------------------------------------------------------------
    // Perform phase 2 of join
    //---------------------------------------------------------------------

    // Update join request
    joinReqMsg.joinPhase = Msg_JoinPhase.PHASE_2;
    joinReqMsg.sendSynapseInstanceID =
        joinAckMsg.sendSynapseInstanceID;
    joinReqMsg.sendSynapseName =
        joinAckMsg.sendSynapseName;
    joinReqMsg.receiveSynapseInstanceID = receiveSynapse.getInstanceID();
    joinReqMsg.receiveSynapseName = receiveSynapse.getLocalName();

    // Send Phase_2 join request
    sendMessage(joinSynapse, joinReqMsg, threadName);

    try
    {
      // Receive Phase_2 join acknowledgement
      joinAckMsg = (Msg_JoinAcknowledge) receiveSynapse.receive();
    }
    catch (ClassCastException cce)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Illegal message received from " + receiveSynapse.getRemoteName(),
                            cce);
    }

    if (joinAckMsg.joinPhase != Msg_JoinPhase.PHASE_2)
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Join sequence error");
    }

    if ( !joinAckMsg.requesterInstanceID.equals(instanceID) )
    {
      throw new Excp_Binder(cellName,
                            cellInstanceID,
                            synName,
                            "Invalid join acknowledgement");
    }

    // Connect to peer
    receiveSynapse.connect(Syn_Factory.createAddress(joinAckMsg.receiveSynapseName));

    // Add peerInfo
    // Since we're adding a pseudo-peer for the listener we connected to,
    // the send and receive synapses are swapped
    Bnd_PeerInfo peerInfo =
        new Bnd_PeerInfo(joinAckMsg.binderInstanceID,
                         joinAckMsg.cellInstanceID,
                         joinAckMsg.cellName,
                         receiveSynapse.getDomain(),
                         receiveSynapse.getInstanceID(),
                         sendSynapse.getInstanceID(),
                         receiveSynapse,
                         sendSynapse,
                         null);
    setPeerInfo(receiveSynapse.getInstanceID(),
                peerInfo,
                threadName);

    nucleus.handlePeerJoin(joinAckMsg.cellName,
                           receiveSynapse.getInstanceID(),
                           peerInfo.synapseDomain,
                           instanceID);
    
    joinSynapse.close();
  }  // end performJoin

  //===========================================================================
  //  THREADS
  //===========================================================================

  //---------------------------------------------------------------------------
  // JoinThread
  //
  // Joins the cell network as a Stream peer.
  //---------------------------------------------------------------------------
  private final class JoinThread extends Thread
  {
    JoinThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Unicast Join (" +
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
      while (terminateJoin.isNotSet())
      {
        try
        {
          performJoin(getName());

          setState(CellState.ONLINE);

          // Join was successful, so start peer thread and terminate
          joinThread = null;
          peerThread = new PeerThread();
          peerThread.start();
          return;
        }
        catch (Excp_NeuPaths be)
        {
          if (terminateJoin.isNotSet())
          {
            nucleus.getCell().logEvent(EventType.WARNING,
                                       getName(),
                                       "Failed to join cell network.  Will try again in " +
                                       JOIN_RETRY_INTERVAL_SECS + " seconds.",
                                       be);

            closeSynapse(sendSynapse, getName());
            closeSynapse(receiveSynapse, getName());
            clearPeerInfo(receiveSynapse.getInstanceID(), getName());

            // Try again in a little while
            try
            {
              Thread.sleep(JOIN_RETRY_INTERVAL_SECS * 1000);
            }
            catch (InterruptedException ie)
            {
              // Ignore
            }
          }
        }
      }  // end while (terminateJoin.isNotSet())
      
      joinThread = null;
      
    }  // end run
  }  // end class JoinThread
  
  //---------------------------------------------------------------------------
  // PeerThread
  //
  // Enqueues messages from a Stream peer.
  //---------------------------------------------------------------------------
  private final class PeerThread extends Thread
  {
    PeerThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Unicast Peer (" +
            receiveSynapse.getInstanceID() + ")");
      
      String gracefulTerminate = System.getenv("NEUPATHS_FORCE_GRACEFUL_TERMINATION");
      
      if (gracefulTerminate == null ||
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
      Msg_NeuPaths msg = null;
      
      boolean peerLeft = false;

      Bnd_PeerInfo peerInfo =
          getPeerInfo(receiveSynapse.getInstanceID(), getName());

      if (peerInfo != null)
      {
        ReceiveLoop:
        while (terminatePeer.isNotSet())
        {
          //---------------------------------------------------------------
          //  Get next message
          //---------------------------------------------------------------
          try
          {
            msg = receiveMessage(terminatePeer,
                                 receiveSynapse,
                                 getName());
          }
          catch (Excp_SynapseFatal tfe)
          {
            nucleus.getCell().logEvent(EventType.ERROR,
                                       getName(),
                                       "Synapse failed on receive",
                                       tfe);
            
            break ReceiveLoop;
          }

          //---------------------------------------------------------------
          //  Enqueue the message
          //---------------------------------------------------------------
          if (terminatePeer.isNotSet())
          {
            // If leave msg, terminate the PeerThread
            if (msg instanceof Msg_Leave)
            {
              peerLeft = true;
              break ReceiveLoop;
            }

            // Enqueue the message and announce it
            nucleus.receiveMessage(msg);
          }  // if (terminatePeer.isNotSet())          
        }  // ReceiveLoop: while (terminatePeer.isNotSet())

        // There are three ways to exit the ReceiveLoop:
        // (1) The terminatePeer flag is set (we are shutting down)
        // (2) A fatal synapse error occurs
        // (3) Our peer leaves the cell network
        //
        // Only send a Leave message when we terminated (cases 1 & 2)
        if (!peerLeft)
        {
          Msg_Leave leaveMsg = new Msg_Leave(cellInstanceID,
                                             cellName,
                                             instanceID);
          send(receiveSynapse.getInstanceID(), leaveMsg);
        }
        
        terminatePeer(peerInfo, getName());
        
        nucleus.handlePeerLeave(receiveSynapse.getInstanceID());
        
        peerThread = null;
        
        if (terminatePeer.isNotSet())
        {
          // Re-join the cell network
          joinThread = new JoinThread();
          joinThread.start();
        }
        else
        {
          setState(CellState.OFFLINE);
        }
      }
      else
      {
        nucleus.getCell().logEvent(EventType.ERROR,
                                   getName(),
                                   "Peer " + receiveSynapse.getInstanceID() + " unknown");
      }  // if peerInfo != null
    }  // end run

  }  // end class PeerThread
  
  
  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private Syn_Address joinAddress;
  private Syn_Synapse joinSynapse;
  private Syn_Synapse sendSynapse;
  private Syn_Synapse receiveSynapse;

  private JoinThread joinThread;
  private SafeBoolean terminateJoin;
  private PeerThread peerThread;
  private SafeBoolean terminatePeer;

  private static final int JOIN_RETRY_INTERVAL_SECS = 1;
}
