// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code double} value.
 *
 * @author Aaron Caraveo
 */
public class DoubleStimulus extends Stimulus
{
  /**
   * Allocates a new {@code DoubleStimulus} object with the specified
   * double value.
   * 
   * @param d The initial value of the stimulus.
   */
  public
  DoubleStimulus (double d)
  {
    super(TYPE_NAME, TYPE_ID);

    value = d;
  }

  /**
   * Allocates a new {@code DoubleStimulus} object with the specified
   * type name and double value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param d        The initial value of this stimulus.
   */
  protected
  DoubleStimulus (String typeName, double d)
  {
    super(typeName, TYPE_ID);

    value = d;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  double
  get ()
  {
    return value;
  }

  /**
   * Sets the stimulus value.
   * 
   * @param d The stimulus value.
   */
  public
  void
  set (double d)
  {
    value = d;
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

    final DoubleStimulus other = (DoubleStimulus) obj;

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
    return Double.toString(value);
  }

  private double value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "DoubleStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("51c275ad-417e-4dcf-8c94-ab33c5cfc2c2");

  static final long serialVersionUID = -41117516400912230L;
}
