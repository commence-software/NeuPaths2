// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.LinkedList;
import javax.crypto.SealedObject;

/**
 * Synapse for UDP communication (unicast and multicast).
 *
 * @author Aaron Caraveo
 */
final class Syn_InetDatagramChannel extends Syn_Synapse
{
  Syn_InetDatagramChannel
    (StandardProtocolFamily family,
     Syn_Type               type,
     Syn_Mode               mode,
     String                 domain)
  {
    super(family, type, mode, domain, Syn_SessionType.CONNECTIONLESS);

    if (!(type == Syn_Type.UNICAST || type == Syn_Type.MULTICAST))
    {
      throw new NeuPathsException("Syn_InetDatagram must have Unicast or Multicast type");
    }

    peerChannel = null;
    groupMembership = null;

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

  private
  InetSocketAddress
  getLocalAddress ()
  {
    InetSocketAddress rc = null;

    if (state.compareTo(Syn_State.INITIALIZED) >= 0 &&
        state.compareTo(Syn_State.CONNECTED) <= 0)
    {
      try
      {
        rc = (InetSocketAddress) peerChannel.getLocalAddress();
      }
      catch (IOException ioe)
      {
        // ignore - will return null
      }
    }

    return rc;
  }

  private
  InetSocketAddress
  getRemoteAddress ()
  {
    InetSocketAddress rc = null;

    if (state == Syn_State.CONNECTED)
    {
      try
      {
        rc = (InetSocketAddress) peerChannel.getRemoteAddress();
      }
      catch (IOException ioe)
      {
        // ignore - will return null
      }
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
      else if (sa.getAddress().isAnyLocalAddress())
      {
        hostName = Syn_Factory.defaultSocketAddress(family).getAddress().getHostAddress();
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

    try
    {
      if (type != Syn_Type.MULTICAST &&
          mode == Syn_Mode.PEER &&
          socketAddress != null &&
          socketAddress.getAddress().isAnyLocalAddress())
      {
        socketAddress = Syn_Factory.defaultSocketAddress(family, socketAddress.getPort());
      }

      peerChannel = DatagramChannel.open(family);
      closer.setChannel(peerChannel);
      peerChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      setState(Syn_State.INITIALIZED);

      // Bind socket to address.  If socketAddress is null, the OS will
      // choose the address and port
      peerChannel.bind(socketAddress);
      setState(Syn_State.OPEN);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not open channel", ioe);
    }
  }

  @Override
  Syn_Synapse
  accept ()
    throws Excp_SynapseFatal
  {
    throw new Excp_SynapseFatal("Unsupported operation");
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
    
    if (!(state == Syn_State.OPEN ||
          state == Syn_State.CONNECTED))
    {
      throw new Excp_SynapseFatal("Synapse in invalid state");
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
    
    if (state != Syn_State.CONNECTED)
    {
      throw new Excp_SynapseFatal("Synapse in invalid state");
    }

    try
    {
      byte[] objectData = formatPayload(data);

      DatagramPacket msg = new DatagramPacket(objectData,
                                              objectData.length,
                                              getRemoteAddress());

      peerChannel.socket().send(msg);
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
    if (address == null)
    {
      throw new Excp_SynapseFatal("Parameter 'address' is required");
    }
    
    if (data == null)
    {
      throw new Excp_SynapseFatal("Data must be provided");
    }
    
    try
    {
      byte[] objectData = formatPayload(data);

      DatagramPacket msg =
          new DatagramPacket(objectData,
                             objectData.length,
                             (InetSocketAddress) address.getValue());

      peerChannel.socket().send(msg);
    }
    catch (ClassCastException cce)
    {
      throw new Excp_SynapseFatal("Invalid synapse address");
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not send", ioe);
    }
  }

  @Override
  Object
  receive ()
    throws Excp_SynapseFatal,
           Excp_SynapseNonFatal
  {
    if (!(state == Syn_State.OPEN ||
          state == Syn_State.CONNECTED))
    {
      throw new Excp_SynapseFatal("Synapse in invalid state");
    }

    try
    {
      byte[] receiveBuffer = new byte[RECEIVE_BUFFER_SIZE];

      DatagramPacket msg = new DatagramPacket(receiveBuffer, receiveBuffer.length);

      peerChannel.socket().receive(msg);

      Object data = extractPayload(msg);

      return data;
    }
    catch (EOFException eofe)
    {
      throw new Excp_SynapseNonFatal("End of stream", eofe);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new Excp_SynapseFatal("De-serialization error", cnfe);
    }
    catch (StreamCorruptedException sce)
    {
      throw new Excp_SynapseFatal(sce);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not receive", ioe);
    }
  }

  void
  join
    (Syn_Address      group,
     NetworkInterface netInterface)
    throws Excp_SynapseFatal
  {
    NetworkInterface localNetInterface = netInterface;
    InetSocketAddress groupSocketAddress = null;
    
    if (!(state == Syn_State.OPEN ||
          state == Syn_State.CONNECTED))
    {
      throw new Excp_SynapseFatal("Synapse in invalid state");
    }
    
    if (group == null || group.getValue() == null)
    {
      throw new Excp_SynapseFatal("Parameter 'group' is required");
    }

    try
    {
      groupSocketAddress = (InetSocketAddress) group.getValue();
    }
    catch (ClassCastException cce)
    {
      throw new Excp_SynapseFatal("Invalid synapse address");
    }

    if (!groupSocketAddress.getAddress().isMulticastAddress())
    {
      throw new Excp_SynapseFatal("Parameter 'group' must contain a Multicast IP address");
    }

    try
    {
      if (localNetInterface == null)
      {
        InetSocketAddress localSocketAddress = Syn_Factory.defaultSocketAddress(family);
        
        localNetInterface = NetworkInterface.getByInetAddress(localSocketAddress.getAddress());
      }
      
      groupMembership = peerChannel.join(groupSocketAddress.getAddress(), localNetInterface);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not join group", ioe);
    }
  }

  void
  leave
    (Syn_Address      group,
     NetworkInterface netInterface)
    throws Excp_SynapseFatal
  {
    NetworkInterface localNetInterface = netInterface;
    InetSocketAddress groupSocketAddress = null;
    
    if (!(state == Syn_State.OPEN ||
          state == Syn_State.CONNECTED))
    {
      throw new Excp_SynapseFatal("Synapse in invalid state");
    }

    if (group == null || group.getValue() == null)
    {
      throw new Excp_SynapseFatal("Parameter 'group' is required");
    }

    try
    {
      groupSocketAddress = (InetSocketAddress) group.getValue();
    }
    catch (ClassCastException cce)
    {
      throw new Excp_SynapseFatal("Invalid synapse address");
    }

    if (!groupSocketAddress.getAddress().isMulticastAddress())
    {
      throw new Excp_SynapseFatal("Parameter 'group' must contain a Multicast IP address");
    }

    try
    {
      if (localNetInterface == null)
      {
        InetSocketAddress localSocketAddress = Syn_Factory.defaultSocketAddress(family);
        
        localNetInterface = NetworkInterface.getByInetAddress(localSocketAddress.getAddress());
      }
      
      //channel.socket().leaveGroup(socketAddress, netInterface);
      groupMembership.drop();
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not leave group", ioe);
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
//      peerChannel.close();
//      peerChannel = null;
//    }
//    catch (IOException ioe)
//    {
//      throw new Excp_SynapseFatal("Could not close", ioe);
//    }

    cleanable.clean();
  }

  boolean
  loopbackEnabled ()
    throws Excp_SynapseFatal
  {
    try
    {
      return peerChannel.getOption(StandardSocketOptions.IP_MULTICAST_LOOP);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not query loopback mode", ioe);
    }
  }
  
  void
  setLoopbackEnabled (boolean enabled)
    throws Excp_SynapseFatal
  {
    try
    {
      peerChannel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, enabled);
    }
    catch (IOException ioe)
    {
      throw new Excp_SynapseFatal("Could not set loopback mode", ioe);
    }
  }
  
  byte[]
  formatPayload (Object data)
    throws IOException
  {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

    objectStream.writeObject(data);
    objectStream.flush();

    return byteStream.toByteArray();
  }
  
  Object
  extractPayload (DatagramPacket msg)
    throws IOException, ClassNotFoundException
  {
    Object data = null;

    ByteArrayInputStream byteStream = new ByteArrayInputStream(msg.getData());
    ObjectInputStream objectStream =  new ObjectInputStream(byteStream);

    data = objectStream.readObject();
    
    return data;
  }
  
  private DatagramChannel peerChannel;
  private MembershipKey groupMembership;
  private String host;
  
  // Size of receive buffer in bytes
  private static final int RECEIVE_BUFFER_SIZE = 10240;

  static class Closer implements Runnable
  {
    Closer ()
    {
      peerChannel = null;
    }

    void setChannel (DatagramChannel channel)
    {
      peerChannel = channel;
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
    }

    private DatagramChannel peerChannel;
  }

  private final Closer closer;
  private final java.lang.ref.Cleaner.Cleanable cleanable;
}
