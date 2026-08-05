// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.net.StandardProtocolFamily;
import java.util.UUID;

/**
 * The base class for all NeuPaths synapses.
 *
 * @author Aaron Caraveo
 */
abstract class Syn_Synapse implements AutoCloseable
{
  Syn_Synapse
    (StandardProtocolFamily family,
     Syn_Type               type,
     Syn_Mode               mode,
     String                 domain,
     Syn_SessionType        sessionType)
  {
    instanceID = UUID.randomUUID();
    
    // All parameter values validated before reaching this constructor
    this.family = family;
    this.type = type;
    this.mode = mode;
    this.domain = domain;
    this.sessionType = sessionType;

    state = Syn_State.UNINITIALIZED;
    acceptQueueLength = 100;
  }

  Syn_Synapse
    (StandardProtocolFamily family,
     Syn_Type               type,
     Syn_Mode               mode,
     String                 domain,
     Syn_SessionType        sessionType,
     Syn_State              state)
  {
    instanceID = UUID.randomUUID();
    
    // All parameter values validated before reaching this constructor
    this.family = family;
    this.type = type;
    this.mode = mode;
    this.domain = domain;
    this.sessionType = sessionType;
    this.state = state;

    acceptQueueLength = 100;
  }

  final
  UUID
  getInstanceID ()
  {
    return instanceID;
  }

  final
  StandardProtocolFamily
  getFamily ()
  {
    return family;
  }

  final
  Syn_Type
  getType ()
  {
    return type;
  }

  final
  Syn_Mode
  getMode ()
  {
    return mode;
  }

  final
  String
  getDomain ()
  {
    return domain;
  }
  
  final
  Syn_SessionType
  getSessionType ()
  {
    return sessionType;
  }

  final
  Syn_State
  getState ()
  {
    return state;
  }

  final
  void
  setState (Syn_State state)
  {
    this.state = state;
  }
  
  final
  int
  getAcceptQueueLength ()
  {
    return acceptQueueLength;
  }

  final
  void
  setAcceptQueueLength (int length)
  {
    acceptQueueLength = length;
  }

  abstract String getLocalName ()
    throws Excp_SynapseFatal;

  abstract String getRemoteName ()
    throws Excp_SynapseFatal;

  // Opens a connection by creating and initializing the
  // underlying communication mechanism
  // (e.g. creates Socket, sets options, binds/listens)
  abstract void open (Syn_Address address)
    throws Excp_SynapseFatal;

  // Waits for connection from peer in Listener mode
  abstract Syn_Synapse accept ()
    throws Excp_SynapseFatal;

  // Connects to peer in CLIENT mode
  abstract void connect (Syn_Address address)
    throws Excp_SynapseFatal;

  // Sends data to peer
  abstract void send (Object data)
    throws Excp_SynapseFatal;

  // Sends data to peer
  abstract void send (Syn_Address address, Object data)
    throws Excp_SynapseFatal;

  // Receives data from peer
  abstract Object receive ()
    throws Excp_SynapseFatal,
           Excp_SynapseNonFatal;

  // Closes the connection
  abstract public void close ();

  void
  release ()
  {
    state = Syn_State.RECLAIM;
  }
  
  UUID instanceID;
  StandardProtocolFamily family;
  Syn_Type type;
  Syn_Mode mode;
  String domain;
  Syn_SessionType sessionType;
  Syn_State state;
  int acceptQueueLength;

  protected static final java.lang.ref.Cleaner cleaner =
    java.lang.ref.Cleaner.create();
}
