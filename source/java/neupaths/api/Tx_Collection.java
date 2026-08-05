// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

/**
 * A collection of transmitters.
 *
 * @author Aaron Caraveo
 */
final class Tx_Collection implements Iterable<String>
{
  Tx_Collection ()
  {
    names = new LinkedList<>();
    transmitters = new HashMap<>();
  }
  
  Tx_Collection (TransmitterSpecSet transmitters)
    throws Excp_Transmitter
  {
    if (transmitters == null)
    {
      throw new Excp_Transmitter("Parameter 'transmitters' is required");
    }
    
    names = new LinkedList<>();
    
    this.transmitters = new HashMap<>();

    for (TransmitterSpec t : transmitters)
    {
      names.add(t.getName());
      
      this.transmitters.put(t.getName(),
                            new Tx_Transmitter(t.getName(),
                                               t.getStimulusTypeID(),
                                               t.getTraceSetting()));
    }
  }

  synchronized
  void
  addTransmitters (TransmitterSpecSet transmitters)
    throws Excp_Transmitter
  {
    if (transmitters == null)
    {
      throw new Excp_Transmitter("Parameter 'transmitters' is required");
    }

    for (TransmitterSpec t : transmitters)
    {
      names.add(t.getName());
      
      this.transmitters.put(t.getName(),
                            new Tx_Transmitter(t.getName(),
                                               t.getStimulusTypeID(),
                                               t.getTraceSetting()));
    }
  }
  
  synchronized
  void
  insertTransmitter (TransmitterSpec transmitter)
    throws Excp_Transmitter
  {
    if (transmitter == null)
    {
      throw new Excp_Transmitter("Parameter 'transmitter' is required");
    }

    names.addFirst(transmitter.getName());
    
    transmitters.put(transmitter.getName(),
                     new Tx_Transmitter(transmitter.getName(),
                                        transmitter.getStimulusTypeID(),
                                        transmitter.getTraceSetting()));
  }
  
  synchronized
  UUID
  getStimulusTypeID (String name)
    throws Excp_Transmitter
  {
    return getTransmitter(name).getStimulusTypeID();
  }

  synchronized
  boolean
  isTraceEnabled (String name)
    throws Excp_Transmitter
  {
    return getTransmitter(name).isTraceEnabled();
  }
  
  synchronized
  boolean
  hasStimulus (String name)
    throws Excp_Transmitter
  {
    return getTransmitter(name).hasStimulus();
  }

  synchronized
  Stimulus
  getStimulus (String name)
    throws Excp_Transmitter
  {
    return getTransmitter(name).getStimulus();
  }

  synchronized
  Stimulus
  peekStimulus (String name)
    throws Excp_Transmitter
  {
    return getTransmitter(name).peekStimulus();
  }

  synchronized
  void
  setStimulus (String name, Stimulus stimulus)
    throws Excp_Transmitter
  {
    if (stimulus == null)
    {
      throw new Excp_Transmitter("Cannot add null stimulus");
    }

    getTransmitter(name).setStimulus(stimulus);
  }

  synchronized
  void
  clear ()
  {
    for (Tx_Transmitter t : transmitters.values())
    {
      t.clear();
    }
  }
  
  @Override
  public
  Iterator<String>
  iterator ()
  {
    return names.iterator();
  }
  
  private
  Tx_Transmitter
  getTransmitter (String name)
    throws Excp_Transmitter
  {
    Tx_Transmitter t = transmitters.get(name);

    if (t == null)
    {
      throw new Excp_Transmitter(name,
                                 "Transmitter not in collection");
    }

    return t;
  }
    
  private LinkedList<String> names;
  private HashMap<String, Tx_Transmitter> transmitters;
}
