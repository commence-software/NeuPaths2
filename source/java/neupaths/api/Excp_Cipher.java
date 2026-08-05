// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when a Synapse fails to encrypt/decrypt data.
 *
 * @author Aaron Caraveo
 */
final class Excp_Cipher extends Excp_NeuPaths
{  
  Excp_Cipher ()
  {
    super("Cipher operation failed");
  }

  Excp_Cipher (String message)
  {
    super("Cipher operation failed:\n" + message);
  }
  
  Excp_Cipher (Throwable cause)
  {
    super("Cipher operation failed", cause);
  }

  Excp_Cipher (String message, Throwable cause)
  {
    super("Cipher operation failed:\n" + message, cause);
  }
  
  static final long serialVersionUID = 4878168567355462687L;
}
