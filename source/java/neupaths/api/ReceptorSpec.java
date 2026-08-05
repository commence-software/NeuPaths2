// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * NeuPaths receptor specification.
 * A receptor has the following characteristics:
 * <ul>
 * <li>
 * <i>name</i> - A receptor is identified by a name.
 * </li>
 * <li>
 * <i>mode</i> - A receptor is Buffered or Non-buffered
 *               (see {@link ReceptorMode}.)
 * </li>
 * <li>
 * <i>stimulus type</i> - A receptor accepts a specified stimulus type.
 * </li>
 * </ul>
 * 
 * @author Aaron Caraveo
 */
public final class ReceptorSpec
{
  /**
   * Creates a receptor specification.
   * 
   * @param name           The name of the receptor.
   * @param mode           The mode of the receptor.
   * @param stimulusTypeID The ID of the stimulus type accepted by the receptor.
   */
  public
  ReceptorSpec
    (String       name,
     ReceptorMode mode,
     UUID         stimulusTypeID)
  {
    if (name == null)
    {
      throw new NeuPathsException("Parameter 'name' is required");
    }
    
    if (mode == null)
    {
      throw new NeuPathsException("Parameter 'mode' is required");
    }
    
    if (stimulusTypeID == null)
    {
      throw new NeuPathsException("Parameter 'stimulusTypeID' is required");
    }
    
    this.name = name;
    this.mode = mode;
    this.stimulusTypeID = stimulusTypeID;

    stimulusClassName = null;
  }

  /**
   * Copies a receptor specification.
   * 
   * @param r The source specification.
   */
  public
  ReceptorSpec (ReceptorSpec r)
  {
    name = r.name;
    mode = r.mode;
    stimulusTypeID = r.stimulusTypeID;
    stimulusClassName = r.stimulusClassName;
  }
  
  /**
   * Sets the receptor name.  Used by tools that deploy NeuPaths cells.
   * 
   * @param name The receptor name.
   */
  public
  void
  setName (String name)
  {
    this.name = name;
  }
  
  /**
   * Returns the receptor name.
   * 
   * @return The receptor name.
   */
  public
  String
  getName ()
  {
    return name;
  }

  /**
   * Sets the receptor mode.  Used by tools that deploy NeuPaths cells.
   * 
   * @param mode The receptor mode.
   */
  public
  void
  setMode (ReceptorMode mode)
  {
    this.mode = mode;
  }
  
  /**
   * Returns the receptor mode.
   * 
   * @return The receptor mode.
   */
  public
  ReceptorMode
  getMode ()
  {
    return mode;
  }
  
  /**
   * Indicates if the receptor is buffered.
   * 
   * @return {@code true} if buffered, {@code false} otherwise.
   */
  public
  boolean
  isBuffered ()
  {
    return (mode == ReceptorMode.BUFFERED);
  }

  /**
   * Sets the stimulus type ID.  Used by tools that deploy NeuPaths cells.
   * 
   * @param stimulusTypeID The ID of the stimulus type accepted by the receptor.
   */
  public
  void
  setStimulusTypeID (UUID stimulusTypeID)
  {
    this.stimulusTypeID = stimulusTypeID;
  }

  /**
   * Returns the stimulus type ID accepted by the receptor.
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

    final ReceptorSpec other = (ReceptorSpec) obj;

    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
    {
      return false;
    }

    if ((this.mode == null) ? (other.mode != null) : !this.mode.equals(other.mode))
    {
      return false;
    }

    if ((this.stimulusTypeID == null) ?
        (other.stimulusTypeID != null) : !this.stimulusTypeID.equals(other.stimulusTypeID))
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
    String concat = name + mode + stimulusTypeID + stimulusClassName;
    return concat.hashCode();
  }

  @Override
  public
  String
  toString ()
  {
    String image = "[" +
                   name + "/" +
                   mode + "/" +
                   stimulusTypeID + "/" +
                   stimulusClassName +
                   "]";

    return image;
  }

  private String       name;
  private ReceptorMode mode;
  private UUID         stimulusTypeID;
  private String       stimulusClassName;
}
