// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.UUID;
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

final class Cfg_CellClusterParser
{
  Cfg_CellClusterParser (String clusterDefinitionFile)
  {
    this.clusterDefinitionFile = clusterDefinitionFile;

    handler = new Cfg_CellClusterHandler();

    parse();
  }

  private void parse ()
  {
    try
    {
      DocumentBuilderFactory documentFactory =
          DocumentBuilderFactory.newInstance();

      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

      documentBuilder.setErrorHandler(new XMLErrorHandler());
      
      Document document = documentBuilder.parse(clusterDefinitionFile);
      
      NodeList nodes = document.getChildNodes();
      
      if (nodes.getLength() == 1)
      {
        Node node = nodes.item(0);
        String nodeName = node.getNodeName().toUpperCase();

        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          if (nodeName.equals("CELL_CLUSTER"))
          {
            Cfg_Utils.processElement(clusterDefinitionFile, null, handler, node);
          }
          else
          {
            throw new NeuPathsException("Cluster definition file " +
                                        clusterDefinitionFile +
                                        " contains invalid top-level element: " +
                                        nodeName);
          }
        }
        else
        {
          throw new NeuPathsException("Cluster definition file " +
                                      clusterDefinitionFile +
                                      " has invalid format");
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

  void validate ()
  {
    if (handler.name == null)
    {
      throw new NeuPathsException("Clusters require a name");
    }
  }
  
  void instantiate (LinkedHashMap<String, Cell> cells)
  {
    for (String cellDefinitionFile : handler.cellDefinitionFiles)
    {
      Cell newCell = CellFactory.createCell(cellDefinitionFile);

      if (handler.loggingSpecified)
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

      if (handler.traceLoggingSpecified)
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

      if (handler.debugLoggingSpecified)
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

      if (handler.runtimeLoggingSpecified)
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
           newCell.getType() == CellType.LOAD_BALANCED ||
           newCell.getType() == CellType.CUSTOM))
      {
        newCell.setPulseInterval(handler.pulseInterval);
      }

      cells.put(newCell.getName(), newCell);
    }
  }

  void model (LinkedHashMap<String, Mdl_Cell> cells,
              LinkedHashSet<Mdl_Flow>         flows,
              LinkedHashSet<Mdl_Syn>          syns)
  {
    // First pass builds the cell list
    for (String cellDefinitionFile : handler.cellDefinitionFiles)
    {
      Cfg_CellDefinitionParser parser =
        Cfg_CellDefinitionParser.createParser(cellDefinitionFile);

      parser.validate();
      
      Mdl_Cell newCell = parser.model();

      if (cells.containsKey(newCell.name))
      {
        throw new NeuPathsException("Cell " + newCell.name + " already exists");
      }
      
      cells.put(newCell.name, newCell);
    }

    // Second pass fills in the dataflow
    for (Mdl_Cell scanCell : cells.values())
    {
      for (Mdl_Sub scanSub : scanCell.subs)
      {
        for (Mdl_Cell checkCell : cells.values())
        {
          // Exclude the cell currently being scanned
          if (!checkCell.name.equals(scanCell.name))
          {
            if (checkCell.name.matches(scanSub.cell))
            {
              for (Mdl_Tx checkTx : checkCell.txs)
              {
                if (checkTx.name.matches(scanSub.tx))
                {
                  String rx = scanSub.rx;
                  if (rx.equals("Unspecified"))
                    rx = checkTx.name;
                  
                  flows.add(new Mdl_Flow(checkCell.name,
                                         checkTx.name,
                                         scanCell.name,
                                         rx,
                                         scanSub.dmn,
                                         scanSub.act));
                }
              }  // for each transmitter to check
            } // if cell matches subscription
          } // if not scan cell
        } // for each cell to check
      } // for each subscription
    } // for each cell to scan

    // Third pass fills in the synapses
    for (Mdl_Cell scanCell : cells.values())
    {
      for (String scanSyn : scanCell.syns)
      {
        try
        {
          Syn_Name scanName = new Syn_Name(scanSyn);
  
          if (scanName.getMode() != Syn_Mode.LISTENER)
            continue;
          
          String scanSynModeless = scanName.getModelessText();
          
          for (Mdl_Cell checkCell : cells.values())
          {
            // Exclude the cell currently being scanned
            if (!checkCell.name.equals(scanCell.name))
            {
              for (String checkSyn : checkCell.syns)
              {
                try
                {
                  Syn_Name checkName = new Syn_Name(checkSyn);
    
                  if (checkName.getMode() != Syn_Mode.PEER)
                    continue;
    
                  String checkSynModeless = checkName.getModelessText();
    
                  if (checkSynModeless.equals(scanSynModeless))
                  {
                    Mdl_Syn syn = new Mdl_Syn(scanSyn, scanCell.name);

                    boolean found = false;
                    for (Mdl_Syn s : syns)
                    {
                      if (s.equals(syn))
                      {
                        s.peers.add(checkCell.name);
                        found = true;
                        break;
                      }
                    }

                    if (!found)
                    {
                      syn.peers.add(checkCell.name);
                      syns.add(syn);
                    }
                  }
                }
                catch (Excp_SynapseFatal sfe1)
                {
                  System.out.println("WARNING: Invalid synapse: " + checkSyn);
                }
              } // for each synapse to check
            } // if not scan cell
          } // for each cell to check
        }
        catch (Excp_SynapseFatal sfe2)
        {
          System.out.println("WARNING: Invalid synapse: " + scanSyn);
        }
      } // for each synapse to scan
    } // for each cell to scan
  }
  
  private static class XMLErrorHandler implements ErrorHandler
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
  
  String clusterDefinitionFile;
  Cfg_CellClusterHandler handler;
}
