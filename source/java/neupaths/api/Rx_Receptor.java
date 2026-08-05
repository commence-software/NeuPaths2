// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import java.util.UUID;

/**
 * Represents an {@link Activator}'s receptor.
 *
 * @author Aaron Caraveo
 */
final class Rx_Receptor
{
  Rx_Receptor
    (String       name,
     ReceptorMode mode,
     UUID         stimulusTypeID)
  {
    this.name = name;
    this.mode = mode;
    this.stimulusTypeID = stimulusTypeID;

    stimuli = new LinkedList<Stimulus>();
  }

  final
  String
  getName ()
  {
    return name;
  }

  final
  ReceptorMode
  getMode ()
  {
    return mode;
  }
  
  final
  UUID
  getStimulusTypeID ()
  {
    return stimulusTypeID;
  }

  synchronized
  boolean
  hasStimulus ()
  {
    return (!stimuli.isEmpty());
  }

  @SuppressWarnings("unchecked")
  synchronized
  <T extends Stimulus> T
  getStimulus ()
    throws Excp_Receptor
  {
    T s = null;
    
    try
    {
      s = (T) stimuli.pollFirst();
    }
    catch (ClassCastException cce)
    {
      throw new Excp_Receptor(getName(), "Stimulus type mismatch", cce);
    }

    if (s == null)
    {
      throw new Excp_Receptor(getName(), "Receptor has no stimulus");
    }

    return s;
  }

  @SuppressWarnings("unchecked")
  synchronized
  <T extends Stimulus> T
  peekStimulus ()
    throws Excp_Receptor
  {
    T s = null;
    
    try
    {
      s = (T) stimuli.peekFirst();
    }
    catch (ClassCastException cce)
    {
      throw new Excp_Receptor(getName(), "Stimulus type mismatch", cce);
    }

    if (s == null)
    {
      throw new Excp_Receptor(getName(), "Receptor has no stimulus");
    }

    return s;
  }

  synchronized
  void
  setStimulus (Stimulus stimulus)
    throws Excp_Receptor
  {
    if (stimulus == null)
    {
      throw new Excp_Receptor(getName(), "Cannot add null stimulus");
    }

    if (!stimulusTypeID.equals(stimulus.getTypeID()))
    {
      throw new Excp_Receptor(getName(),
                              "Type mismatch (r=" + stimulusTypeID +
                              ", s=" + stimulus.getTypeID() + ")");
    }

    if (mode == ReceptorMode.NON_BUFFERED)
    {
      // An non-buffered receptor should contain at most one stimulus.
      // Clear the list before adding a new entry.
      stimuli.clear();
    }
    
    stimuli.addLast(stimulus);
  }

  synchronized
  int
  depth ()
  {
    return stimuli.size();
  }

  synchronized
  void
  clear ()
  {
    stimuli.clear();
  }
  
  private String  name;
  private ReceptorMode mode;
  private UUID stimulusTypeID;
  private LinkedList<Stimulus> stimuli;
}
