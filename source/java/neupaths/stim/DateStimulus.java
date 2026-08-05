// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import neupaths.api.Stimulus;
import java.util.Date;
import java.util.UUID;

/**
 * A NeuPaths stimulus type that encapsulates a {@link java.util.Date} value.
 * The string representation uses the format:
 * dow mon dd hh:mm:ss zzz yyyy (e.g. Wed Jan 29 18:23:44 EST 2025)
 *
 * @author Aaron Caraveo
 */
public class DateStimulus extends Stimulus
{
  /**
   * Allocates a new {@code DateStimulus} object with the current
   * date/time.
   */
  public
  DateStimulus ()
  {
    super(TYPE_NAME, TYPE_ID);

    value = new Date();
  }
  
  /**
   * Allocates a new {@code DateStimulus} object with the specified
   * date/time value.
   * 
   * @param d The initial value of the stimulus.
   */
  public
  DateStimulus (Date d)
  {
    super(TYPE_NAME, TYPE_ID);

    value = d;
  }
  
  /**
   * Allocates a new {@code DateStimulus} object with the specified
   * type name and current date/time.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   */
  protected
  DateStimulus (String typeName)
  {
    super(typeName, TYPE_ID);
    
    value = new Date();
  }
  
  /**
   * Allocates a new {@code DateStimulus} object with the specified
   * type name and date/time value.  This constructor can be used to create
   * aliases of this stimulus type.  The alias will extend this class,
   * thereby using the same TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param d        The initial value of this stimulus.
   */
  protected
  DateStimulus (String typeName, Date d)
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
  Date
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
  set (Date d)
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

    final DateStimulus other = (DateStimulus) obj;

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
    return value.toString();
  }

  private Date value;
  
  /**
   * The stimulus type name.  Used in event recordings.
   */
  public static final String TYPE_NAME = "DateStimulus";

  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  public static final UUID TYPE_ID = UUID.fromString("fa75a7fe-644c-4c9d-bcb6-ab3c4407e343");

  static final long serialVersionUID = 2085244958622361315L;
}
