// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code long} value.
 *
 * @author Aaron Caraveo
 */
public class LongStimulus extends Stimulus
{
  /**
   * Allocates a new {@code LongStimulus} object with the specified
   * long integer value.
   * 
   * @param l The initial value of the stimulus.
   */
  public
  LongStimulus (long l)
  {
    super(TYPE_NAME, TYPE_ID);

    value = l;
  }

  /**
   * Allocates a new {@code LongStimulus} object with the specified
   * type name and long integer value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param l        The initial value of this stimulus.
   */
  protected
  LongStimulus (String typeName, long l)
  {
    super(typeName, TYPE_ID);

    value = l;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  long
  get ()
  {
    return value;
  }

  /**
   * Sets the stimulus value.
   * 
   * @param l The stimulus value.
   */
  public
  void
  set (long l)
  {
    value = l;
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

    final LongStimulus other = (LongStimulus) obj;

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
    return Long.toString(value);
  }

  private long value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "LongStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("8590e985-9f91-4d70-b6e0-71a727f75511");

  static final long serialVersionUID = -2843123580165896946L;
}
