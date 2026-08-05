// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when a non-fatal condition occurs on a Synapse.
 *
 * @author Aaron Caraveo
 */
final class Excp_SynapseNonFatal extends Excp_Synapse
{
  Excp_SynapseNonFatal ()
  {
    super("Synapse operation failed [Non-Fatal]");
  }

  Excp_SynapseNonFatal (String message)
  {
    super("[Non-Fatal] " + message);
  }
  
  Excp_SynapseNonFatal (Throwable cause)
  {
    super("[Non-Fatal]", cause);
  }

  Excp_SynapseNonFatal (String message, Throwable cause)
  {
    super("[Non-Fatal] " + message, cause);
  }
  
  static final long serialVersionUID = -339826330773029972L;
}
