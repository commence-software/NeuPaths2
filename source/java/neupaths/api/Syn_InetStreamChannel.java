// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedList;
import javax.crypto.SealedObject;

/**
 * Synapse for TCP communication.
 *
 * @author Aaron Caraveo
 */
final class Syn_InetStreamChannel extends Syn_Synapse
{
  Syn_InetStreamChannel
    (StandardProtocolFamily family,
     Syn_Type               type,
     Syn_Mode               mode,
     String                 domain)
  {
    super(family, type, mode, domain, Syn_SessionType.CONNECTIONED);

    if (type != Syn_Type.STREAM)
    {
      throw new NeuPathsException("Syn_InetStream must have Stream type");
    }

    peerChannel = null;
    listenerChannel = null;

    try
    {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException uhe)
    {
      host = Syn_Factory.defaultSocketAddress(family).getAddress().getHostAddress();
    }        

    closer = new Closer();
    cleanable = cleaner.register(this, closer);
  }

  // Private constructor for creating Synapse from
  // accepted connection
  Syn_InetStreamChannel
    (StandardProtocolFamily family,
     SocketChannel          peer,
     String                 domain)
  {
    super(family,
          Syn_Type.STREAM,
          Syn_Mode.PEER,
          domain,
          Syn_SessionType.CONNECTIONED,
          Syn_State.CONNECTED);

    peerChannel = peer;
    listenerChannel = null;

    try
    {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException uhe)
    {
      host = Syn_Factory.defaultSocketAddress(family).getAddress().getHostAddress();
    }

    closer = new Closer();
    cleanable = cleaner.register(this, closer);

    closer.setPeerChannel(peerChannel);
  }

  private
  InetSocketAddress
  getLocalAddress ()
  {
    InetSocketAddress rc = null;

    if (state.compareTo(Syn_State.INITIALIZED) >= 0 &&
        state.compareTo(Syn_State.CONNECTED) <= 0)
    {
      switch (mode)
      {
        case PEER:
          try
          {
            rc = (InetSocketAddress) peerChannel.getLocalAddress();
          }
          catch (IOException ioe)
          {
            // ignore - will return null
          }
          break;

        case LISTENER:
          try
          {
            rc = (InetSocketAddress) listenerChannel.getLocalAddress();
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
  InetSocketAddress
  getRemoteAddress ()
  {
    InetSocketAddress rc = null;

    switch (mode)
    {
      case PEER:
        if (state == Syn_State.CONNECTED)
          try
          {
            rc = (InetSocketAddress) peerChannel.getRemoteAddress();
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

    InetSocketAddress sa = getLocalAddress();

    if (sa != null)
    {
      if (sa.getAddress().isLoopbackAddress())
      {
        hostName = host;
      }
      else
      {
        hostName = sa.getAddress().getHostAddress();
      }
      
      LinkedList<String> options = new LinkedList<>();
      
      options.addLast(Integer.toString(sa.getPort()));
      options.addLast(hostName);
      
      Syn_Name synName = new Syn_Name(Syn_Scope.NETWORK,
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

    InetSocketAddress sa = getRemoteAddress();

    if (sa != null)
    {
      if (sa.getAddress().isLoopbackAddress())
      {
        hostName = host;
      }
      else
      {
        hostName = sa.getAddress().getHostAddress();
      }
      
      LinkedList<String> options = new LinkedList<>();
      
      options.addLast(Integer.toString(sa.getPort()));
      options.addLast(hostName);
      
      Syn_Name synName = new Syn_Name(Syn_Scope.NETWORK,
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

    InetSocketAddress socketAddress = null;    
    if (address != null)
    {
      try
      {
        socketAddress = (InetSocketAddress) address.getValue();
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
          if (socketAddress != null &&
              socketAddress.getAddress().isAnyLocalAddress())
          {
            socketAddress = Syn_Factory.defaultSocketAddress(family, socketAddress.getPort());
          }
          
          peerChannel = SocketChannel.open(family);
          closer.setPeerChannel(peerChannel);
          peerChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);//.setReuseAddress(true);
          peerChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);//.setKeepAlive(true);
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
          listenerChannel = ServerSocketChannel.open(family);
          closer.setListenerChannel(listenerChannel);
          listenerChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
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
      peer.setOption(StandardSocketOptions.SO_KEEPALIVE, true);//.setKeepAlive(true);
      rc = new Syn_InetStreamChannel(family, peer, domain);
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
      InetSocketAddress socketAddress = (InetSocketAddress) address.getValue();
      
      if (socketAddress != null &&
          socketAddress.getAddress().isAnyLocalAddress())
      {
        socketAddress = Syn_Factory.defaultSocketAddress(family, socketAddress.getPort());
      }

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

    if (peerChannel.socket().isInputShutdown())
    {
      throw new Excp_SynapseFatal("Peer synapse closed");
    }
    
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
  private String host;

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
