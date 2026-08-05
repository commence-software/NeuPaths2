// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus used by {@link LoadBalancedActivator}s to register with a
 * {@link LoadControllerCell}.
 *
 * @author Aaron Caraveo
 */
class Stim_LoadBalanceRequest extends Stimulus
{
  public Stim_LoadBalanceRequest (UUID cellInstanceID)
  {
    super(TYPE_NAME, TYPE_ID);
    this.cellInstanceID = cellInstanceID;
  }

  protected Stim_LoadBalanceRequest (String typeName, UUID cellInstanceID)
  {
    super(typeName, TYPE_ID);
    this.cellInstanceID = cellInstanceID;
  }

  public String toString()
  {
    return TYPE_NAME + "[" + cellInstanceID + "]";
  }

  UUID cellInstanceID;
  
  public static final String TYPE_NAME = "LoadBalanceRequest";
  public static final UUID TYPE_ID = UUID.fromString("45a363a7-ad8b-4b5a-a867-fd1e724fbcd8");

  private static final long serialVersionUID = 560604734015276976L;
}
