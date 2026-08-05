// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Maintains information about a transaction.
 *
 * @author Aaron Caraveo
 */
final class Rx_TransactionInfo
{
  Rx_TransactionInfo ()
  {
    transactionID = null;
    originatorID = null;
    stimulusID = null;
    responseTransactionID = null;
    isLocal = false;
    transaction = new Rx_Transaction(null);
  }
  
  Rx_TransactionInfo
    (UUID            transactionID,
     UUID            originatorID,
     UUID            stimulusID,
     UUID            responseTransactionID,
     boolean         local,
     ReceptorSpecSet receptors)
    throws Excp_Receptor
  {
    this.transactionID = transactionID;
    this.originatorID = originatorID;
    this.stimulusID = stimulusID;
    this.responseTransactionID = responseTransactionID;
    isLocal = local;

    transaction = new Rx_Transaction(transactionID, receptors);
  }

  UUID transactionID;
  UUID originatorID;
  UUID stimulusID;
  UUID responseTransactionID;
  boolean isLocal;
  Rx_Transaction transaction;
}
