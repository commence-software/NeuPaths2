// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Creates a cell cluster according to a cluster definition file.
 * <p>
 * A cell cluster is a collection of NeuPaths cell objects that can be
 * manipulated as a single unit.  The cells can be tightly-coupled
 * (i.e. heavily interconnected), loosely-coupled (i.e. lightly
 * interconnected) or independent.
 * </p>
 * <p>
 * A cluster definition file contains an XML schema that lists the
 * cell definition file for each cell in the cluster.  Logging and
 * interval settings override the settings in the individual cell
 * definition files.
 * </p>
 * <ul>
 * <pre>
 * {@literal <cell_cluster>}
 * {@literal     <name></name>}
 * {@literal     <cell_definitions>}
 * {@literal         <cell_definition></cell_definition>}
 * {@literal         ...}
 * {@literal     </cell_definitions>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <pulse_interval_ms>n</pulse_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </cell_cluster>}</pre>
 * </ul>
 * <h2>Cluster Definition Element Types</h2>
 * <p>
 * <table border=1>
 * <tr align=center valign=bottom>
 * <td width="20%"><b>Element Name</b></td>
 * <td><b>Description</b></td>
 * <td><b>Element Path</b></td>
 * <td><b>Element Type</b></td>
 * <td><b>Opt?</b></td>
 * <td><b>Default</b></td>
 * <td><b>Example</b></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>cell_definition</td>
 * <td>Pathname of a cell definition file</td>
 * <td>/cell_cluster/cell_definitions/cell_definition</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>cfg/A_Cell.xml</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>crypto_key_file</td>
 * <td>Pathname of file containing crypto secret key.</td>
 * <td>/cell_cluster/crypto_key_file</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>null</td>
 * <td>cfg/stim_key.dat</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>debug_logging_enabled</td>
 * <td>Indicates if logging of {@link EventType#DEBUG} events is enabled.</td>
 * <td>/cell_cluster/debug_logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>false</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>duplicate_detection_interval_ms</td>
 * <td>Length of duplicate detection window (millisecs).</td>
 * <td>/cell_cluster/duplicate_detection_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>1000</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>logging_enabled</td>
 * <td>Indicates if cell logging is enabled.</td>
 * <td>/cell_cluster/logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>true</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>propagate_global_subscriptions</td>
 * <td>Allows propagation of subscriptions in the global domain.</td>
 * <td>/cell_cluster/propagate_global_subscriptions</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>true</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>pulse_interval_ms</td>
 * <td>Interval between pulses (millisecs).</td>
 * <td>/cell_cluster/pulse_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>0</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>runtime_logging_enabled</td>
 * <td>Indicates if logging of {@link EventType#RUNTIME} events is enabled.</td>
 * <td>/cell_cluster/runtime_logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>false</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>subscription_refresh_interval_ms</td>
 * <td>Interval at which cell's subscriptions are refreshed (millisecs).</td>
 * <td>/cell_cluster/subscription_refresh_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>1500</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>subscription_trace_interval_ms</td>
 * <td>Interval at which cell's current subscriptions are reported via {@link EventType#TRACE} events (millisecs).</td>
 * <td>/cell_cluster/subscription_trace_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>0</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>trace_logging_enabled</td>
 * <td>Indicates if logging of {@link EventType#TRACE} events is enabled.</td>
 * <td>/cell_cluster/trace_logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>false</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>name</td>
 * <td>Cluster name</td>
 * <td>/cell_cluster/name</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>A_Cluster</td>
 * </tr>
 * </table>
 *
 * @author Aaron Caraveo
 */
public class CellCluster implements Iterable<Cell>
{
  /**
   * Creates a collection of NeuPaths cell objects according to the
   * cell definitions listed in a cluster definition file.
   *
   * @param clusterDefinitionFile Pathname of a cluster definition XML file.
   */
  public CellCluster (String clusterDefinitionFile)
  {
    cells = new LinkedHashMap<>();

    try
    {
      DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      
      documentBuilder.setErrorHandler(new XMLErrorHandler());
      
      Document document = documentBuilder.parse(clusterDefinitionFile);
      
      NodeList nodes = document.getChildNodes();
      
      if (nodes.getLength() == 1)
      {
        Node node = nodes.item(0);
        String nodeName = node.getNodeName().toUpperCase();
        Syn_Name daemonSynapse = null;
        Syn_Name clusterSynapse = null;

        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          if (nodeName.equals("CELL_CLUSTER"))
          {
            Cfg_CellClusterHandler handler = new Cfg_CellClusterHandler();
            Cfg_Utils.processElement(clusterDefinitionFile, null, handler, node);

            if (handler.clusterName == null)
            {
              throw new NeuPathsException("Cluster name is missing in cluster definition file " +
                                          clusterDefinitionFile);
            }

            try
            {
              LinkedList<String> daemonOptions = new LinkedList<>();
              daemonOptions.addLast(System.getProperty("java.io.tmpdir") + File.separator + "daemon_" + Math.abs(handler.clusterName.hashCode()) + ".syn");

              // Daemon to Cluster
              daemonSynapse =
                  new Syn_Name(Syn_Scope.LOCAL,
                               Syn_Type.STREAM,
                               Syn_Mode.LISTENER,
                               "Daemon:Ctrl",
                               daemonOptions);

              LinkedList<String> clusterOptions = new LinkedList<>();
              clusterOptions.addLast(System.getProperty("java.io.tmpdir") + File.separator + "cluster_" + Math.abs(handler.clusterName.hashCode()) + ".syn");

              // Cell to Cluster 
              clusterSynapse =
                  new Syn_Name(Syn_Scope.LOCAL,
                               Syn_Type.STREAM,
                               Syn_Mode.LISTENER,
                               "Cluster:Ctrl",
                               clusterOptions);
            }
            catch (Excp_SynapseFatal sfe)
            {
              throw new NeuPathsException("Could not create synapse name: " + sfe);
            }

            BridgeCell clusterBridge =
                new BridgeCell(handler.clusterName,
                               new String[] {
                                   daemonSynapse.toString(),
                                   clusterSynapse.toString()
                               },
                               new BridgeSubscriptionSpec[] {
                                   new BridgeSubscriptionSpec("MasterControl",
                                                              "Command",
                                                              "Daemon:Ctrl")
                               },
                               Cfg_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

            // Don't propagate global subscriptions.  Doing so could result
            // in stimuli crossing over the control domains to all clusters.
            clusterBridge.disableGlobalSubscriptionPropagation();

            cells.put(handler.clusterName, clusterBridge);
            name = handler.clusterName;
            instanceID = clusterBridge.getInstanceID();

            for (String cellDefinitionFile : handler.cellDefinitionFiles)
            {
              Cell newCell = CellFactory.createCell(handler,
                                                    cellDefinitionFile);

              if (handler.loggingSpecified &&
                  newCell.getType() != CellType.EVENT)
              {
                if (handler.loggingEnabled)
                {
                  newCell.enableLogging();
                }
                else
                {
                  newCell.disableLogging();
                }
              }

              if (handler.traceLoggingSpecified &&
                  newCell.getType() != CellType.EVENT)
              {
                if (handler.traceLoggingEnabled)
                {
                  newCell.enableTraceLogging();
                }
                else
                {
                  newCell.disableTraceLogging();
                }
              }

              if (handler.debugLoggingSpecified &&
                  newCell.getType() != CellType.EVENT)
              {
                if (handler.debugLoggingEnabled)
                {
                  newCell.enableDebugLogging();
                }
                else
                {
                  newCell.disableDebugLogging();
                }
              }

              if (handler.runtimeLoggingSpecified &&
                  newCell.getType() != CellType.EVENT)
              {
                if (handler.runtimeLoggingEnabled)
                {
                  newCell.enableRuntimeLogging();
                }
                else
                {
                  newCell.disableRuntimeLogging();
                }
              }

              if (handler.propagateGlobalSpecified)
              {
                if (handler.propagateGlobalSubscriptions)
                {
                  newCell.enableGlobalSubscriptionPropagation();
                }
                else
                {
                  newCell.disableGlobalSubscriptionPropagation();
                }
              }

              if (handler.subscriptionRefreshSpecified &&
                  newCell.getType() != CellType.INJECTOR &&
                  newCell.getType() != CellType.ROUTER)
              {
                newCell.setSubscriptionRefreshInterval(handler.subscriptionRefreshInterval);
              }

              if (handler.duplicateDetectionSpecified)
              {
                newCell.setDuplicateDetectionInterval(handler.duplicateDetectionInterval);
              }

              if (handler.subscriptionTraceSpecified)
              {
                newCell.setSubscriptionTraceInterval(handler.subscriptionTraceInterval);
              }

              if (handler.pulseSpecified &&
                  (newCell.getType() == CellType.LOGIC ||
                   newCell.getType() == CellType.LOAD_BALANCED))
              {
                newCell.setPulseInterval(handler.pulseInterval);
              }

              cells.put(newCell.getName(), newCell);
            }
          }
          else
          {
            throw new NeuPathsException("Cluster definition file " +
                                        clusterDefinitionFile +
                                        " contains invalid top-level element:" +
                                        nodeName);
          }
        }
      }
      else
      {
        throw new NeuPathsException("Cluster definition file " +
                                    clusterDefinitionFile +
                                    " has invalid format");
      }
    }
    catch (ParserConfigurationException pce)
    {
      throw new NeuPathsException("Could not parse cluster definition file " +
                                     clusterDefinitionFile, pce);
    }
    catch (SAXException se)
    {
      throw new NeuPathsException("Could not parse cluster definition file " +
                                     clusterDefinitionFile, se);
    }
    catch (IOException ioe)
    {
      throw new NeuPathsException("Could not parse cluster definition file " +
                                     clusterDefinitionFile, ioe);
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Retrieves name of this cluster.
   *
   * @return The cluster's name.
   */
  public
  String
  getName ()
  {
    return name;
  }
  
  //---------------------------------------------------------------------------

  /**
   * Retrieves instance ID of this cluster.
   *
   * @return The cluster's instance ID.
   */
  public
  UUID
  getInstanceID ()
  {
    return instanceID;
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables event logging for the cluster.
   */
  public
  void
  enableLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables event logging for the cluster.
   */
  public
  void
  disableLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables Runtime event logging for the cluster.
   */
  public
  void
  enableRuntimeLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableRuntimeLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Runtime event logging for the cluster.
   */
  public
  void
  disableRuntimeLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableRuntimeLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables Trace event logging for the cluster.
   */
  public
  void
  enableTraceLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableTraceLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Trace event logging for the cluster.
   */
  public
  void
  disableTraceLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableTraceLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Enables Debug event logging for the cluster.
   */
  public
  void
  enableDebugLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableDebugLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Debug event logging for the cluster.
   */
  public
  void
  disableDebugLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableDebugLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Enables Runtime, Trace and Debug event logging for the cluster.
   */
  public
  void
  enableSystemLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableSystemLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Disables Runtime, Trace and Debug event logging for the cluster.
   */
  public
  void
  disableSystemLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableSystemLogging();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Enables Debug output logging for the cluster.  Event data will
   * be output to standard output.
   */
  public
  void
  enableDebugOutputLogging ()
  {
    for (Cell c : cells.values())
    {
      c.enableDebugOutputLogging();
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Disables Debug output logging for the cluster.
   */
  public
  void
  disableDebugOutputLogging ()
  {
    for (Cell c : cells.values())
    {
      c.disableDebugOutputLogging();
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Allows propagation of subscriptions in the global domain.
   */
  public
  void
  enableGlobalSubscriptionPropagation ()
  {
    for (Cell c : cells.values())
    {
      c.enableGlobalSubscriptionPropagation();
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Disables propagation of subscriptions in the global domain.
   */
  public
  void
  disableGlobalSubscriptionPropagation ()
  {
    for (Cell c : cells.values())
    {
      c.disableGlobalSubscriptionPropagation();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Sets the duplicate detection interval.
   * <p>
   * The default is 1000 milliseconds (1 second.)  The minimum is 250
   * milliseconds.  Values less than the minimum will be automatically
   * changed to the minimum value.
   * </p>
   * 
   * @param millisecs Duration of interval in milliseconds
   */
  public
  void
  setDuplicateDetectionInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setDuplicateDetectionInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets the subscription refresh interval.
   * <p>
   * The default is 1500 milliseconds (1.5 seconds.)  Values less than or
   * equal to zero will disable automatic subscription refreshes.  When
   * disabled, the user must manually invoke {@link #publishSubscriptions}
   * to advertise the cell's subscriptions.
   * </p>
   * 
   * @param millisecs Duration of interval in milliseconds
   */
  public
  void
  setSubscriptionRefreshInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setSubscriptionRefreshInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Sets the subscription trace interval.
   * <p>
   * When enabled, the cell's nucleus reports the subscriptions it has
   * registered in an {@link EventType#TRACE} log event each interval.
   * Values less than or equal to zero disable subscription tracing.
   * The value is zero by default, meaning this feature must be
   * specifically enabled.
   * </p>
   * 
   * @param millisecs Duration of interval in milliseconds
   */
  public
  void
  setSubscriptionTraceInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setSubscriptionTraceInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Sets the pulse generator interval.
   * <p>
   * When enabled, produces a periodic pulse that can be acted upon using
   * a specialized {@link PulsedActivator}.  Values less than or equal to
   * zero disable pulse generation.  The value is zero by default, meaning
   * this feature must be specicially enabled.
   * </p>
   *
   * @param millisecs Duration of pulse interval in milliseconds
   */
  public
  void
  setPulseInterval (long millisecs)
  {
    for (Cell c : cells.values())
    {
      c.setPulseInterval(millisecs);
    }
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Advertises the cell's subscriptions to the cell system.  This method can
   * be used when subscription refresh has been turned off.  It should only be
   * invoked after the cell has been started.
   */
  public
  void
  publishSubscriptions ()
  {
    for (Cell c : cells.values())
    {
      c.publishSubscriptions();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Starts the cluster.  Cells will be started in the order they were
   * defined in the cluster definition file.
   */
  public void start ()
  {
    for (Cell c : cells.values())
    {
      c.start();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Pause the cluster.  Cells will be paused in the order they were
   * defined in the cluster definition file.
   */
  public void pause ()
  {
    for (Cell c : cells.values())
    {
      c.pause();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Resume the cluster.  Cells will be resumed in the order they were
   * defined in the cluster definition file.
   */
  public void resume ()
  {
    for (Cell c : cells.values())
    {
      c.resume();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Stops the cluster.  Cells will be stopped in the reverse order they were
   * defined in the cluster definition file.
   */
  public void stop ()
  {
    Cell[] tmpForTyping = new Cell[0];
    Cell[] cellObjs = cells.values().toArray(tmpForTyping);

    for (int i = (cellObjs.length - 1); i >= 0; i--)
    {
      cellObjs[i].stop();
    }
  }

  //---------------------------------------------------------------------------
  
  /**
   * Retrieves a reference to the named cell.
   *
   * @param name The cell's name.
   */
  @SuppressWarnings("unchecked")
  public
  <T extends Cell> T
  getCell (String name)
  {
    return (T)(cells.get(name));
  }

  //---------------------------------------------------------------------------

  @Override
  public
  Iterator<Cell>
  iterator ()
  {
    return cells.values().iterator();
  }

  private String name;
  private UUID instanceID;
  private LinkedHashMap<String, Cell> cells;

  private class XMLErrorHandler implements ErrorHandler
  {
    public void error (SAXParseException exception) throws SAXException
    {
      throw exception;
    }

    public void fatalError (SAXParseException exception) throws SAXException
    {
      throw exception;
    }

    public void warning (SAXParseException exception) throws SAXException
    {
      throw exception;
    }
  }
}
