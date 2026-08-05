// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnixDomainSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedList;
import javax.crypto.SealedObject;

/**
 * A synapse for Unix stream sockets.
 *
 * @author Aaron Caraveo
 */
final class Syn_UnixStreamChannel extends Syn_Synapse
{
  Syn_UnixStreamChannel (Syn_Type type, Syn_Mode mode, String domain)
  {
    super(StandardProtocolFamily.UNIX,
          type,
          mode,
          domain,
          Syn_SessionType.CONNECTIONED);

    if (type != Syn_Type.STREAM)
    {
      throw new NeuPathsException("Syn_UnixStream must have Stream type");
    }

    peerChannel = null;
    listenerChannel = null;

    closer = new Closer();
    cleanable = cleaner.register(this, closer);
  }

  // Private constructor for creating Synapse from
  // accepted connection
  Syn_UnixStreamChannel (SocketChannel peer, String domain)
  {
    super(StandardProtocolFamily.UNIX,
          Syn_Type.STREAM,
          Syn_Mode.PEER,
          domain,
          Syn_SessionType.CONNECTIONED,
          Syn_State.CONNECTED);

    peerChannel = peer;
    listenerChannel = null;

    closer = new Closer();
    cleanable = cleaner.register(this, closer);

    closer.setPeerChannel(peerChannel);
  }

  private
  UnixDomainSocketAddress
  getLocalAddress ()
  {
    UnixDomainSocketAddress rc = null;

    if (state.compareTo(Syn_State.INITIALIZED) >= 0 &&
        state.compareTo(Syn_State.CONNECTED) <= 0)
    {
      switch (mode)
      {
        case PEER:
          try
          {
            rc = (UnixDomainSocketAddress) peerChannel.getLocalAddress();
          }
          catch (IOException ioe)
          {
            // ignore - will return null
          }
          break;

        case LISTENER:
          try
          {
            rc = (UnixDomainSocketAddress) listenerChannel.getLocalAddress();
          }
          catch (IOException ioe)
          {
            // ignore - will return null
          }
          break;

        default:
          rc = null;
          break;
      }
    }

    return rc;
  }

  private
  UnixDomainSocketAddress
  getRemoteAddress ()
  {
    UnixDomainSocketAddress rc = null;

    switch (mode)
    {
      case PEER:
        if (state == Syn_State.CONNECTED)
          try
          {
            rc = (UnixDomainSocketAddress) peerChannel.getRemoteAddress();
          }
          catch (IOException ioe)
          {
            // ignore - will return null
          }
        else
          rc = null;
        break;

      default:
        rc = null;
        break;
    }

    return rc;
  }

  @Override
  String
  getLocalName ()
    throws Excp_SynapseFatal
  {
    String localName = null;
    String hostName = null;

    UnixDomainSocketAddress sa = getLocalAddress();

    if (sa != null)
    {
      LinkedList<String> options = new LinkedList<>();

      String localPath = sa.getPath().toString();
      
      if (!localPath.isEmpty())
        options.addLast(localPath);
      else
        options.addLast("ephemeral");
      
      Syn_Name synName = new Syn_Name(Syn_Scope.LOCAL,
                                      type,
                                      mode,
                                      domain,
                                      options);

      localName = synName.getText();
    }

    return localName;
  }

  @Override
  String
  getRemoteName ()
    throws Excp_SynapseFatal
  {
    String remoteName = null;
    String hostName = null;

    UnixDomainSocketAddress sa = getRemoteAddress();

    if (sa != null)
    {
      LinkedList<String> options = new LinkedList<>();
      
      options.addLast(sa.getPath().toString());
      
      Syn_Name synName = new Syn_Name(Syn_Scope.LOCAL,
                                      type,
                                      mode,
                                      domain,
                                      options);

      remoteName = synName.getText();
    }

    return remoteName;
  }

  @Override
  void
  open (Syn_Address address)
    throws Excp_SynapseFatal
  {
    if (!(state == Syn_State.UNINITIALIZED ||
          state == Syn_State.RECLAIM))
    {
      throw new Excp_SynapseFatal("Synapse already initialized");
    }

    UnixDomainSocketAddress socketAddress = null;    
    if (address != null)
    {
      try
      {
        socketAddress = (UnixDomainSocketAddress) address.getValue();
        
        // Clean up existing file (ignore return code)
        socketAddress.getPath().toFile().delete();
      }
      catch (ClassCastException cce)
      {
        throw new Excp_SynapseFatal("Invalid synapse address");
      }
    }

    switch (mode)
    {
      case PEER:
        try
        {
          peerChannel = SocketChannel.open(StandardProtocolFamily.UNIX);
          closer.setPeerChannel(peerChannel);
          setState(Syn_State.INITIALIZED);

          peerChannel.bind(socketAddress);
          setState(Syn_State.OPEN);
        }
        catch (IOException ioe)
        {
          throw new Excp_SynapseFatal("Could not open channel", ioe);
        }
        break;

      case LISTENER:
        try
        {
          listenerChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
          closer.setListenerChannel(listenerChannel);
          setState(Syn_State.INITIALIZED);

          listenerChannel.bind(socketAddress, getAcceptQueueLength());
          setState(Syn_State.OPEN);
        }
        catch (IOException ioe)
        {
          throw new Excp_SynapseFatal("Could not open channel", ioe);
        }
        break;

      default:
        throw new Excp_SynapseFatal("Unsupported Synapse mode");
    }
  }

