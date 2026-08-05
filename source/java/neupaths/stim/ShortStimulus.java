// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code short} value.
 *
 * @author Aaron Caraveo
 */
public class ShortStimulus extends Stimulus
{
  /**
   * Allocates a new {@code ShortStimulus} object with the specified
   * short integer value.
   * 
   * @param s The initial value of the stimulus.
   */
  public
  ShortStimulus (short s)
  {
    super(TYPE_NAME, TYPE_ID);

    value = s;
  }

  /**
   * Allocates a new {@code ShortStimulus} object with the specified
   * type name and short integer value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param s        The initial value of this stimulus.
   */
  protected
  ShortStimulus (String typeName, short s)
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
  short
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
  set (short s)
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

    final ShortStimulus other = (ShortStimulus) obj;

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
    return Short.toString(value);
  }

  private short value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "ShortStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("f8aa02a6-81e5-47e4-808a-61c13eca7ed3");

  static final long serialVersionUID = -6549861727892227465L;
}
