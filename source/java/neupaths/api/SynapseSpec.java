// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * NeuPaths synapse specification.  Parses the string-encoded synapse name into
 * its constituent parts.
 * 
 * @author Aaron Caraveo
 */
public class SynapseSpec
{
  /**
   * Creates a synapse specification.
   * 
   * @param synapseName The string-encoded synapse name to parse.
   */
  public
  SynapseSpec (String synapseName)
  {
    try
    {
      synapseSpec = new Syn_Name(synapseName);
    }
    catch (Excp_SynapseFatal tfe)
    {
      throw new NeuPathsException("Could not create synapse specification", tfe);
    }
  }
  
  /** Returns the synapse's scope.
   * 
   * @return The synapse scope.
   */
  public
  String
  getScope ()
  {
    return synapseSpec.getScope().toString();
  }

  /**
   * Returns the synapse's type.
   * 
   * @return The synapse type.
   */
  public  
  String
  getType ()
  {
    return synapseSpec.getType().toString();
  }
  
  /**
   * Returns the synapses's mode.
   * 
   * @return The synapse mode.
   */
  public
  String
  getMode ()
  {
    return synapseSpec.getMode().toString();
  }
  
  /**
   * Returns the synapse's domain.
   * 
   * @return The synapse domain.
   */
  public
  String
  getDomain ()
  {
    return synapseSpec.getDomain();
  }

  /**
   * Returns the number of options used in the synapse specification.
   * 
   * @return The number of optional elements.
   */
  public  
  int
  optionCount ()
  {
    return synapseSpec.optionCount();
  }
  
  /**
   * Retrieves the optional element at the specified index.  The index
   * range is 0 to optionCount - 1.
   * 
   * @param index The index of the optional element.
   * @return The value of the optional element.
   */
  public
  String
  getOption (int index)
  {
    return synapseSpec.getOption(index);
  }

  /**
   * Toggles the synapse's mode (from Peer to Listener and vice versa).
   * Use {@link #toString} to retrieve the updated synapse name.
   */
  public
  void
  toggleMode ()
  {
    Syn_Mode mode = synapseSpec.getMode();

    if (mode == Syn_Mode.PEER)
      synapseSpec.setMode(Syn_Mode.LISTENER);
    else
      synapseSpec.setMode(Syn_Mode.PEER);
  }

  /*
   * Updates the synapse's domain.
   * Use {@link #toString} to retrieve the updated synapse name.
   *
   * @param domain The new domain.
   */
  public
  void
  updateDomain (String domain)
  {
    synapseSpec.setDomain(domain);
  }

  @Override
  public String toString ()
  {
    return synapseSpec.toString();
  }

  Syn_Name synapseSpec;
}
