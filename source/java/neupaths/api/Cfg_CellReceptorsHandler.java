// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for {@link LoadControllCell} receptors.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellReceptorsHandler extends Cfg_ConfigHandler implements Cfg_ReceptorHandlerInt
{
  Cfg_CellReceptorsHandler (String xPath)
  {
    super(xPath);

    receptors = new LinkedList<>();
  }
  
  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("RECEPTOR"))
    {
      Cfg_CellReceptorHandler handler = new Cfg_CellReceptorHandler(xPath + "/Receptor");
      Cfg_Utils.processElement(configFile, this, handler, node);
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }

  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_ReceptorsHandlerInt handler = (Cfg_ReceptorsHandlerInt) parentHandler;
    
    for (Cfg_CellReceptorHandler context : receptors)
    {
      try
      {
        // Load the stimulus class
        Class<?> stimClass = Class.forName(context.stimulusClassName);
        UUID stimTypeID = (UUID) stimClass.getField("TYPE_ID").get(null);
      
        handler.addReceptor(
            new ReceptorSpec(context.name,
                             context.mode,
                             stimTypeID));
      }
      catch (ClassNotFoundException cnfe)
      {
        throw new NeuPathsException("Could not resolve TYPE_ID for receptor in file " + configFile, cnfe);
      }
      catch (IllegalAccessException iae)
      {
        throw new NeuPathsException("Could not resolve TYPE_ID for receptor in file " + configFile, iae);
      }
      catch (NoSuchFieldException nsfe)
      {
        throw new NeuPathsException("Could not resolve TYPE_ID for receptor in file " + configFile, nsfe);
      }
    }
  }

  public void addReceptor (Cfg_CellReceptorHandler receptor)
  {
    receptors.add(receptor);
  }
  
  LinkedList<Cfg_CellReceptorHandler> receptors;
}