  @Override
  Syn_Synapse
  accept ()
    throws Excp_SynapseFatal
  {
    Syn_Synapse rc = null;

    if (listenerChannel == null)
    {
      throw new Excp_SynapseFatal("Listener in unknown state");
    }
      
    if (mode != Syn_Mode.LISTENER)
    {
      throw new Excp_SynapseFatal("Synapse not in listener mode");
    }

    if (!(state == Syn_State.OPEN ||
          state == Syn_State.LISTENING))
    {
      throw new Excp_SynapseFatal("Synapse not open");
    }

    setState(Syn_State.LISTENING);

    try
    {
      SocketChannel peer = listenerChannel.accept();
      rc = new Syn_UnixStreamChannel(peer, domain);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not accept connection", ioe);
    }

    return rc;
  }

  @Override
  void
  connect (Syn_Address address)
    throws Excp_SynapseFatal
  {
    if (address == null)
    {
      throw new Excp_SynapseFatal("Parameter 'address' is required");
    }
    
    if (peerChannel == null)
    {
      throw new Excp_SynapseFatal("Peer in unknown state");
    }

    if (mode != Syn_Mode.PEER)
    {
      throw new Excp_SynapseFatal("Synapse not in peer mode");
    }

    if (state != Syn_State.OPEN)
    {
      throw new Excp_SynapseFatal("Synapse not open");
    }

    try
    {
      UnixDomainSocketAddress socketAddress = (UnixDomainSocketAddress)address.getValue();
      
      peerChannel.connect(socketAddress);
      setState(Syn_State.CONNECTED);
    }
    catch (ClassCastException cce)
    {
      throw new Excp_SynapseFatal("Invalid synapse address");
    }
    catch (IOException ioe)
    {
      setState(Syn_State.UNINITIALIZED);
      throw new Excp_SynapseFatal("Could not connect", ioe);
    }
  }

  @Override
  void
  send (Object data)
    throws Excp_SynapseFatal
  {
    if (data == null)
    {
      throw new Excp_SynapseFatal("Data must be provided");
    }
    
    if (peerChannel == null)
    {
      throw new Excp_SynapseFatal("Peer in unknown state");
    }
      
    if (mode != Syn_Mode.PEER)
    {
      throw new Excp_SynapseFatal("Synapse not in peer mode");
    }

    if (state != Syn_State.CONNECTED)
    {
      throw new Excp_SynapseFatal("Synapse not connected");
    }

    try
    {
      ObjectOutputStream outputStream =
          new ObjectOutputStream(Channels.newOutputStream(peerChannel));

      outputStream.writeObject(data);
      outputStream.flush();
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not send", ioe);
    }
  }

  @Override
  void
  send (Syn_Address address, Object data)
    throws Excp_SynapseFatal
  {
    throw new Excp_SynapseFatal("Unsupported operation");
  }
  
  @Override
  Object
  receive ()
    throws Excp_SynapseFatal,
           Excp_SynapseNonFatal
  {
    if (peerChannel == null)
    {
      throw new Excp_SynapseFatal("Peer in unknown state");
    }
      
    if (mode != Syn_Mode.PEER)
    {
      throw new Excp_SynapseFatal("Synapse not in peer mode");
    }

    if (state != Syn_State.CONNECTED)
    {
      throw new Excp_SynapseFatal("Synapse not connected");
    }

//    if (peerChannel.socket().isInputShutdown())
//    {
//      throw new Excp_SynapseFatal("Peer Synapse closed");
//    }
    
    try
    {
      Object data = null;

      ObjectInputStream inputStream =
          new ObjectInputStream(Channels.newInputStream(peerChannel));

      data = inputStream.readObject();
      
      return data;
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new Excp_SynapseFatal("De-serialization error", cnfe);
    }
    catch (StreamCorruptedException sce)
    {
      throw new Excp_SynapseFatal(sce);
    }
    catch (EOFException eofe)
    {
      throw new Excp_SynapseFatal("End of stream", eofe);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not receive", ioe);
    }
  }

  @Override
  public
  void
  close ()
  {
    setState(Syn_State.UNINITIALIZED);

//    try
//    {
//      if (peerChannel != null)
//      {
//        peerChannel.close();
//        peerChannel = null;
//      }
//
//      if (listenerChannel != null)
//      {
//        listenerChannel.close();
//        listenerChannel = null;
//      }
//    }
//    catch (IOException ioe)
//    {
//      throw new Excp_SynapseFatal("Could not close", ioe);
//    }

    cleanable.clean();
  }

  private SocketChannel peerChannel;
  private ServerSocketChannel listenerChannel;

  static class Closer implements Runnable
  {
    Closer ()
    {
      peerChannel = null;
      listenerChannel = null;
    }

    void setPeerChannel (SocketChannel channel)
    {
      peerChannel = channel;
    }

    void setListenerChannel (ServerSocketChannel channel)
    {
      listenerChannel = channel;
    }

    public void run ()
    {
      if (peerChannel != null)
      {
        try
        {
          peerChannel.close();
        }
        catch (IOException ioe)
        {
          // ignore exception
        }
      }

      if (listenerChannel != null)
      {
        try
        {
          listenerChannel.close();
        }
        catch (IOException ioe)
        {
          // ignore exception
        }
      }
    }

    private SocketChannel peerChannel;
    private ServerSocketChannel listenerChannel;
  }

  private final Closer closer;
  private final java.lang.ref.Cleaner.Cleanable cleanable;
}
