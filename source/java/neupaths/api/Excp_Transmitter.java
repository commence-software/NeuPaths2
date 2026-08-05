// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when an operation on a Transmitter fails.
 *
 * @author Aaron Caraveo
 * @see    Tx_Collection
 */
final class Excp_Transmitter extends Excp_NeuPaths
{
  Excp_Transmitter ()
  {
    super("Transmitter operation failed");
  }

  Excp_Transmitter (String message)
  {
    super("Transmitter operation failed:\n" + message);
  }
  
  Excp_Transmitter (Throwable cause)
  {
    super("Transmitter operation failed", cause);
  }

  Excp_Transmitter (String name, String message)
  {
    super("Transmitter '" + name + "' operation failed:\n" + message);
  }

  Excp_Transmitter (String name, String message, Throwable cause)
  {
    super("Transmitter '" + name + "' operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = 5487244604617179398L;
}
