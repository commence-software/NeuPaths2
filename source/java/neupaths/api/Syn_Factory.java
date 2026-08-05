// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * Creates synapses based on provided synapse names.  Also contains
 * utility functions for parsing synapse names and creating synapse
 * addresses.
 *
 * @author Aaron Caraveo
 */
final class Syn_Factory
{
  static
  Syn_Address
  createAddress (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = null;
    
    if (synapseName != null)
    {
      synName = new Syn_Name(synapseName);
    }
    
    return createAddress(synName);
  }
  
  static
  Syn_Address
  createAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    Syn_Address synapseAddress = null;

    if (synName != null)
    {
      switch (synName.getScope())
      {
        case NETWORK:
          switch (synName.getType())
          {
            case STREAM:
              synapseAddress = createNetworkStreamAddress(synName);
              break;

            case UNICAST:
              synapseAddress = createNetworkUnicastAddress(synName);
              break;

            case MULTICAST:
              synapseAddress = createNetworkMulticastAddress(synName);
              break;

            default:
              throw new Excp_SynapseFatal("Synapse type " +
                                          synName.getType() +
                                          " is illegal in this context");
          }
          break;
          
        case LOCAL:
          switch (synName.getType())
          {
            case STREAM:
              synapseAddress = createLocalStreamAddress(synName);
              break;

            default:
              throw new Excp_SynapseFatal("Synapse type " +
                                          synName.getType() +
                                          " is illegal in this context");
          }
          break;
      }
    }  // if synName != null

