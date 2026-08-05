// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code boolean} value.
 * 
 * @author Aaron Caraveo
 */
public class BooleanStimulus extends Stimulus
{
  /**
   * Allocates a new {@code BooleanStimulus} object with the specified
   * boolean value.
   * 
   * @param b The initial value of the stimulus.
   */
  public
  BooleanStimulus (boolean b)
  {
    super(TYPE_NAME, TYPE_ID);

    value = b;    
  }
  
  /**
   * Allocates a new {@code BooleanStimulus} object with the specified
   * type name and boolean value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param b        The initial value of this stimulus.
   */
  protected
  BooleanStimulus (String typeName, boolean b)
  {
    super(typeName, TYPE_ID);

    value = b;
  }

  /**
   * Retrieves the stimulus value.
   * 
   * @return The stimulus value.
   */
  public
  boolean
  get ()
  {
    return value;
  }
  
  /**
   * Sets the stimulus value.
   * 
   * @param b The stimulus value.
   */
  public
  void
  set (boolean b)
  {
    value = b;
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

    final BooleanStimulus other = (BooleanStimulus) obj;

    return value == other.get();
  }

  @Override
  public
  int
  hashCode()
  {
    int hash = 0;

    if (value)
      hash = 1;

    return hash;
  }

  @Override
  public
  String
  toString()
  {
    return Boolean.toString(value);
  }

  private boolean value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "BooleanStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("9c24b15c-4cfe-4a38-bc05-2152e8230a10");

  static final long serialVersionUID = 1675801684142253065L;
}
