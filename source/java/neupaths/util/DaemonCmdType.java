// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

/**
 * Command types supported by the {@link CellClusterDaemon}
 *
 * @author Aaron Caraveo
 */
enum DaemonCmdType
{
  DISCOVER_DAEMON,
  DISCOVER_CLUSTER,
  DISCOVER_CELL,
  QUERY,
  DEPLOY_CLUSTER,
  START_CLUSTER,
  PAUSE_CLUSTER,
  RESUME_CLUSTER,
  STOP_CLUSTER,
  PUBLISH_CLUSTER,
  RECALL_CLUSTER,
  START_CELL,
  PAUSE_CELL,
  RESUME_CELL,
  STOP_CELL,
  PUBLISH_CELL,
  ENABLE_CLUSTER_LOGGING,
  DISABLE_CLUSTER_LOGGING,
  ENABLE_CLUSTER_TRACE,
  DISABLE_CLUSTER_TRACE,
  ENABLE_CLUSTER_DEBUG,
  DISABLE_CLUSTER_DEBUG,
  ENABLE_CLUSTER_RUNTIME,
  DISABLE_CLUSTER_RUNTIME,
  ENABLE_CELL_LOGGING,
  DISABLE_CELL_LOGGING,
  ENABLE_CELL_TRACE,
  DISABLE_CELL_TRACE,
  ENABLE_CELL_DEBUG,
  DISABLE_CELL_DEBUG,
  ENABLE_CELL_RUNTIME,
  DISABLE_CELL_RUNTIME;
}