    return synapseAddress;
  }  // end createAddress

  static
  Syn_Type
  getNameType (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    
    return synName.getType();
  }

  static
  Syn_Mode
  getNameMode (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    
    return synName.getMode();
  }

  static
  String
  getNameDomain (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    
    return synName.getDomain();
  }

  static
  String
  getInetNameAddress (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    Syn_Scope scope = synName.getScope();
    Syn_Type type = synName.getType();

    if (scope != Syn_Scope.NETWORK)
    {
      throw new Excp_SynapseFatal("Synapse scope " + scope +
                                  " is illegal in this conetxt");      
    }
    
    if (!isValidConfiguration(scope, type))
    {
      throw new Excp_SynapseFatal("Synapse type " + type +
                                  " is illegal in this conetxt");      
    }
    
    String address = null;

    if (synName.optionCount() == 1)  // Address not specified
    {
      try
      {
        address = InetAddress.getLocalHost().getHostName();
      }
      catch (UnknownHostException uhe)
      {
        address = defaultSocketAddress(StandardProtocolFamily.INET).getAddress().getHostAddress();
      }
    }
    else if (synName.optionCount() == 2)
    {
      address = synName.getOption(1);

      if (address.contains("4/") || address.contains("6/"))
      {
        String[] elements = address.split("/");
        address = elements[1];
      }

      if (address.equals("*"))
      {
        try
        {
          address = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException uhe)
        {
          address = defaultSocketAddress(StandardProtocolFamily.INET).getAddress().getHostAddress();
        }
      }
    }
    else
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synapseName + "'");
    }

    return address;
  }

  static
  int
  getInetNamePort (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    Syn_Scope scope = synName.getScope();
    Syn_Type type = synName.getType();

    if (scope != Syn_Scope.NETWORK)
    {
      throw new Excp_SynapseFatal("Synapse scope " + scope +
                                  " is illegal in this conetxt");      
    }
    
    if (!isValidConfiguration(scope, type))
    {
      throw new Excp_SynapseFatal("Synapse type " + type +
                                  " is illegal in this conetxt");      
    }
    
    int port = 0;

    try
    {
      if (synName.optionCount() >= 1)
      {
        port = Integer.parseInt(synName.getOption(0));
      }
      else
      {
        throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                    synapseName + "'");
      }
    }
    catch (NumberFormatException nfe)
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synapseName + "'");
    }

    return port;
  }

  static
  String
  updateInetNameAddress (String synapseName, String address)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    Syn_Scope scope = synName.getScope();
    Syn_Type type = synName.getType();

    if (scope != Syn_Scope.NETWORK)
    {
      throw new Excp_SynapseFatal("Synapse scope " + scope +
                                  " is illegal in this conetxt");      
    }
    
    if (!isValidConfiguration(scope, type))
    {
      throw new Excp_SynapseFatal("Synapse type " + type +
                                  " is illegal in this context");
    }
    
    if (address == null)
    {
      throw new Excp_SynapseFatal("Parameter 'address' is required");
    }

    LinkedList<String> newOptions = new LinkedList<>();

    if (synName.optionCount() >= 1 && synName.optionCount() <= 2)
    {
      newOptions.addLast(synName.getOption(0));  // port in first option
      newOptions.addLast(address);
    }
    else
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synapseName + "'");
    }

    Syn_Name newName = new Syn_Name(synName.getScope(),
                                    synName.getType(),
                                    synName.getMode(),
                                    synName.getDomain(),
                                    newOptions);

    return newName.getText();
  }  // end updateInetNameAddress

  static
  String
  updateInetNamePort (String synapseName, int port)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    Syn_Scope scope = synName.getScope();
    Syn_Type type = synName.getType();

    if (scope != Syn_Scope.NETWORK)
    {
      throw new Excp_SynapseFatal("Synapse scope " + scope +
                                  " is illegal in this conetxt");      
    }
    
    if (!isValidConfiguration(scope, type))
    {
      throw new Excp_SynapseFatal("Synapse type " + type +
                                  " is illegal in this context");
    }
    
    LinkedList<String> newOptions = new LinkedList<>();

    if (synName.optionCount() == 1)  // Address not specified
    {
      newOptions.addLast(Integer.toString(port));
    }
    else if (synName.optionCount() == 2)
    {
      newOptions.addLast(Integer.toString(port));
      newOptions.addLast(synName.getOption(1));
    }
    else
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synapseName + "'");
    }

    Syn_Name newName = new Syn_Name(synName.getScope(),
                                    synName.getType(),
                                    synName.getMode(),
                                    synName.getDomain(),
                                    newOptions);

    return newName.getText();
  }  // end updateInetNamePort

  static
  Syn_Synapse
  createSynapse (String synapseName)
    throws Excp_SynapseFatal
  {
    Syn_Name synName = new Syn_Name(synapseName);
    Syn_Scope scope = synName.getScope();
    Syn_Type type = synName.getType();
    Syn_Mode mode = synName.getMode();
    String domain = synName.getDomain();

    String address = null;
    StandardProtocolFamily family = null;

    Syn_Synapse synapse = null;

    switch (scope)
    {
      case NETWORK:
        if (synName.optionCount() == 2)
        {
          address = synName.getOption(1);
        }
        else
        {
          address = "*";
        }

        if (address.contains("4/") || address.contains("6/"))
        {
          if (address.contains("6/"))
            family = StandardProtocolFamily.INET6;
          else
            family = StandardProtocolFamily.INET;
        }
        else if (address.contains(":"))
        {
          family = StandardProtocolFamily.INET6;
        }
        else
        {
          family = StandardProtocolFamily.INET;
        }

        switch (type)
        {
          case STREAM:
            synapse = new Syn_InetStreamChannel(family, type, mode, domain);
            break;

          case UNICAST:
          case MULTICAST:
            synapse = new Syn_InetDatagramChannel(family, type, mode, domain);
            break;

          default:
            throw new Excp_SynapseFatal("Synapse type " + type +
                                        " is illegal in this conetxt");
        }
        break;
        
      case LOCAL:
        switch (type)
        {
          case STREAM:
            synapse = new Syn_UnixStreamChannel(type, mode, domain);
            break;

          default:
            throw new Excp_SynapseFatal("Synapse type " + type +
                                        " is illegal in this conetxt");
        }
        break;
    }

    return synapse;
  }

  static
  Syn_Synapse
  createSynapse (Syn_Address address)
    throws Excp_SynapseFatal
  {
    if (address == null)
    {
      throw new Excp_SynapseFatal("Parameter 'address' is required");
    }

    Syn_Synapse synapse = null;

    switch (address.getSynapseScope())
    {
      case NETWORK:
        switch (address.getSynapseType())
        {
          case STREAM:
            synapse = new Syn_InetStreamChannel(address.getFamily(),
                                                address.getSynapseType(),
                                                address.getSynapseMode(),
                                                address.getDomain());
            break;

          case UNICAST:
          case MULTICAST:
            synapse = new Syn_InetDatagramChannel(address.getFamily(),
                                                  address.getSynapseType(),
                                                  address.getSynapseMode(),
                                                  address.getDomain());
            break;

          default:
            throw new Excp_SynapseFatal("Synapse type " +
                                        address.getSynapseType() +
                                        " is illegal in this conetxt");
        }
        break;
        
      case LOCAL:
        switch (address.getSynapseType())
        {
          case STREAM:
            synapse = new Syn_UnixStreamChannel(address.getSynapseType(),
                                                address.getSynapseMode(),
                                                address.getDomain());
            break;

          default:
            throw new Excp_SynapseFatal("Synapse type " +
                                        address.getSynapseType() +
                                        " is illegal in this conetxt");
        }
        break;
    }
    
    return synapse;
  }

  static
  Syn_Synapse
  createSynapse
    (StandardProtocolFamily family,
     Syn_Scope              scope,
     Syn_Type               type,
     Syn_Mode               mode,
     String                 domain)
    throws Excp_SynapseFatal
  {
    if (family == null)
    {
      throw new Excp_SynapseFatal("Parameter 'family' is required");
    }

    if (scope == null)
    {
      throw new Excp_SynapseFatal("Parameter 'scope' is required");
    }

    if (type == null)
    {
      throw new Excp_SynapseFatal("Parameter 'type' is required");
    }

    if (mode == null)
    {
      throw new Excp_SynapseFatal("Parameter 'mode' is required");
    }

    if (domain == null)
    {
      throw new Excp_SynapseFatal("Parameter 'domain' is required");
    }

    Syn_Synapse synapse = null;

    switch (scope)
    {
      case NETWORK:
        switch (type)
        {
          case STREAM:
            synapse = new Syn_InetStreamChannel(family, type, mode, domain);
            break;

          case UNICAST:
          case MULTICAST:
            synapse = new Syn_InetDatagramChannel(family, type, mode, domain);
            break;

          default:
            throw new Excp_SynapseFatal("Synapse type " + type +
                                        " is illegal in this conetxt");
        }
        break;
        
      case LOCAL:
        switch (type)
        {
          case STREAM:
            synapse = new Syn_UnixStreamChannel(type, mode, domain);
            break;

          default:
            throw new Excp_SynapseFatal("Synapse type " + type +
                                        " is illegal in this conetxt");
        }
        break;
    }
    
    return synapse;
  }
  
  static
  InetSocketAddress
  defaultSocketAddress (StandardProtocolFamily family, int port)
  {
    InetSocketAddress rc = null;
    InetAddress global = null;
    InetAddress local = null;
    
    try
    {
      Enumeration<NetworkInterface> interfaces =
        NetworkInterface.getNetworkInterfaces();

      while (interfaces.hasMoreElements())
      {
        NetworkInterface i = interfaces.nextElement();

        Enumeration<InetAddress> addresses = i.getInetAddresses();
        
        while (addresses.hasMoreElements())
        {
          InetAddress a = addresses.nextElement();
          
          if (a instanceof Inet4Address &&
              family == StandardProtocolFamily.INET)
          {
            if (!a.isAnyLocalAddress() && !a.isLoopbackAddress())
            {
              if ((a.isLinkLocalAddress() ||
                   a.isSiteLocalAddress()) && local == null)
                local = a;
              else if (global == null)
                global = a;
            }
          }
          else if (a instanceof Inet6Address &&
                   family == StandardProtocolFamily.INET6)
          {
            if (!a.isAnyLocalAddress() && !a.isLoopbackAddress())
            {
              if ((a.isLinkLocalAddress() ||
                   a.isSiteLocalAddress()) && local == null)
                local = a;
              else if (global == null)
                global = a;
            }
          }
        }  // while more addresses
      }  // while more interfaces

      if (global != null)
        rc = new InetSocketAddress(global, port);
      else
        rc = new InetSocketAddress(local, port);
    }
    catch (SocketException se)
    {
      try
      {
        rc = new InetSocketAddress(InetAddress.getByName("127.0.0.1"),
                                   port);
      }
      catch (UnknownHostException uhe)
      {
        // Ignore.  Shouldn't happen in this case.
      }
    }

    return rc;
  }  // end defaultSocketAddress

  static
  InetSocketAddress
  defaultSocketAddress (StandardProtocolFamily family)
  {
    return defaultSocketAddress(family, 0);
  }
  
  static
  String
  defaultHostAddress (StandardProtocolFamily family)
  {
    return defaultSocketAddress(family).getAddress().getHostAddress();
  }

  static
  boolean
  isValidConfiguration (Syn_Scope scope, Syn_Type type)
  {
    boolean rc = true;

    switch (scope)
    {
      case NETWORK:
        if (!(type == Syn_Type.STREAM ||
              type == Syn_Type.UNICAST ||
              type == Syn_Type.MULTICAST))
        {
          rc = false;
        }
        break;
      case LOCAL:
        if (type != Syn_Type.STREAM)
        {
          rc = false;
        }
        break;
    }

    return rc;
  }
  
  private static
  Syn_Address
  createNetworkStreamAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    Syn_InetAddress synapseAddress = createInetAddress(synName);

    InetSocketAddress socketAddress = (InetSocketAddress)synapseAddress.getValue();
    
    if (socketAddress.getAddress().isMulticastAddress())
    {
      throw new Excp_SynapseFatal("Stream synapse requires non-multicast IP address");
    }

    return synapseAddress;
  }

  private static
  Syn_Address
  createNetworkUnicastAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    Syn_InetAddress synapseAddress = createInetAddress(synName);

    InetSocketAddress socketAddress = (InetSocketAddress)synapseAddress.getValue();
    
    if (socketAddress.getAddress().isMulticastAddress())
    {
      throw new Excp_SynapseFatal("Unicast synapse requires non-multicast IP address");
    }

    return synapseAddress;
  }

  private static
  Syn_Address
  createNetworkMulticastAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    Syn_InetAddress synapseAddress = createInetAddress(synName);
    
    InetSocketAddress socketAddress = (InetSocketAddress)synapseAddress.getValue();
    
    if (!socketAddress.getAddress().isAnyLocalAddress())
    {
      if (!socketAddress.getAddress().isMulticastAddress())
      {
        throw new Excp_SynapseFatal("Multicast synapse requires multicast IP address");
      }
    }

    return synapseAddress;
  }
  
  private static
  Syn_Address
  createLocalStreamAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    return createUnixAddress(synName);
  }

  private static
  Syn_InetAddress
  createInetAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    StandardProtocolFamily family = null;
    String portField = null;
    String addrField = null;

    if (synName.optionCount() == 2)  // Address specified
    {
      portField = synName.getOption(0);
      addrField = synName.getOption(1);
    }
    else if (synName.optionCount() == 1)
    {
      portField = synName.getOption(0);
      addrField = "*";
    }
    else
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synName.getText() + "'");
    }

    InetSocketAddress socketAddress = null;

    int port = 0;

    // Check if port has been specified
    if (!portField.equals("*"))
    {
      try
      {
        port = Integer.parseInt(portField);
      }
      catch (NumberFormatException nfe)
      {
        throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                    synName.getText() + "'");
      }
    }

    // Determine address family
    if (addrField.contains("4/") || addrField.contains("6/"))
    {
      if (addrField.contains("6/"))
        family = StandardProtocolFamily.INET6;
      else
        family = StandardProtocolFamily.INET;

      String[] elements = addrField.split("/");
      addrField = elements[1];
    }
    else if (addrField.contains(":"))
    {
      family = StandardProtocolFamily.INET6;
    }
    else
    {
      family = StandardProtocolFamily.INET;
    }

    // Check if IP address has been specified
    if (addrField.equals("*"))
    {
      if (port != 0)
      {
        socketAddress = new InetSocketAddress(port);
      }
    }
    else
    {
      socketAddress = new InetSocketAddress(addrField, port);

      if (socketAddress.isUnresolved())
      {
        throw new Excp_SynapseFatal("Could not resolve socket address for synapse name: " + synName.getText());
      }
    }

    return new Syn_InetAddress(family,
                               synName.getType(),
                               synName.getMode(),
                               synName.getDomain(),
                               socketAddress);
  }

  private static
  Syn_UnixAddress
  createUnixAddress (Syn_Name synName)
    throws Excp_SynapseFatal
  {
    String pathField = null;

    if (synName.optionCount() == 1)
    {
      pathField = synName.getOption(0);
    }
    else
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synName.getText() + "'");
    }

    UnixDomainSocketAddress socketAddress = UnixDomainSocketAddress.of(pathField);

    return new Syn_UnixAddress(synName.getType(),
                               synName.getMode(),
                               synName.getDomain(),
                               socketAddress);
  }
}
