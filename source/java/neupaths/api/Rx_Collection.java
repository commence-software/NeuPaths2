// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashMap;
import java.util.UUID;

/**
 * A receptor collection.  Technically a collection of transactions, where
 * each transaction is a collection of receptors.
 *
 * @author Aaron Caraveo
 */
final class Rx_Collection
{
  Rx_Collection ()
  {
    transactions = new HashMap<>();

    // Add the NULL (i.e. global) transaction
    transactions.put(null, new Rx_TransactionInfo());
  }
  
  Rx_Collection (ReceptorSpecSet receptors)
    throws Excp_Receptor
  {
    if (receptors == null)
    {
      throw new Excp_Receptor("Parameter 'receptors' is required");
    }

    transactions = new HashMap<>();

    // Add the NULL (i.e. global) transaction
    transactions.put(null,
                     new Rx_TransactionInfo(null, null, null, null, false, receptors));
  }
  
  synchronized
  void
  addReceptors (ReceptorSpecSet receptors)
    throws Excp_Receptor
  {
    if (receptors == null)
    {
      throw new Excp_Receptor("Parameter 'receptors' is required");
    }

    transactions.get(null).transaction.addReceptors(receptors);
  }

  synchronized
  Rx_Transaction
  addTransaction
    (UUID            transactionID,
     ReceptorSpecSet receptors,
     UUID            originatorID,
     UUID            stimulusID,
     UUID            responseTransactionID,
     boolean         local)
    throws Excp_Receptor
  {
    if (transactionID == null)
    {
      throw new Excp_Receptor("Parameter 'transactionID' is required");
    }

    if (receptors == null)
    {
      throw new Excp_Receptor("Parameter 'receptors' is required");
    }

    Rx_TransactionInfo tinfo =
        new Rx_TransactionInfo(transactionID,
                               originatorID,
                               stimulusID,
                               responseTransactionID,
                               local,
                               receptors);
    
    transactions.put(transactionID, tinfo);
    
    return tinfo.transaction;
  }

  synchronized
  Rx_Transaction
  removeTransaction (UUID transactionID)
  {
    Rx_TransactionInfo tinfo = null;
    Rx_Transaction transaction = null;
    
    if (transactionID != null)
    {
      tinfo = transactions.remove(transactionID);
    }
    
    if (tinfo != null)
    {
      transaction = tinfo.transaction;
    }
    
    return transaction;
  }
  
  synchronized
  Rx_TransactionInfo
  getTransactionInfo (UUID transactionID)
  {
    // Note: null is a valid transactionID.  It is considered the global
    //       transaction

    return transactions.get(transactionID);
  }
  
  synchronized
  Rx_Transaction
  getTransaction (UUID transactionID)
  {
    // Note: null is a valid transactionID.  It is considered the global
    //       transaction

    Rx_TransactionInfo tinfo = getTransactionInfo(transactionID);
    Rx_Transaction transaction = null;
    
    if (tinfo != null)
    {
      transaction = tinfo.transaction;
    }
    
    return transaction;
  }
  
  synchronized
  void
  clear ()
  {
    for (Rx_TransactionInfo t : transactions.values())
    {
      t.transaction.clear();
    }
  }
  
  private HashMap<UUID, Rx_TransactionInfo> transactions;
}
