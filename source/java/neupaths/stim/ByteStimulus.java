// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@code byte} value.
 * 
 * @author Aaron Caraveo
 */
public class ByteStimulus extends Stimulus
{
  /**
   * Allocates a new {@code ByteStimulus} object with the specified
   * byte value.
   * 
   * @param b The initial value of the stimulus.
   */
  public
  ByteStimulus (byte b)
  {
    super(TYPE_NAME, TYPE_ID);

    value = b;
  }

  /**
   * Allocates a new {@code ByteStimulus} object with the specified
   * type name and byte value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param b        The initial value of this stimulus.
   */
  protected
  ByteStimulus (String typeName, byte b)
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
  byte
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
  set (byte b)
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

    final ByteStimulus other = (ByteStimulus) obj;

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
    return Byte.toString(value);
  }

  private byte value;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "ByteStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("57b0aa1c-91a7-47ae-8795-4055a49c88ac");

  static final long serialVersionUID = -7682063241375287548L;
}
