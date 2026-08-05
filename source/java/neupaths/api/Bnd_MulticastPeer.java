// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * A binder for datagram-oriented multicast peers (Multicast UDP).
 *
 * @author Aaron Caraveo
 */
final class Bnd_MulticastPeer extends Bnd_Binder
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  Bnd_MulticastPeer
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
      groupAddress = Syn_Factory.createAddress(synName);

      if (groupAddress == null ||
          groupAddress.getValue() == null)
      {
        throw new Excp_Binder(cellName,
                              cellInstanceID,
                              synName,
                              "Parameter 'synName' is invalid");
      }

      boolean isMulticast =
          groupAddress.getSynapseType() == Syn_Type.MULTICAST;
      
      if ( !(isMulticast && groupAddress.getSynapseMode() == Syn_Mode.PEER) )
      {
        throw new Excp_Binder(cellName,
                              cellInstanceID,
                              synName,
                              "Synapse characteristics invalid");
      }

      // will get synapse type/mode/domain from groupAddress
      sendSynapse = Syn_Factory.createSynapse(groupAddress);
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
      // will get synapse type/mode/domain from groupAddress
      receiveSynapse = Syn_Factory.createSynapse(groupAddress);
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
      terminatePeer.clear();

      setState(CellState.DEGRADED);
      
      // Start join thread
      peerThread = new PeerThread();
      peerThread.start();
    }
  }
  
  @Override
  void
  stop ()
  {
    if (getState() != CellState.OFFLINE)
    {
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

  //===========================================================================
  //  THREADS
  //===========================================================================

  //---------------------------------------------------------------------------
  // PeerThread
  //
  // Enqueues messages from a Stream peer.
  //---------------------------------------------------------------------------
  private final class PeerThread extends Thread
  {
    PeerThread ()
    {
      super(cellName + " (" + cellInstanceID + ") Multicast Peer (" +
            receiveSynapse.getInstanceID() + ")");
      
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
      Msg_NeuPaths msg = null;
      
      try
      {
        // Open send synapse
        sendSynapse.open(null);

        // Connect to multicast group
        sendSynapse.connect(groupAddress);

        // Use the synapse type and port from group address to create
        // a receiveAddress (i.e. listen on INADDR_ANY for the given port)
        Syn_Address receiveAddress =
            Syn_Factory.createAddress(
                Syn_Factory.updateInetNameAddress(groupAddress.getSynapseName(), "*"));

        // Open receive synapse
        receiveSynapse.open(receiveAddress);

        // Join the multicast group
        Syn_InetDatagramChannel mcastReceiveSynapse =
            (Syn_InetDatagramChannel) receiveSynapse;
        mcastReceiveSynapse.join(groupAddress, null);

        setPeerInfo(receiveSynapse.getInstanceID(),
                    new Bnd_PeerInfo(instanceID,
                                     nucleus.getCell().getInstanceID(),
                                     nucleus.getCell().getName(),
                                     receiveSynapse.getDomain(),
                                     receiveSynapse.getInstanceID(),  // fromPeerSynapseInstanceID
                                     sendSynapse.getInstanceID(),     // toPeerSynapseInstanceID
                                     receiveSynapse,                  // fromPeerSynapse
                                     sendSynapse,                     // toPeerSynapse
                                     null),                           // peerThread
                    getName());

        nucleus.handlePeerJoin(nucleus.getCell().getName() + " (" + synName.getOption(0) + ")",
                               receiveSynapse.getInstanceID(),
                               receiveSynapse.getDomain(),
                               instanceID);
        
        setState(CellState.ONLINE);
        
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
            // If not the sender, forward to nucleus
            if (!msg.departureSynapseInstanceID.equals(sendSynapse.getInstanceID()))
            {
              // Enqueue the message and announce it
              nucleus.receiveMessage(msg);
            }
          }  // if (terminatePeer.isNotSet())          
        }  // ReceiveLoop: while (terminatePeer.isNotSet())

        peerThread = null;

        setState(CellState.OFFLINE);
      }
      catch (Excp_SynapseFatal tfe)
      {
        nucleus.getCell().logEvent(EventType.ERROR,
                                   getName(),
                                   "Synapse operation failed",
                                   tfe);
      }
    }  // end run
  }  // end class PeerThread
  
  
  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private Syn_Address groupAddress;
  private Syn_Synapse sendSynapse;
  private Syn_Synapse receiveSynapse;

  private PeerThread peerThread;
  private SafeBoolean terminatePeer;

  private static final int JOIN_RETRY_INTERVAL_SECS = 1;
}
