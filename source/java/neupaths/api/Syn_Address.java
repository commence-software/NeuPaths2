// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.net.StandardProtocolFamily;

/**
 * Base class for all synapse addresses.
 *
 * @author Aaron Caraveo
 */
abstract class Syn_Address
{
  Syn_Address
    (StandardProtocolFamily family,
     Syn_Mode               synapseMode,
     String                 domain)
    throws Excp_SynapseFatal
  {
    if (family == null)
    {
      throw new Excp_SynapseFatal("Parameter 'family' is required");
    }

    if (synapseMode == null)
    {
      throw new Excp_SynapseFatal("Parameter 'synapseMode' is required");
    }

    if (domain == null)
    {
      throw new Excp_SynapseFatal("Parameter 'domain' is required");
    }
      
    this.family = family;
    this.synapseMode = synapseMode;
    this.domain = domain;
  }

  final
  StandardProtocolFamily
  getFamily ()
  {
    return family;
  }

  final
  Syn_Mode
  getSynapseMode ()
  {
    return synapseMode;
  }

  final
  String
  getDomain ()
  {
    return domain;
  }
  
  @Override
  public
  String
  toString ()
  {
    return getSynapseName();
  }
  
  abstract Syn_Scope getSynapseScope ();
  
  abstract Syn_Type getSynapseType ();
  
  abstract String getSynapseName ();

  abstract Object getValue ();

  StandardProtocolFamily family;
  Syn_Mode synapseMode;
  String domain;
}
