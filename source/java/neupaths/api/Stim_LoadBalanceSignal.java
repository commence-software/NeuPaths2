// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus used to indicate when a {@link LoadBalancedActivator} should
 * process its stimuli.
 *
 * @author Aaron Caraveo
 */
class Stim_LoadBalanceSignal extends Stimulus
{
  Stim_LoadBalanceSignal (UUID cellInstanceID)
  {
    super(TYPE_NAME, TYPE_ID);
    this.cellInstanceID = cellInstanceID;
  }

  protected Stim_LoadBalanceSignal (String typeName, UUID cellInstanceID)
  {
    super(typeName, TYPE_ID);
    this.cellInstanceID = cellInstanceID;
  }

  public String toString()
  {
    return TYPE_NAME + "[" + cellInstanceID + "]";
  }

  UUID cellInstanceID; 

  public static final String TYPE_NAME = "LoadBalanceSignal";
  public static final UUID TYPE_ID = UUID.fromString("29263391-d9d8-4fbd-b3d2-3085f1e3a999");

  private static final long serialVersionUID = -2559230381460439668L;
}
