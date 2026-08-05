// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import java.util.UUID;

/**
 * Represents an {@link Activator}'s transmitter.
 *
 * @author Aaron Caraveo
 */
final class Tx_Transmitter
{
  Tx_Transmitter
    (String        name,
     UUID          stimulusTypeID,
     StimulusTrace trace)
  {
    this.name = name;
    this.stimulusTypeID = stimulusTypeID;
    this.trace = trace;
    
    stimuli = new LinkedList<>();
  }

  String
  getName ()
  {
    return name;
  }

  UUID
  getStimulusTypeID ()
  {
    return stimulusTypeID;
  }
  
  boolean
  isTraceEnabled ()
  {
    return (trace == StimulusTrace.ENABLED);
  }

  StimulusTrace
  getTraceSetting ()
  {
    return trace;
  }

  synchronized
  boolean
  hasStimulus ()
  {
    return (!stimuli.isEmpty());
  }

  synchronized
  Stimulus
  getStimulus ()
    throws Excp_Transmitter
  {
    Stimulus s = stimuli.pollFirst();

    if (s == null)
    {
      throw new Excp_Transmitter(name, "Transmitter has no stimulus");
    }

    return s;
  }

  synchronized
  Stimulus
  peekStimulus ()
    throws Excp_Transmitter
  {
    Stimulus s = stimuli.peekFirst();

    if (s == null)
    {
      throw new Excp_Transmitter(name, "Transmitter has no stimulus");
    }

    return s;
  }

  synchronized
  void
  setStimulus (Stimulus stimulus)
    throws Excp_Transmitter
  {
    if (stimulus == null)
    {
      throw new Excp_Transmitter(name, "Cannot add null stimulus");
    }

    if (!stimulusTypeID.equals(stimulus.getTypeID()))
    {
      throw new Excp_Transmitter(name,
                                 "Type mismatch (t=" + stimulusTypeID +
                                 ", s=" + stimulus.getTypeID() + ")");
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
  
  private String        name;
  private UUID          stimulusTypeID;
  private StimulusTrace trace;

  private LinkedList<Stimulus> stimuli;
}
