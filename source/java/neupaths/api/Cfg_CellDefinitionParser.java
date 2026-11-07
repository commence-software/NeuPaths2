// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

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

abstract class Cfg_CellDefinitionParser
{
  protected
  Cfg_CellDefinitionParser (CellType        cellType,
                            String          cellDefinitionFile,
                            Cfg_CellHandler handler)
  {
    this.cellType = cellType;
    this.cellDefinitionFile = cellDefinitionFile;
    this.handler = handler;
  }

  protected abstract void parseNode (Node root);
  protected abstract void validate ();
  protected abstract Cell instantiate ();
  protected abstract Mdl_Cell model ();
  
  static
  Cfg_CellDefinitionParser
  createParser (String cellDefinitionFile)
  {
    Cfg_CellDefinitionParser parser = null;
    
    try
    {
      DocumentBuilderFactory documentFactory =
          DocumentBuilderFactory.newInstance();

      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

      documentBuilder.setErrorHandler(new XMLErrorHandler());
      
      Document document = documentBuilder.parse(cellDefinitionFile);
      
      NodeList nodes = document.getChildNodes();
      
      if (nodes.getLength() == 1)
      {
        Node root = nodes.item(0);

        if (root.getNodeType() == Node.ELEMENT_NODE)
        {
          String nodeName = root.getNodeName().toUpperCase();

          switch (nodeName)
          {
            case "BRIDGE_CELL":
              parser = new Cfg_BridgeCellParser(cellDefinitionFile);
              break;
            case "EVENT_CELL":
              parser = new Cfg_EventCellParser(cellDefinitionFile);
              break;
            case "EXTRACTOR_CELL":
              parser = new Cfg_ExtractorCellParser(cellDefinitionFile);
              break;
            case "INJECTOR_CELL":
              parser = new Cfg_InjectorCellParser(cellDefinitionFile);
              break;
            case "LOAD_BALANCED_CELL":
              parser = new Cfg_LoadBalancedCellParser(cellDefinitionFile);
              break;
            case "LOAD_CONTROLLER_CELL":
              parser = new Cfg_LoadControllerCellParser(cellDefinitionFile);
              break;
            case "LOGIC_CELL":
              parser = new Cfg_LogicCellParser(cellDefinitionFile);
              break;
            case "ROUTER_CELL":
              parser = new Cfg_RouterCellParser(cellDefinitionFile);
              break;
            case "CUSTOM_CELL":
              parser = new Cfg_CustomCellParser(cellDefinitionFile);
              break;
            default:
              throw new NeuPathsException("Cell definition file " +
                                          cellDefinitionFile +
                                          " contains invalid top-level element:" +
                                          nodeName);
          }

          parser.parseNode(root);
        }
        else
        {
          throw new NeuPathsException("Cell definition file " +
                                      cellDefinitionFile +
                                      " has invalid format");
        }
      }
      else
      {
        throw new NeuPathsException("Cell definition file " +
                                    cellDefinitionFile +
                                    " has invalid format");
      }
    }
    catch (ParserConfigurationException pce)
    {
      throw new NeuPathsException("Could not parse cell definition file " +
                                  cellDefinitionFile, pce);
    }
    catch (SAXException se)
    {
      throw new NeuPathsException("Could not parse cell definition file " +
                                  cellDefinitionFile, se);
    }
    catch (IOException ioe)
    {
      throw new NeuPathsException("Could not parse cell definition file " +
                                  cellDefinitionFile, ioe);
    }

    return parser;
  }

  protected void applyCellOptions (Cell cell)
  {
    if (handler.loggingEnabled)
    {
      cell.enableLogging();
    }
    else
    {
      cell.disableLogging();
    }

    if (handler.traceLoggingEnabled)
    {
      cell.enableTraceLogging();
    }
    else
    {
      cell.disableTraceLogging();
    }
  
    if (handler.debugLoggingEnabled)
    {
      cell.enableDebugLogging();
    }
    else
    {
      cell.disableDebugLogging();
    }

    if (handler.runtimeLoggingEnabled)
    {
      cell.enableRuntimeLogging();
    }
    else
    {
      cell.disableRuntimeLogging();
    }

    if (handler.propagateGlobalSubscriptions)
    {
      cell.enableGlobalSubscriptionPropagation();
    }
    else
    {
      cell.disableGlobalSubscriptionPropagation();
    }

    if (cell.getType() != CellType.INJECTOR &&
        cell.getType() != CellType.ROUTER)
    {
      cell.setSubscriptionRefreshInterval(handler.subscriptionRefreshInterval);
    }

    cell.setDuplicateDetectionInterval(handler.duplicateDetectionInterval);

    cell.setSubscriptionTraceInterval(handler.subscriptionTraceInterval);

    if (cell.getType() == CellType.LOGIC ||
        cell.getType() == CellType.LOAD_BALANCED ||
        cell.getType() == CellType.CUSTOM)
    {
      cell.setPulseInterval(handler.pulseInterval);
    }
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
  
  CellType cellType;
  String cellDefinitionFile;
  Cfg_CellHandler handler;
}
