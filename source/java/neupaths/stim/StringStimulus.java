// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a java.lang.String value.
 *
 * @author Aaron Caraveo
 */
public class StringStimulus extends Stimulus
{
  /**
   * Allocates a new {@code StringStimulus} object with the specified
   * string value.
   * 
   * @param s The initial value of the stimulus.
   */
  public
  StringStimulus (String s)
  {
    super(TYPE_NAME, TYPE_ID);
    
    value = s;
  }

  /**
   * Allocates a new {@code StringStimulus} object with the specified
   * type name and string value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param s        The initial value of this stimulus.
   */
  protected
  StringStimulus (String typeName, String s)
  {
    super(typeName, TYPE_ID);

    value = s;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  String
  get ()
  {
    return value;
  }

  /**
   * Sets the stimulus value.
   * 
   * @param s The stimulus value.
   */
  public
  void
  set (String s)
  {
    value = s;
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

    final StringStimulus other = (StringStimulus) obj;

    if ((value == null) ? (other.get() != null) : !value.equals(other.get()))
    {
      return false;
    }

    return true;
  }

  @Override
  public
  int
  hashCode()
  {
    int hash = 7;

    if (value != null)
      hash = value.hashCode();

    return hash;
  }

  @Override
  public
  String
  toString()
  {
    return value;
  }

  private String value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "StringStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("3d55c259-acc4-44ae-b98b-374e2d071a0b");

  static final long serialVersionUID = 7327026221171210040L;
}
