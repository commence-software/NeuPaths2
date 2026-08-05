// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * A null stimulus.
 *
 * @author Aaron Caraveo
 */
final class Stim_Null extends Stimulus
{
  Stim_Null ()
  {
    super(TYPE_NAME, TYPE_ID);
  }

  // Creates a NullStimulus alias for another stimulus type
  Stim_Null (UUID stimulusTypeID)
  {
    super(TYPE_NAME, stimulusTypeID);
  }
  
  public static final String TYPE_NAME = "NullStimulus";
  public static final UUID TYPE_ID = UUID.fromString("a5369068-24a7-4c4d-8150-736338e462ae");

  static final long serialVersionUID = 3911757760989272769L;
}
