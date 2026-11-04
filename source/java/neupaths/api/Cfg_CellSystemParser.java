// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedHashMap;
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

final class Cfg_CellSystemParser
{
  Cfg_CellSystemParser (String systemDefinitionFile)
  {
    this.systemDefinitionFile = systemDefinitionFile;

    handler = new Cfg_CellSystemHandler();
  }

  void parse ()
  {
    try
    {
      DocumentBuilderFactory documentFactory =
          DocumentBuilderFactory.newInstance();

      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

      documentBuilder.setErrorHandler(new XMLErrorHandler());
      
      Document document = documentBuilder.parse(systemDefinitionFile);
      
      NodeList nodes = document.getChildNodes();
      
      if (nodes.getLength() == 1)
      {
        Node node = nodes.item(0);
        String nodeName = node.getNodeName().toUpperCase();

        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          if (nodeName.equals("CELL_SYSTEM"))
          {
            Cfg_Utils.processElement(systemDefinitionFile, null, handler, node);
          }
          else
          {
            throw new NeuPathsException("System definition file " +
                                        systemDefinitionFile +
                                        " contains invalid top-level element: " +
                                        nodeName);
          }
        }
        else
        {
          throw new NeuPathsException("System definition file " +
                                      systemDefinitionFile +
                                      " has invalid format");
        }
      }
      else
      {
        throw new NeuPathsException("System definition file " +
                                    systemDefinitionFile +
                                    " has invalid format");
      }
    }
    catch (ParserConfigurationException pce)
    {
      throw new NeuPathsException("Could not parse system definition file " +
                                  systemDefinitionFile, pce);
    }
    catch (SAXException se)
    {
      throw new NeuPathsException("Could not parse system definition file " +
                                  systemDefinitionFile, se);
    }
    catch (IOException ioe)
    {
      throw new NeuPathsException("Could not parse system definition file " +
                                  systemDefinitionFile, ioe);
    }
  }

  void validate ()
  {
    if (handler.systemName == null)
    {
      throw new NeuPathsException("Systems require a name");
    }
  }
  
  void instantiate (LinkedHashMap<String, CellCluster> clusters)
  {
    for (String clusterDefinitionFile : handler.clusterDefinitionFiles)
    {
      CellCluster newCluster = new CellCluster(clusterDefinitionFile);

      clusters.put(newCluster.getName(), newCluster);
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
  
  String systemDefinitionFile;
  Cfg_CellSystemHandler handler;
}
