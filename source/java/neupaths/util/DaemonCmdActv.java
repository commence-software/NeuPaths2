// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import neupaths.api.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The {@link neupaths.api.Activator Activator} responsible for processing
 * {@link CellClusterDaemon} commands.
 *
 * @author Aaron Caraveo
 */
class DaemonCmdActv extends Activator
{
  DaemonCmdActv ()
  {
    super("DaemonCmdActivator",
          new ReceptorSpec[] {
              new ReceptorSpec("Command",
                               ReceptorMode.BUFFERED,
                               DaemonCmdStim.TYPE_ID)
          },
          new TransmitterSpec[] {
              new TransmitterSpec(Daemon.COMMAND_RSP_TRANSMITTER,
                                  DaemonCmdRespStim.TYPE_ID)
          },
          new LogicSubscriptionSpec[] {
              new LogicSubscriptionSpec(".*",
                                        Daemon.COMMAND_REQ_TRANSMITTER,
                                        "Command",
                                        "Daemon:Ctrl",
                                        TransactionFilter.DISABLED)
          });

    clusters = new HashMap<>();
  }

  @Override
  protected void start ()
  {
    daemonName = getProperty("daemonName");
    daemonID = UUID.fromString(getProperty("daemonID"));
    configTopDir = getProperty("configTopDir");
  }

  @Override
  protected void evaluate ()
  {
    CellCluster cluster = null;
    DaemonCmdRespStim resp = null;

    DaemonCmdStim cmd = getStimulus("Command");

    if (cmd.type == DaemonCmdType.DISCOVER_DAEMON)
    {
      resp = new DaemonCmdRespStim();
      resp.records.addLast(
          new DaemonCellInfo(daemonName,
                             daemonID,
                             NeuPathsRuntime.VERSION,
                             "",
                             daemonID,
                             "",
                             daemonID,
                             CellType.BRIDGE,
                             CellState.ONLINE,
                             false,
                             false,
                             false,
                             false));

      setStimulus(Daemon.COMMAND_RSP_TRANSMITTER, resp, cmd.getTransactionID());
    }
    else if (daemonName.equals(cmd.daemonName))
    {
      switch (cmd.type)
      {
        case DISCOVER_CLUSTER:
          resp = new DaemonCmdRespStim();
    
          for (CellCluster clust : clusters.values())
          {
            resp.records.addLast(
                new DaemonCellInfo(daemonName,
                                   daemonID,
                                   NeuPathsRuntime.VERSION,
                                   clust.getName(),
                                   clust.getInstanceID(),
                                   "",
                                   clust.getInstanceID(),
                                   CellType.BRIDGE,
                                   CellState.ONLINE,
                                   false,
                                   false,
                                   false,
                                   false));
          }
    
          setStimulus(Daemon.COMMAND_RSP_TRANSMITTER, resp, cmd.getTransactionID());
          break;

        case DISCOVER_CELL:
          resp = new DaemonCmdRespStim();
          cluster = clusters.get(cmd.clusterName);

          if (cluster == null)
          {
            resp.succeeded = false;
            resp.errorInfo = "Cluster does not exist";
          }
          else
          {
            for (Cell cell : cluster)
            {
              resp.records.addLast(
                  new DaemonCellInfo(daemonName,
                                     daemonID,
                                     NeuPathsRuntime.VERSION,
                                     cluster.getName(),
                                     cluster.getInstanceID(),
                                     cell.getName(),
                                     cell.getInstanceID(),
                                     cell.getType(),
                                     cell.getState(),
                                     cell.isLoggingEnabled(),
                                     cell.isTraceLoggingEnabled(),
                                     cell.isDebugLoggingEnabled(),
                                     cell.isRuntimeLoggingEnabled()));
            }
          }

          setStimulus(Daemon.COMMAND_RSP_TRANSMITTER, resp, cmd.getTransactionID());
          break;

        case QUERY:
          break;

        case DEPLOY_CLUSTER:
          resp = new DaemonCmdRespStim();

          try
          {
            cluster = new CellCluster(configTopDir + File.separator + cmd.params);
            cluster.start();
            clusters.put(cluster.getName(), cluster);
          }
          catch (NeuPathsException bre)
          {
            resp.succeeded = false;
            resp.errorInfo = bre.getMessage();
          }

          setStimulus(Daemon.COMMAND_RSP_TRANSMITTER, resp, cmd.getTransactionID());
          break;

        case START_CLUSTER:
        case PAUSE_CLUSTER:
        case RESUME_CLUSTER:
        case STOP_CLUSTER:
        case PUBLISH_CLUSTER:
        case RECALL_CLUSTER:
        case ENABLE_CLUSTER_LOGGING:
        case DISABLE_CLUSTER_LOGGING:
        case ENABLE_CLUSTER_TRACE:
        case DISABLE_CLUSTER_TRACE:
        case ENABLE_CLUSTER_DEBUG:
        case DISABLE_CLUSTER_DEBUG:
        case ENABLE_CLUSTER_RUNTIME:
        case DISABLE_CLUSTER_RUNTIME:
          executeClusterCmd(cmd.getTransactionID(), cmd.type, cmd.clusterName);
          break;

        case START_CELL:
        case PAUSE_CELL:
        case RESUME_CELL:
        case STOP_CELL:
        case PUBLISH_CELL:
        case ENABLE_CELL_LOGGING:
        case DISABLE_CELL_LOGGING:
        case ENABLE_CELL_TRACE:
        case DISABLE_CELL_TRACE:
        case ENABLE_CELL_DEBUG:
        case DISABLE_CELL_DEBUG:
        case ENABLE_CELL_RUNTIME:
        case DISABLE_CELL_RUNTIME:
          executeCellCmd(cmd.getTransactionID(), cmd.type, cmd.clusterName, cmd.cellName);
          break;
      }
    } // if daemonID matches
  }

