// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Thrown when a Cell operation (such as construction, starting or stopping)
 * fails.
 * 
 * @author Aaron Caraveo
 * @see    Cell
 */
final class Excp_Cell extends Excp_NeuPaths
{
  Excp_Cell ()
  {
    super("Cell operation failed");
  }

  Excp_Cell (String message)
  {
    super("Cell operation failed:\n" + message);
  }

  Excp_Cell (Throwable cause)
  {
    super("Cell operation failed", cause);
  }

  Excp_Cell (String name, String message)
  {
    super("Cell " + name + " operation failed:\n" + message);
  }

  Excp_Cell
    (String    name,
     String    message,
     Throwable cause)
  {
    super("Cell " + name + " operation failed:\n" + message, cause);
  }

  static final long serialVersionUID = 1744439516734209717L;
}
