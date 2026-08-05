// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus used by {@link LoadControllerCell}s to indicate when a
 * {@link LoadBalancedCell} has registered.
 *
 * @author Aaron Caraveo
 */
class Stim_LoadBalanceRegistration extends Stimulus
{
  public Stim_LoadBalanceRegistration (UUID cellInstanceID)
  {
    super(TYPE_NAME, TYPE_ID);
    this.cellInstanceID = cellInstanceID;
  }

  protected Stim_LoadBalanceRegistration (String typeName, UUID cellInstanceID)
  {
    super(typeName, TYPE_ID);
    this.cellInstanceID = cellInstanceID;
  }

  public String toString()
  {
    return TYPE_NAME + "[" + cellInstanceID + "]";
  }

  UUID cellInstanceID;
  
  public static final String TYPE_NAME = "LoadBalanceRegistration";
  public static final UUID TYPE_ID = UUID.fromString("00e09786-d390-41a1-8a81-5b6530be0fba");

  private static final long serialVersionUID = -6910904464927227051L;
}
