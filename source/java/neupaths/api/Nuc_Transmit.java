// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * {@link Nuc_Nucleus} transmit queue element.
 *
 * @author Aaron Caraveo
 */
final class Nuc_Transmit
{
  Nuc_Transmit (UUID synapseInstanceID, Msg_NeuPaths msg)
  {
    this.synapseInstanceID = synapseInstanceID;
    this.msg = msg;
  }
  
  public UUID synapseInstanceID;
  public Msg_NeuPaths msg;
}
