// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;

/**
 * Parses a string-encoded synapse name into its constituent parts and
 * provides routines for retrieving and updating name components.
 *
 * @author Aaron Caraveo
 */
final class Syn_Name
{
  Syn_Name
    (String synapseName)
    throws Excp_SynapseFatal
  {
    // String-encoded synapse name will have the format
    // scope#type#mode#domain[#opt1[#opt2 ... [#optN]]].
    //
    // Examples:
    //   Network#Stream#Listener#@#12345
    //   Network#Unicast#Peer#D1#12345#localhost
    //   Local#Stream#Listener#Domain2#/tmp/cell

    if (synapseName == null)
    {
      throw new Excp_SynapseFatal("Parameter 'synapseName' is required");
    }

    String[] elements = synapseName.split(Syn.SYNAPSE_NAME_DELIM);

    if (elements.length > 4)
    {
      try
      {
        scope = Syn_Scope.translate(elements[0]);
      }
      catch (IllegalArgumentException iae)
      {
        throw new Excp_SynapseFatal("Invalid synapse scope: '" +
                                     synapseName + "' <" + elements[0] + ">");
      }

      try
      {
        type = Syn_Type.translate(elements[1]);
      }
      catch (IllegalArgumentException iae)
      {
        throw new Excp_SynapseFatal("Invalid synapse type: '" +
                                    synapseName + "' <" + elements[1] + ">");
      }

      try
      {
        mode = Syn_Mode.translate(elements[2]);
      }
      catch (IllegalArgumentException iae)
      {
        throw new Excp_SynapseFatal("Invalid synapse mode: '" +
                                    synapseName + "' <" + elements[2] + ">");
      }
      
      domain = elements[3];
      
      options = new LinkedList<>();
      
      for (int i = 4; i < elements.length; i++)
      {
        options.addLast(elements[i]);
      }
    }
    else
    {
      throw new Excp_SynapseFatal("Malformed synapse name: '" +
                                  synapseName + "'");
    }
  }

  Syn_Name
    (Syn_Scope          scope,
     Syn_Type           type,
     Syn_Mode           mode,
     String             domain,
     LinkedList<String> options)
    throws Excp_SynapseFatal
  {
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

    this.scope = scope;
    this.type = type;
    this.mode = mode;
    this.domain = domain;
    
    if (options == null)
      this.options = new LinkedList<String>();
    else
      this.options = options;
  }
  
  Syn_Scope
  getScope ()
  {
    return scope;
  }
  
  Syn_Type
  getType ()
  {
    return type;
  }
  
  Syn_Mode
  getMode ()
  {
    return mode;
  }
  
  String
  getDomain ()
  {
    return domain;
  }
  
  String
  getOption (int index)
  {
    return options.get(index);
  }

  String
  getText ()
  {
    String optionList = "";
    for (int i = 0; i < options.size(); i++)
    {
      optionList += (Syn.SYNAPSE_NAME_DELIM + options.get(i));
    }
    
    String text = scope + Syn.SYNAPSE_NAME_DELIM +
                  type + Syn.SYNAPSE_NAME_DELIM +
                  mode + Syn.SYNAPSE_NAME_DELIM +
                  domain + optionList;

    return text;
  }
  
  int optionCount ()
  {
    return options.size();
  }
  
  void
  setMode (Syn_Mode mode)
  {
    this.mode = mode;
  }

  void
  setDomain (String domain)
  {
    this.domain = domain;
  }

  @Override
  public
  String
  toString ()
  {
    return getText();
  }

  private Syn_Scope scope;  
  private Syn_Type type;
  private Syn_Mode mode;
  private String domain;
  private LinkedList<String> options;
}
