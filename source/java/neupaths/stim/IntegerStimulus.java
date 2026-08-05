// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates an {@code int} value.
 *
 * @author Aaron Caraveo
 */
public class IntegerStimulus extends Stimulus
{
  /**
   * Allocates a new {@code IntegerStimulus} object with the specified
   * integer value.
   * 
   * @param i The initial value of the stimulus.
   */
  public
  IntegerStimulus (int i)
  {
    super(TYPE_NAME, TYPE_ID);

    value = i;
  }

  /**
   * Allocates a new {@code IntegerStimulus} object with the specified
   * type name and integer value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param i        The initial value of this stimulus.
   */
  protected
  IntegerStimulus (String typeName, int i)
  {
    super(typeName, TYPE_ID);

    value = i;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  int
  get ()
  {
    return value;
  }

  /**
   * Sets the stimulus value.
   * 
   * @param i The stimulus value.
   */
  public
  void
  set (int i)
  {
    value = i;
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

    final IntegerStimulus other = (IntegerStimulus) obj;

    return value == other.get();
  }

  @Override
  public
  int
  hashCode()
  {
    return value;
  }

  @Override
  public
  String
  toString()
  {
    return Integer.toString(value);
  }

  private int value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "IntegerStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("706a8311-95b2-499a-938f-cb9e383a32df");

  static final long serialVersionUID = -7633151746586357800L;
}
