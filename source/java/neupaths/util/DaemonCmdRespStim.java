// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.util.LinkedList;
import java.util.UUID;
import neupaths.api.Stimulus;

/**
 * Stimulus used for {@link CellClusterDaemon} command responses.
 *
 * @author Aaron Caraveo
 */
final class DaemonCmdRespStim extends Stimulus
{
  DaemonCmdRespStim ()
  {
    super(TYPE_NAME, TYPE_ID);
    succeeded = true;
    errorInfo = "";
    records = new LinkedList<>();
  }

  // Constructor to be used for creating aliases of this type.
  protected DaemonCmdRespStim (String typeName)
  {
    super(typeName, TYPE_ID);
    succeeded = true;
    errorInfo = "";
    records = new LinkedList<>();
  }

  public String toString()
  {
    return TYPE_NAME;
  }

  boolean succeeded;
  String errorInfo;
  LinkedList<DaemonCellInfo> records;

  static final String TYPE_NAME = "DaemonCmdRespStim";
  static final UUID TYPE_ID = UUID.fromString("bf215abb-6e89-4ddd-b19e-f4083a939a35");

  static final long serialVersionUID = 1967744865033701458L;
}