  private void executeClusterCmd (UUID          transactionID,
                                  DaemonCmdType command,
                                  String        clusterName)
  {
    DaemonCmdRespStim resp = new DaemonCmdRespStim();
    CellCluster cluster = clusters.get(clusterName);

    if (cluster == null)
    {
      resp.succeeded = false;
      resp.errorInfo = "Cluster does not exist";
    }
    else
    {
      try
      {
        switch (command)
        {
          case START_CLUSTER:
            cluster.start();
            break;
          case PAUSE_CLUSTER:
            cluster.pause();
            break;
          case RESUME_CLUSTER:
            cluster.resume();
            break;
          case STOP_CLUSTER:
            cluster.stop();
            break;
          case PUBLISH_CLUSTER:
            cluster.publishSubscriptions();
            break;
          case RECALL_CLUSTER:
            cluster.stop();
            clusters.remove(clusterName);
            break;
          case ENABLE_CLUSTER_LOGGING:
            cluster.enableLogging();
            break;
          case DISABLE_CLUSTER_LOGGING:
            cluster.disableLogging();
            break;
          case ENABLE_CLUSTER_TRACE:
            cluster.enableTraceLogging();
            break;
          case DISABLE_CLUSTER_TRACE:
            cluster.disableTraceLogging();
            break;
          case ENABLE_CLUSTER_DEBUG:
            cluster.enableDebugLogging();
            break;
          case DISABLE_CLUSTER_DEBUG:
            cluster.disableDebugLogging();
            break;
          case ENABLE_CLUSTER_RUNTIME:
            cluster.enableRuntimeLogging();
            break;
          case DISABLE_CLUSTER_RUNTIME:
            cluster.disableRuntimeLogging();
            break;
        }
      }
      catch (NeuPathsException bre)
      {
        resp.succeeded = false;
        resp.errorInfo = bre.getMessage();
      }
    }

    setStimulus(Daemon.COMMAND_RSP_TRANSMITTER, resp, transactionID);
  }

  private void executeCellCmd (UUID          transactionID,
                               DaemonCmdType command,
                               String        clusterName,
                               String        cellName)
  {
    DaemonCmdRespStim resp = new DaemonCmdRespStim();
    CellCluster cluster = clusters.get(clusterName);

    if (cluster == null)
    {
      resp.succeeded = false;
      resp.errorInfo = "Cluster does not exist";
    }
    else
    {
      try
      {
        for (Cell cell : cluster)
        {
          if (cell.getName().matches(cellName))
          {
            switch (command)
            {
              case START_CELL:
                cell.start();
                break;
              case PAUSE_CELL:
                cell.pause();
                break;
              case RESUME_CELL:
                cell.resume();
                break;
              case STOP_CELL:
                cell.stop();
                break;
              case PUBLISH_CELL:
                cell.publishSubscriptions();
                break;
              case ENABLE_CELL_LOGGING:
                cell.enableLogging();
                break;
              case DISABLE_CELL_LOGGING:
                cell.disableLogging();
                break;
              case ENABLE_CELL_TRACE:
                cell.enableTraceLogging();
                break;
              case DISABLE_CELL_TRACE:
                cell.disableTraceLogging();
                break;
              case ENABLE_CELL_DEBUG:
                cell.enableDebugLogging();
                break;
              case DISABLE_CELL_DEBUG:
                cell.disableDebugLogging();
                break;
              case ENABLE_CELL_RUNTIME:
                cell.enableRuntimeLogging();
                break;
              case DISABLE_CELL_RUNTIME:
                cell.disableRuntimeLogging();
                break;
            }
          }
        }
      }
      catch (NeuPathsException bre)
      {
        resp.succeeded = false;
        resp.errorInfo = bre.getMessage();
      }
    }

    setStimulus(Daemon.COMMAND_RSP_TRANSMITTER, resp, transactionID);
  }

  String daemonName;
  UUID daemonID;
  String configTopDir;
  HashMap<String, CellCluster> clusters;
}
