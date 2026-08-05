// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus used by {@link LoadControllerCell}s to convey the transaction
 * of the work being load-balanced.
 *
 * @author Aaron Caraveo
 */
class Stim_LoadBalanceTransaction extends Stimulus
{
  public Stim_LoadBalanceTransaction (UUID transactionID)
  {
    super(TYPE_NAME, TYPE_ID);
    this.transactionID = transactionID;
  }

  protected Stim_LoadBalanceTransaction (String typeName, UUID transactionID)
  {
    super(typeName, TYPE_ID);
    this.transactionID = transactionID;
  }

  public String toString()
  {
    return TYPE_NAME + "[" + transactionID + "]";
  }

  UUID transactionID;
  
  public static final String TYPE_NAME = "LoadBalanceTransaction";
  public static final UUID TYPE_ID = UUID.fromString("ab3f7b5e-f07e-4b27-917a-b5cbcedf3698");

  private static final long serialVersionUID = -1944749260992526872L;
}
