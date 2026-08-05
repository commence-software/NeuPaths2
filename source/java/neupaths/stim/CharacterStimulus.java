// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code char} value.
 *
 * @author Aaron Caraveo
 */
public class CharacterStimulus extends Stimulus
{
  /**
   * Allocates a new {@code CharacterStimulus} object with the specified
   * character value.
   * 
   * @param c The initial value of the stimulus.
   */
  public
  CharacterStimulus (char c)
  {
    super(TYPE_NAME, TYPE_ID);

    value = c;
  }

  /**
   * Allocates a new {@code CharacterStimulus} object with the specified
   * type name and character value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param c        The initial value of this stimulus.
   */
  protected
  CharacterStimulus (String typeName, char c)
  {
    super(typeName, TYPE_ID);

    value = c;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  char
  get ()
  {
    return value;
  }

  /**
   * Sets the stimulus value.
   * 
   * @param c The stimulus value.
   */
  public
  void
  set (char c)
  {
    value = c;
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

    final CharacterStimulus other = (CharacterStimulus) obj;

    return value == other.get();
  }

  @Override
  public
  int
  hashCode()
  {
    return (int)value;
  }

  @Override
  public
  String
  toString()
  {
    return Character.toString(value);
  }

  private char value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "CharacterStatus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("a88a3d1f-7c98-45c6-8825-91005bea5f6a");

  static final long serialVersionUID = 1410226421559104023L;
}
