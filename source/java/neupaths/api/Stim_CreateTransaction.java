// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus type used internally by {@link Nuc_Nucleus} to create
 * a transaction.
 *
 * @author Aaron Caraveo
 */
final class Stim_CreateTransaction extends Stimulus
{
  Stim_CreateTransaction (UUID transactionID,
                          UUID stimulusID,
                          UUID responseTransactionID)
  {
    super(TYPE_NAME, TYPE_ID);
    this.transactionID = transactionID;
    this.stimulusID = stimulusID;
    this.responseTransactionID = responseTransactionID;
  }

  @Override
  public String toString ()
  {
    return TYPE_NAME + "[transactionID=" + transactionID +
           ", stimulusID=" + stimulusID +
           ", responseTransactionID=" + responseTransactionID + "]";
  }
  
  UUID transactionID;
  UUID stimulusID;
  UUID responseTransactionID;

  public static final String TYPE_NAME = "CreateTransStimulus";
  public static final UUID TYPE_ID = UUID.fromString("02fd1e29-c888-496e-b4a7-552321a0a9e7");
  
  static final long serialVersionUID = -592272005018250038L;
}
