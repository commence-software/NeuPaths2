// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when a fatal condition occurs on a Synapse.
 * 
 * @author Aaron Caraveo
 */
final class Excp_SynapseFatal extends Excp_Synapse
{
  Excp_SynapseFatal ()
  {
    super("Synapse operation failed [Fatal]");
  }

  Excp_SynapseFatal (String message)
  {
    super("[Fatal] " + message);
  }
  
  Excp_SynapseFatal (Throwable cause)
  {
    super("[Fatal]", cause);
  }

  Excp_SynapseFatal (String message, Throwable cause)
  {
    super("[Fatal] " + message, cause);
  }

  static final long serialVersionUID = 8643907033051936639L;
}
