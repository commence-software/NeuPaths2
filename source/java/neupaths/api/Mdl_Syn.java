// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedHashSet;

final class Mdl_Syn
{
  Mdl_Syn (String synapse, String listener)
  {
    this.synapse = synapse;
    this.listener = listener;

    peers = new LinkedHashSet<>();

    try
    {
      Syn_Name synName = new Syn_Name(synapse);
      name = synName.getModelessText();
      scope = synName.getScope().toString();
      type = synName.getType().toString();
      domain = synName.getDomain();

      address = "";
      for (int i = 0; i < synName.optionCount(); i++)
      {
        if (i > 0)
          address += Syn.SYNAPSE_NAME_DELIM;
        
        address += synName.getOption(i);
      }
    }
    catch (Excp_SynapseFatal sfe)
    {
      throw new NeuPathsException("Invalid synapse", sfe);
    }
  }

  public int
  hashCode ()
  {
    return synapse.hashCode();
  }

  public boolean
  equals (Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }
    
    final Mdl_Syn other = (Mdl_Syn) obj;

    if ((this.synapse == null) ?
        (other.synapse != null) :
        !this.synapse.equals(other.synapse))
    {
      return false;
    }

    if ((this.listener == null) ?
        (other.listener != null) :
        !this.listener.equals(other.listener))
    {
      return false;
    }

    return true;
  }
  
  String synapse;
  String name;
  String scope;
  String type;
  String domain;
  String address;
  String listener;
  LinkedHashSet<String> peers;
}
