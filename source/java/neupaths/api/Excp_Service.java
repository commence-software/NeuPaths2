// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when a service request fails.
 *
 * @author Aaron Caraveo
 */
final class Excp_Service extends Excp_NeuPaths
{
  Excp_Service ()
  {
    super("Service operation failed");
  }

  Excp_Service (String message)
  {
    super("Service operation failed:\n" + message);
  }

  Excp_Service (Throwable cause)
  {
    super("Service operation failed", cause);
  }

  Excp_Service (String name, String message)
  {
    super("Service '" + name + "' operation failed:\n" + message);
  }

  Excp_Service
    (String    name,
     String    message,
     Throwable cause)
  {
    super("Service '" + name + "' operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = 6056473114787330955L;
}
