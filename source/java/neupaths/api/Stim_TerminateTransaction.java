// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus type used internally by {@link Nuc_Nucleus} to terminate
 * a transaction.
 *
 * @author Aaron Caraveo
 */
final class Stim_TerminateTransaction extends Stimulus
{
  Stim_TerminateTransaction (UUID transactionID)
  {
    super(TYPE_NAME, TYPE_ID);
    this.transactionID = transactionID;
  }

  @Override
  public String toString ()
  {
    return TYPE_NAME + "[transactionID=" + transactionID + "]";
  }
  
  UUID transactionID;
  
  public static final String TYPE_NAME = "TerminateTransStimulus";
  public static final UUID TYPE_ID = UUID.fromString("1f5fc3ea-c3b4-4f52-a914-36cf388ade69");
  
  static final long serialVersionUID = -4477581303579632224L;
}
