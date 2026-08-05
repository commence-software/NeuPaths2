// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when an operation on a Synapse fails.  This class is the base
 * class for all exceptions related to Synapse failures.
 * 
 * @author Aaron Caraveo
 */
class Excp_Synapse extends Excp_NeuPaths
{
  Excp_Synapse ()
  {
    super("Synapse operation failed");
  }

  Excp_Synapse (String message)
  {
    super("Synapse operation failed:\n" + message);
  }
  
  Excp_Synapse (Throwable cause)
  {
    super("Synapse operation failed", cause);
  }

  Excp_Synapse (String message, Throwable cause)
  {
    super("Synapse operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = -6069881060379438450L;
}
