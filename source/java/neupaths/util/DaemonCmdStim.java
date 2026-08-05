// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import neupaths.api.*;

import java.util.UUID;

/**
 * Stimulus used to issue commands to a {@link CellClusterDaemon}.
 *
 * @author Aaron Caraveo
 */
final class DaemonCmdStim extends Stimulus
{
  DaemonCmdStim (DaemonCmdType cmdType,
                 String        daemonName)
  {
    super(TYPE_NAME, TYPE_ID);

    type = cmdType;

    this.daemonName = daemonName;
    this.clusterName = "NA";
    this.cellName = "NA";
    this.params = "NA";
  }
  
  DaemonCmdStim (DaemonCmdType cmdType,
                 String        daemonName,
                 String        clusterName,
                 String        cellName,
                 String        params)
  {
    super(TYPE_NAME, TYPE_ID);

    type = cmdType;

    this.daemonName = daemonName;
    this.clusterName = clusterName;
    this.cellName = cellName;
    this.params = params;
  }
  
  protected DaemonCmdStim (String        typeName,
                           DaemonCmdType cmdType,
                           String        daemonName,
                           String        clusterName,
                           String        cellName,
                           String        params)
  {
    super(typeName, TYPE_ID);

    type = cmdType;

    this.daemonName = daemonName;
    this.clusterName = clusterName;
    this.cellName = cellName;
    this.params = params;
  }

  public String toString ()
  {
    return type.toString() + "/" + daemonName + "/" + clusterName + "/" + cellName + "/" + params;
  }
  
  DaemonCmdType type;
  String daemonName;
  String clusterName;
  String cellName; // can have reg expr
  String params;

  static final String TYPE_NAME = "DaemonCmdStim";
  static final UUID TYPE_ID = UUID.fromString("faae4e70-1080-4c8e-a28d-d24ce7720d84");

  static final long serialVersionUID = -840639656460381872L;
}
