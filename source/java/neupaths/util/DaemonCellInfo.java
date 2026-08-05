// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import neupaths.api.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Maintains information about a Cell running inside a {@link CellClusterDaemon}.
 *
 * @author Aaron Caraveo
 */
class DaemonCellInfo implements Serializable
{
  DaemonCellInfo (String    daemonName,
                  UUID      daemonID,
                  String    daemonVersion,
                  String    clusterName,
                  UUID      clusterID,
                  String    cellName,
                  UUID      cellID,
                  CellType  cellType,
                  CellState cellState,
                  boolean   loggingEnabled,
                  boolean   traceLoggingEnabled,
                  boolean   debugLoggingEnabled,
                  boolean   runtimeLoggingEnabled)
  {
    this.daemonName = daemonName;
    this.daemonID = daemonID;
    this.daemonVersion = daemonVersion;
    this.clusterName = clusterName;
    this.clusterID = clusterID;
    this.cellName = cellName;
    this.cellID = cellID;
    this.cellType = cellType;
    this.cellState = cellState;
    this.loggingEnabled = loggingEnabled;
    this.traceLoggingEnabled = traceLoggingEnabled;
    this.debugLoggingEnabled = debugLoggingEnabled;
    this.runtimeLoggingEnabled = runtimeLoggingEnabled;
  }

  String    daemonName;
  UUID      daemonID;
  String    daemonVersion;
  String    clusterName;
  UUID      clusterID;
  String    cellName;
  UUID      cellID;
  CellType  cellType;
  CellState cellState;
  boolean   loggingEnabled;
  boolean   traceLoggingEnabled;
  boolean   debugLoggingEnabled;
  boolean   runtimeLoggingEnabled;

  static final long serialVersionUID = -731868584117516605L;
}
