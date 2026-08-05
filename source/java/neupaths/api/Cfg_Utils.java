// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities used by configuration parsing.
 *
 * @author Aaron Caraveo
 */
class Cfg_Utils
{
  static String getNodeText (Node node)
  {
    String text = null;
    
    NodeList children = node.getChildNodes();
    
    for (int i = 0; i < children.getLength(); i++)
    {
      Node childNode = children.item(i);
      
      if (childNode.getNodeType() == Node.TEXT_NODE)
      {
        text = childNode.getNodeValue();
        break;
      }
    }
    
    return text;
  }

  static void processElement (String configFile,
                              Cfg_ConfigHandler parentHandler,
                              Cfg_ConfigHandler handler,
                              Node node)
  {
    NodeList childNodes = node.getChildNodes();
    
    for (int i = 0; i < childNodes.getLength(); i++)
    {
      Node childNode = childNodes.item(i);

      if (childNode.getNodeType() != Node.ELEMENT_NODE)
        continue;
      
      handler.processNode(configFile, childNode);
    }
    
    handler.processElement(configFile, parentHandler);
  }

  static byte[] cryptoKeyBytes (String cryptoKeyFile)
  {
    byte[] cryptoKey = null;

    if (cryptoKeyFile != null)
    {
      try
      {
        File keyFile = new File(cryptoKeyFile);

        cryptoKey = new byte[(int)keyFile.length()];

        FileInputStream keyInput = new FileInputStream(keyFile);
        keyInput.read(cryptoKey);
      }
      catch (FileNotFoundException fnfe)
      {
        throw new NeuPathsException("Could not find " + cryptoKeyFile);
      }
      catch (IOException ioe)
      {
        throw new NeuPathsException("Could not open " + cryptoKeyFile);
      }
    }

    return cryptoKey;
  }
}
