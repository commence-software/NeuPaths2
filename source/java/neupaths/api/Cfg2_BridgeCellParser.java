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

final class Cfg2_BridgeCellParser extends Cfg2_CellDefinitionParser
{
  Cfg2_BridgeCellParser (String cellDefinitionFile)
  {
    super(CellType.BRIDGE,
          cellDefinitionFile,
          new Cfg2_BridgeCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg2_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    Cfg2_BridgeCellHandler bridge = (Cfg2_BridgeCellHandler) handler;
    
    if (bridge.subscriptions.size() == 0)
    {
      throw new NeuPathsException("Bridge cells require at least one subscription");
    }

    for (Cfg2_CellSubscriptionHandler subscription : bridge.subscriptions)
    {
      if (subscription.subType != SubscriptionType.BRIDGE)
      {
        throw new NeuPathsException("Bridge cells require bridge subscriptions");
      }
    }
  }
  
  protected Cell instantiate ()
  {
    Cfg2_BridgeCellHandler bridge = (Cfg2_BridgeCellHandler) handler;
    Cell newCell = null;
    
    BridgeSubscriptionSpec[] subscriptions =
      new BridgeSubscriptionSpec[bridge.subscriptions.size()];

    for (int i = 0; i < bridge.subscriptions.size(); i++)
    {
      Cfg2_CellSubscriptionHandler subscription = bridge.subscriptions.get(i);
      
      subscriptions[i] = new BridgeSubscriptionSpec(subscription.cellName,
                                                    subscription.transmitterName,
                                                    subscription.domain);
    }
    
    newCell = new BridgeCell(handler.name,
                             handler.synapseNames.toArray(String[]::new),
                             subscriptions,
                             Cfg2_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

    applyCellOptions(newCell);

    return newCell;
  }
}
