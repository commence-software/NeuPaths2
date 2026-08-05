// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code float} value.
 *
 * @author Aaron Caraveo
 */
public class FloatStimulus extends Stimulus
{
  /**
   * Allocates a new {@code FloatStimulus} object with the specified
   * float value.
   * 
   * @param f The initial value of the stimulus.
   */
  public
  FloatStimulus (float f)
  {
    super(TYPE_NAME, TYPE_ID);

    value = f;
  }

  /**
   * Allocates a new {@code FloatStimulus} object with the specified
   * type name and float value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param f        The initial value of this stimulus.
   */
  protected
  FloatStimulus (String typeName, float f)
  {
    super(typeName, TYPE_ID);

    value = f;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  float
  get ()
  {
    return value;
  }

  /**
   * Sets the stimulus value.
   * 
   * @param f The stimulus value.
   */
  public
  void
  set (float f)
  {
    value = f;
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

    final FloatStimulus other = (FloatStimulus) obj;

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
    return Float.toString(value);
  }

  private float value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "FloatStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("2e236609-a372-410b-a4a9-4ec65d3283fc");

  static final long serialVersionUID = 7713793738194182017L;
}
