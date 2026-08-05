// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when an operation on a Receptor fails.
 * 
 * @author Aaron Caraveo
 * @see    Rx_Collection
 * @see    Rx_Transaction
 */
final class Excp_Receptor extends Excp_NeuPaths
{
  Excp_Receptor ()
  {
    super("Receptor operation failed");
  }

  Excp_Receptor (String message)
  {
    super("Receptor operation failed:\n" + message);
  }
  
  Excp_Receptor (Throwable cause)
  {
    super("Receptor operation failed", cause);
  }

  Excp_Receptor (String name, String message)
  {
    super("Receptor '" + name + "' operation failed:\n" + message);
  }

  Excp_Receptor (String name, String message, Throwable cause)
  {
    super("Receptor '" + name + "' operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = -1316208741107546755L;
}
