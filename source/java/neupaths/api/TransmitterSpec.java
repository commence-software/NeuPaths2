// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * NeuPaths transmitter specification.
 * A transmitter has the following characteristics:
 * <ul>
 * <li>
 * <i>name</i> - A transmitter is identified by a name.
 * </li>
 * <li>
 * <i>stimulus type</i> - A transmitter accepts a specified stimulus type.
 * </li>
 * <li>
 * <i>trace</i> - A transmitter can request network trace.  When enabled,
 *                a trace of the stimulus' synapse path is recorded.  This
 *                information is logged when trace logging is enabled for
 *                the receiving cell.
 * </li>
 * </ul>
 * 
 * @author Aaron Caraveo
 */
public final class TransmitterSpec
{
  /**
   * Creates a transmitter specification.
   * Specifies the name and stimulus type ID.  {@code trace} defaults to
   * {@link StimulusTrace#ENABLED}.
   * 
   * @param name           The name of the transmitter.
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   */
  public
  TransmitterSpec
    (String name,
     UUID   stimulusTypeID)
  {
    if (name == null)
    {
      throw new NeuPathsException("Parameter 'name' is required");
    }
    
    if (stimulusTypeID == null)
    {
      throw new NeuPathsException("Parameter 'stimulusTypeID' is required");
    }
    
    this.name = name;
    this.stimulusTypeID = stimulusTypeID;

    trace = StimulusTrace.ENABLED;
    stimulusClassName = null;
  }

  /**
   * Creates a transmitter specification.
   * 
   * @param name           The name of the transmitter.
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   * @param trace          The trace flag.  A value of {@link StimulusTrace#ENABLED}
   *                       requests that synapse path trace be recorded.
   */
  public
  TransmitterSpec
    (String        name,
     UUID          stimulusTypeID,
     StimulusTrace trace)
  {
    if (name == null)
    {
      throw new NeuPathsException("Parameter 'name' is required");
    }
    
    if (stimulusTypeID == null)
    {
      throw new NeuPathsException("Parameter 'stimulusTypeID' is required");
    }
    
    this.name = name;
    this.stimulusTypeID = stimulusTypeID;
    this.trace = trace;

    stimulusClassName = null;
  }

  /**
   * Copies a transmitter specification.
   * 
   * @param t The source specification.
   */
  public
  TransmitterSpec (TransmitterSpec t)
  {
    name = t.name;
    stimulusTypeID = t.stimulusTypeID;
    trace = t.trace;
    stimulusClassName = t.stimulusClassName;
  }
  
  /**
   * Sets the transmitter name.  Used by tools that deploy NeuPaths cells.
   * 
   * @param name The transmitter name.
   */
  public
  void
  setName (String name)
  {
    this.name = name;
  }
  
  /**
   * Returns the transmitter name.
   * 
   * @return The transmitter name.
   */
  public
  String
  getName ()
  {
    return name;
  }

  /**
   * Sets the stimulus type ID.  Used by tools that deploy NeuPaths cells.
   * 
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   */
  public
  void
  setStimulusTypeID (UUID stimulusTypeID)
  {
    this.stimulusTypeID = stimulusTypeID;
  }
  
  /**
   * Returns the stimulus type ID accepted by the transmitter.
   * 
   * @return The stimulus type ID.
   */
  public
  UUID
  getStimulusTypeID ()
  {
    return stimulusTypeID;
  }

  /**
   * Sets the stimulus trace flag.  Used by tools that deploy NeuPaths cells.
   * 
   * @param trace ENABLED for trace, DISABLED otherwise.
   */
  public
  void
  setTrace (StimulusTrace trace)
  {
    this.trace = trace;
  }
  
  /**
   * Indicates if synapse path trace is enabled.
   * 
   * @return True if enabled, False otherwise.
   */
  public
  boolean
  isTraceEnabled ()
  {
    return (trace == StimulusTrace.ENABLED);
  }
  
  /**
   * Sets the stimulus class name.  Used by tools that deploy NeuPaths cells.
   * 
   * @param stimulusClassName The stimulus class name.
   */
  public
  void
  setStimulusClassName (String stimulusClassName)
  {
    this.stimulusClassName = stimulusClassName;
  }
  
  /**
   * Returns the stimulus class name.
   * 
   * @return The stimulus class name.
   */
  public
  String
  getStimulusClassName ()
  {
    return stimulusClassName;
  }
  
  @Override
  public
  boolean
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

    final TransmitterSpec other = (TransmitterSpec) obj;

    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
    {
      return false;
    }

    if ((this.stimulusTypeID == null) ?
          (other.stimulusTypeID != null) : !this.stimulusTypeID.equals(other.stimulusTypeID))
    {
      return false;
    }

    if (this.trace != other.trace)
    {
      return false;
    }
    
    if ((this.stimulusClassName == null) ?
        (other.stimulusClassName != null) : !this.stimulusClassName.equals(other.stimulusClassName))
    {
      return false;
    }

    return true;
  }

  @Override
  public
  int
  hashCode ()
  {
    String concat = name + stimulusTypeID + trace + stimulusClassName;
    return concat.hashCode();
  }

  StimulusTrace
  getTraceSetting ()
  {
    return trace;
  }

  private String        name;
  private UUID          stimulusTypeID;
  private StimulusTrace trace;
  private String        stimulusClassName;
}
