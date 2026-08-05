// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Thrown when a Binder operation (such as construction, starting or stopping)
 * fails.
 *
 * @author Aaron Caraveo
 * @see    Bnd_Binder
 */
final class Excp_Binder extends Excp_NeuPaths
{
  Excp_Binder ()
  {
    super("Binder operation failed");
  }

  Excp_Binder (String message)
  {
    super("Binder operation failed:\n" + message);
  }

  Excp_Binder (Throwable cause)
  {
    super("Binder operation failed", cause);
  }

  Excp_Binder (String message, Throwable cause)
  {
    super("Binder operation failed:\n" + message, cause);
  }
  
  Excp_Binder
    (String  cellName,
     UUID    cellInstanceID,
     String  message)
  {
    super("Binder for " + cellName + " (" + cellInstanceID + ") operation failed:\n" + message);
  }

  Excp_Binder
    (String   cellName,
     UUID     cellInstanceID,
     Syn_Name synName,
     String   message)
  {
    super("Binder for " + cellName + " (" + cellInstanceID + ") on synapse " +
          synName + " operation failed:\n" + message);
  }

  Excp_Binder
    (String    cellName,
     UUID      cellInstanceID,
     Syn_Name  synName,
     String    message,
     Throwable cause)
  {
    super("Binder for " + cellName + " (" + cellInstanceID + ") on synapse " +
          synName + " operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = -1711880287391749369L;
}
