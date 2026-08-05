// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * Represents a synapse address for an IP-based synapse.
 *
 * @author Aaron Caraveo
 */
final class Syn_InetAddress extends Syn_Address
{
  Syn_InetAddress
    (StandardProtocolFamily addressType,
     Syn_Type               synapseType,
     Syn_Mode               synapseMode,
     String                 domain,
     InetSocketAddress      inetAddress)
    throws Excp_SynapseFatal
  {
    super(addressType, synapseMode, domain);

    if (synapseType == null)
    {
      throw new Excp_SynapseFatal("Parameter 'synapseType' is required");
    }

    if (!Syn_Factory.isValidConfiguration(Syn_Scope.NETWORK, synapseType))
    {
      throw new Excp_SynapseFatal("Parameter 'synapseType' must be Stream, Unicast or Multicast");
    }

    this.synapseType = synapseType;
    this.inetAddress = inetAddress;
    
    try
    {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException uhe)
    {
      host = Syn_Factory.defaultSocketAddress(addressType).getAddress().getHostAddress();
    }
  }

  @Override
  Syn_Scope
  getSynapseScope ()
  {
    return Syn_Scope.NETWORK;
  }
  
  @Override
  Syn_Type
  getSynapseType ()
  {
    return synapseType;
  }
  
  @Override
  String
  getSynapseName ()
  {
    String synapseName = null;
    String hostName = null;

    if (inetAddress != null)
    {
      if (inetAddress.getAddress().isLoopbackAddress() ||
          inetAddress.getAddress().isAnyLocalAddress())
      {
        hostName = host;
      }
      else
      {
        hostName = inetAddress.getAddress().getHostAddress();
      }
      
      LinkedList<String> options = new LinkedList<>();
      
      options.addLast(Integer.toString(inetAddress.getPort()));
      options.addLast(hostName);
      
      Syn_Name synName = null;
      
      try
      {
        synName = new Syn_Name(Syn_Scope.NETWORK,
                               synapseType,
                               synapseMode,
                               domain,
                               options);
      }
      catch (Excp_SynapseFatal tfe)
      {
        throw new NeuPathsException("Internal error", tfe);
      }

      synapseName = synName.getText();
    }

    return synapseName;
  }
  
  @Override
  Object
  getValue ()
  {
    return inetAddress;
  }

  @Override
  public
  String
  toString ()
  {
    return getSynapseName();
  }

  private Syn_Type synapseType;
  private InetSocketAddress inetAddress;
  private String host;
}
