// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell}s activator configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellActivatorsHandler extends Cfg_ConfigHandler implements Cfg_ActivatorHandlerInt
{
  Cfg_CellActivatorsHandler (String xPath)
  {
    super(xPath);
    
    activators = new LinkedList<>();
  }
  
  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("ACTIVATOR"))
    {
      Cfg_CellActivatorHandler handler = new Cfg_CellActivatorHandler(xPath + "/Activator");
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
    Cfg_ActivatorsHandlerInt handler = (Cfg_ActivatorsHandlerInt) parentHandler;
    
    for (Cfg_CellActivatorHandler context : activators)
    {
      try
      {
        if (context.className == null)
        {
          throw new NeuPathsException("Activator class name must be provided in file " + configFile);
        }
        
        // Load the activator class
        Class<?> actvtrClass = Class.forName(context.className);

        // Instantiate the activator class
        Activator actvtrObj =
            (Activator) actvtrClass.getDeclaredConstructor().newInstance();
        
        actvtrObj.setTransactionHistoryWindow(context.transactionHistoryWindow);

        handler.addActivator(actvtrObj);
      }
      catch (ClassNotFoundException cnfe)
      {
        throw new NeuPathsException("Could not instantiate activator in file " + configFile, cnfe);
      }
      catch (NoSuchMethodException nsme)
      {
        throw new NeuPathsException("Could not instantiate activator in file " + configFile, nsme);
      }
      catch (InstantiationException ie)
      {
        throw new NeuPathsException("Could not instantiate activator in file " + configFile, ie);
      }
      catch (IllegalAccessException iae)
      {
        throw new NeuPathsException("Could not instantiate activator in file " + configFile, iae);
      }
      catch (java.lang.reflect.InvocationTargetException ite)
      {
        throw new NeuPathsException("Could not instantiate activator in file " + configFile, ite);
      }
    }
  }

  public void addActivator (Cfg_CellActivatorHandler activator)
  {
    activators.add(activator);
  }

  LinkedList<Cfg_CellActivatorHandler> activators;
}
