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

abstract class Cfg2_CellDefinitionParser
{
  Cfg2_CellDefinitionParser (CellType         cellType,
                             String           cellDefinitionFile,
                             Cfg2_CellHandler handler)
  {
    this.cellType = cellType;
    this.cellDefinitionFile = cellDefinitionFile;
    this.handler = handler;

    switch (cellType)
    {
      case BRIDGE:
        rootNode = "BRIDGE_CELL";
        break;
      case EVENT:
        rootNode = "EVENT_CELL";
        break;
      case EXTRACTOR:
        rootNode = "EXTRACTOR_CELL";
        break;
      case INJECTOR:
        rootNode = "INJECTOR_CELL";
        break;
      case LOAD_BALANCED:
        rootNode = "LOAD_BALANCED_CELL";
        break;
      case LOAD_CONTROLLER:
        rootNode = "LOAD_CONTROLLER_CELL";
        break;
      case LOGIC:
        rootNode = "LOGIC_CELL";
        break;
      case ROUTER:
        rootNode = "ROUTER_CELL";
        break;
      case CUSTOM:
        rootNode = "CUSTOM_CELL";
        break;
      default:
        throw new NeuPathsException("Invalid cell type");
    }
  }

  protected abstract void parseNode (Node root);
  protected abstract void validate ();
  protected abstract Cell instantiate ();
  
  protected void parse ()
  {
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
        Node node = nodes.item(0);
        String nodeName = node.getNodeName().toUpperCase();

        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          if (nodeName.equals(rootNode))
          {
            parseNode(node);
          }
          else
          {
            throw new NeuPathsException("Cell definition file " +
                                        cellDefinitionFile +
                                        " contains invalid top-level element: " +
                                        nodeName);
          }
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
  Cfg2_CellHandler handler;
  String rootNode;
}
