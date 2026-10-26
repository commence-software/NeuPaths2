// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

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

import neupaths.util.PropertySet;

final class Cfg_CustomCellParser extends Cfg_CellDefinitionParser
{
  Cfg_CustomCellParser (String cellDefinitionFile)
  {
    super(CellType.CUSTOM,
          cellDefinitionFile,
          new Cfg_CustomCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    Cfg_CustomCellHandler ch = (Cfg_CustomCellHandler) handler;

    if (ch.className == null)
    {
      throw new NeuPathsException("Custom cells require a class name");
    }
    
    if (handler.activators.size() == 0)
    {
      throw new NeuPathsException("Custom cells require at least one activator");
    }

    for (Cfg_CellActivatorHandler activator : handler.activators)
    {
      if (activator.className == null)
      {
        throw new NeuPathsException("Activators must provide a class name");
      }
      
      for (Cfg_CellReceptorHandler receptor : activator.receptors)
      {
        if (receptor.stimulusClassName == null)
        {
          throw new NeuPathsException("Receptors must provide a stimulus class name");
        }
      }
      
      for (Cfg_CellTransmitterHandler transmitter : activator.transmitters)
      {
        if (transmitter.stimulusClassName == null)
        {
          throw new NeuPathsException("Transmitters must provide a stimulus class name");
        }
      }
      
      for (Cfg_CellSubscriptionHandler subscription : activator.subscriptions)
      {
        if (subscription.subType != SubscriptionType.LOGIC &&
            subscription.subType != SubscriptionType.MAP &&
            subscription.subType != SubscriptionType.LOOPBACK)
        {
          throw new NeuPathsException("Custom cells only accept logic-based subscriptions");
        }
      }
    }
  }
  
  protected Cell instantiate ()
  {
    Cfg_CustomCellHandler ch = (Cfg_CustomCellHandler) handler;
    Cell newCell = null;

    Activator[] activators = new Activator[handler.activators.size()];

    for (int a = 0; a < handler.activators.size(); a++)
    {
      Cfg_CellActivatorHandler activator = handler.activators.get(a);

      ReceptorSpec[] receptors =
        new ReceptorSpec[activator.receptors.size()];

      for (int r = 0; r < activator.receptors.size(); r++)
      {
        Cfg_CellReceptorHandler receptor = activator.receptors.get(r);
        
        try
        {
          // Load the stimulus class
          Class<?> stimClass = Class.forName(receptor.stimulusClassName);
          UUID stimTypeID = (UUID) stimClass.getField("TYPE_ID").get(null);
    
          receptors[r] =
            new ReceptorSpec(receptor.name,
                             receptor.mode,
                             stimTypeID);
        }
        catch (ClassNotFoundException cnfe)
        {
          throw new NeuPathsException("Could not resolve TYPE_ID for receptor " + receptor.name + " in file " + cellDefinitionFile, cnfe);
        }
        catch (IllegalAccessException iae)
        {
          throw new NeuPathsException("Could not resolve TYPE_ID for receptor " + receptor.name + " in file " + cellDefinitionFile, iae);
        }
        catch (NoSuchFieldException nsfe)
        {
          throw new NeuPathsException("Could not resolve TYPE_ID for receptor " + receptor.name + " in file " + cellDefinitionFile, nsfe);
        }
      }
        
      TransmitterSpec[] transmitters =
        new TransmitterSpec[activator.transmitters.size()];

      for (int t = 0; t < activator.transmitters.size(); t++)
      {
        Cfg_CellTransmitterHandler transmitter = activator.transmitters.get(t);

        try
        {
          // Load the stimulus class
          Class<?> stimClass = Class.forName(transmitter.stimulusClassName);
          UUID stimTypeID = (UUID) stimClass.getField("TYPE_ID").get(null);
    
          transmitters[t] =
            new TransmitterSpec(transmitter.name,
                                stimTypeID,  // need to load class
                                transmitter.trace);
        }
        catch (ClassNotFoundException cnfe)
        {
          throw new NeuPathsException("Could not resolve TYPE_ID for transmitter " + transmitter.name + " in file " + cellDefinitionFile, cnfe);
        }
        catch (IllegalAccessException iae)
        {
          throw new NeuPathsException("Could not resolve TYPE_ID for transmitter " + transmitter.name + " in file " + cellDefinitionFile, iae);
        }
        catch (NoSuchFieldException nsfe)
        {
          throw new NeuPathsException("Could not resolve TYPE_ID for transmitter " + transmitter.name + " in file " + cellDefinitionFile, nsfe);
        }
      }
      
      LogicSubscriptionSpec[] subscriptions =
        new LogicSubscriptionSpec[activator.subscriptions.size()];

      for (int s = 0; s < activator.subscriptions.size(); s++)
      {
        Cfg_CellSubscriptionHandler subscription =
          activator.subscriptions.get(s);

        switch (subscription.subType)
        {
          case LOGIC:
            subscriptions[s] =
              new LogicSubscriptionSpec(subscription.cellName,
                                        subscription.transmitterName,
                                        subscription.receptorName,
                                        subscription.domain,
                                        subscription.filterTransactions);
            break;
          case MAP:
            subscriptions[s] =
              new LogicMapSubscriptionSpec(subscription.cellName,
                                           subscription.transmitterName,
                                           subscription.domain,
                                           subscription.filterTransactions);
            break;
          case LOOPBACK:
            subscriptions[s] =
              new LogicLoopbackSubscriptionSpec(subscription.transmitterName,
                                                subscription.receptorName);
            break;
          default:
            throw new NeuPathsException("Custom cells only accept logic-based subscriptions");
        }
      }
      
      try
      {
        // Load the activator class
        Class<?> actvtrClass = Class.forName(activator.className);

        if (PulsedActivator.class.isAssignableFrom(actvtrClass) &&
            receptors.length == 0 && subscriptions.length == 0)
        {
          activators[a] =
            (Activator) actvtrClass.getDeclaredConstructor
                            (String.class,
                             TransmitterSpec[].class).
                        newInstance
                            (activator.name,
                             transmitters);
        }
        else
        {
          activators[a] =
            (Activator) actvtrClass.getDeclaredConstructor
                            (String.class,
                             ReceptorSpec[].class,
                             TransmitterSpec[].class,
                             LogicSubscriptionSpec[].class).
                        newInstance
                            (activator.name,
                             receptors,
                             transmitters,
                             subscriptions);
        }
        
        activators[a].setTransactionHistoryWindow(activator.transactionHistoryWindow);
      }
      catch (ClassNotFoundException cnfe)
      {
        throw new NeuPathsException("Could not instantiate activator " + activator.name + " in file " + cellDefinitionFile, cnfe);
      }
      catch (NoSuchMethodException nsme)
      {
        throw new NeuPathsException("Could not instantiate activator " + activator.name + " in file " + cellDefinitionFile, nsme);
      }
      catch (InstantiationException ie)
      {
        throw new NeuPathsException("Could not instantiate activator " + activator.name + " in file " + cellDefinitionFile, ie);
      }
      catch (IllegalAccessException iae)
      {
        throw new NeuPathsException("Could not instantiate activator " + activator.name + " in file " + cellDefinitionFile, iae);
      }
      catch (java.lang.reflect.InvocationTargetException ite)
      {
        throw new NeuPathsException("Could not instantiate activator " + activator.name + " in file " + cellDefinitionFile, ite);
      }
    }

    PropertySet properties = new PropertySet();

    for (Cfg_CellPropertyHandler property : handler.properties)
    {
      properties.set(property.name, property.value);
    }
    
    try
    {
      // Load the custom class
      Class<?> customClass = Class.forName(ch.className);
  
      newCell = (Cell) customClass.getDeclaredConstructor
                           (String.class,
                            PropertySet.class,
                            String[].class,
                            Activator[].class,
                            byte[].class).
                       newInstance
                           (handler.name,
                            properties,
                            handler.synapseNames.toArray(String[]::new),
                            activators,
                            Cfg_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

      applyCellOptions(newCell);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new NeuPathsException("Could not instantiate cell in file " + cellDefinitionFile, cnfe);
    }
    catch (NoSuchMethodException nsme)
    {
      throw new NeuPathsException("Could not instantiate cell in file " + cellDefinitionFile, nsme);
    }
    catch (InstantiationException ie)
    {
      throw new NeuPathsException("Could not instantiate cell in file " + cellDefinitionFile, ie);
    }
    catch (IllegalAccessException iae)
    {
      throw new NeuPathsException("Could not instantiate cell in file " + cellDefinitionFile, iae);
    }
    catch (java.lang.reflect.InvocationTargetException ite)
    {
      throw new NeuPathsException("Could not instantiate cell in file " + cellDefinitionFile, ite);
    }

    return newCell;
  }
}
