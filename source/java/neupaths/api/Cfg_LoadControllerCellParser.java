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

final class Cfg_LoadControllerCellParser extends Cfg_CellDefinitionParser
{
  Cfg_LoadControllerCellParser (String cellDefinitionFile)
  {
    super(CellType.LOAD_CONTROLLER,
          cellDefinitionFile,
          new Cfg_LoadControllerCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    Cfg_LoadControllerCellHandler lch = (Cfg_LoadControllerCellHandler) handler;

    if (lch.cellNamePrefix == null)
    {
      throw new NeuPathsException("Load-controller cells require a cell name prefix");
    }
    
    if (lch.cellDomain == null)
    {
      throw new NeuPathsException("Load-controller cells require a cell domain");
    }
    
    for (Cfg_CellReceptorHandler receptor : lch.receptors)
    {
      if (receptor.stimulusClassName == null)
      {
        throw new NeuPathsException("Receptors must provide a stimulus class name");
      }
    }
    
    for (Cfg_CellSubscriptionHandler subscription : lch.subscriptions)
    {
      if (subscription.subType != SubscriptionType.LOGIC &&
          subscription.subType != SubscriptionType.MAP &&
          subscription.subType != SubscriptionType.LOOPBACK)
      {
        throw new NeuPathsException("Load-controller cells only accept logic-based subscriptions");
      }
    }
  }
  
  protected Cell instantiate ()
  {
    Cfg_LoadControllerCellHandler lch = (Cfg_LoadControllerCellHandler) handler;
    Cell newCell = null;

    ReceptorSpec[] receptors =
      new ReceptorSpec[lch.receptors.size()];

    for (int r = 0; r < lch.receptors.size(); r++)
    {
      Cfg_CellReceptorHandler receptor = lch.receptors.get(r);
      
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

    LogicSubscriptionSpec[] subscriptions =
      new LogicSubscriptionSpec[lch.subscriptions.size()];

    for (int s = 0; s < lch.subscriptions.size(); s++)
    {
      Cfg_CellSubscriptionHandler subscription = lch.subscriptions.get(s);

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
          throw new NeuPathsException("Load-controller cells only accept logic-based subscriptions");
      }
    }

    newCell = new LoadControllerCell
                      (handler.name,
                       handler.synapseNames.toArray(String[]::new),
                       receptors,
                       subscriptions,
                       lch.cellNamePrefix,
                       lch.cellDomain,
                       Cfg_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

    applyCellOptions(newCell);

    return newCell;
  }
}
