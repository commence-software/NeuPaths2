// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 * Represents a synapse address for a Unix-based (i.e. Unix socket) synapse.
 *
 * @author Aaron Caraveo
 */
final class Syn_UnixAddress extends Syn_Address
{
  Syn_UnixAddress
    (Syn_Type                synapseType,
     Syn_Mode                synapseMode,
     String                  domain,
     UnixDomainSocketAddress unixAddress)
    throws Excp_SynapseFatal
  {
    super(StandardProtocolFamily.UNIX, synapseMode, domain);

    if (synapseType == null)
    {
      throw new Excp_SynapseFatal("Parameter 'synapseType' is required");
    }

    if (!Syn_Factory.isValidConfiguration(Syn_Scope.LOCAL, synapseType))
    {
      throw new Excp_SynapseFatal("Parameter 'synapseType' must be Stream");
    }

    this.synapseType = synapseType;
    this.unixAddress = unixAddress;
  }

  @Override
  Syn_Scope
  getSynapseScope ()
  {
    return Syn_Scope.LOCAL;
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

    if (unixAddress != null)
    {
      LinkedList<String> options = new LinkedList<>();
      
      options.addLast(unixAddress.getPath().toString());
      
      Syn_Name synName = null;
      
      try
      {
        synName = new Syn_Name(Syn_Scope.LOCAL,
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
    return unixAddress;
  }

  @Override
  public
  String
  toString ()
  {
    return getSynapseName();
  }

  private Syn_Type synapseType;
  private UnixDomainSocketAddress unixAddress;
}
