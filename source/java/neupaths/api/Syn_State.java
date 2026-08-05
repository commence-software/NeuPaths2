// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Synapse internal states.
 *
 * @author Aaron Caraveo
 */
enum Syn_State
{
  UNINITIALIZED,
  INITIALIZED,
  OPEN,
  LISTENING,
  CONNECTED,
  CLOSED,
  RECLAIM
}
