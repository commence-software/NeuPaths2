// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Thrown when a Nucleus operation (such as construction, starting or stopping)
 * fails.
 *
 * @author Aaron Caraveo
 * @see    Nuc_Nucleus
 */
final class Excp_Nucleus extends Excp_NeuPaths
{
  Excp_Nucleus ()
  {
    super("Nucleus operation failed");
  }

  Excp_Nucleus (String message)
  {
    super("Nucleus operation failed:\n" + message);
  }

  Excp_Nucleus (Throwable cause)
  {
    super("Nucleus operation failed", cause);
  }

  Excp_Nucleus (String message, Throwable cause)
  {
    super("Nucleus operation failed:\n" + message, cause);
  }
  
  Excp_Nucleus
    (String cellName,
     UUID   cellInstanceID,
     String message)
  {
    super("Nucleus for " + cellName + " (" + cellInstanceID + ") operation failed:\n" + message);
  }

  Excp_Nucleus
    (String    cellName,
     UUID      cellInstanceID,
     String    message,
     Throwable cause)
  {
    super("Nucleus for " + cellName + " (" + cellInstanceID + ") operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = -1711880287391749369L;
}
