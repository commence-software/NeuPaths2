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

final class Cfg2_ExtractorCellParser extends Cfg2_CellDefinitionParser
{
  Cfg2_ExtractorCellParser (String cellDefinitionFile)
  {
    super(CellType.EXTRACTOR,
          cellDefinitionFile,
          new Cfg2_ExtractorCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg2_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    Cfg2_ExtractorCellHandler extractor = (Cfg2_ExtractorCellHandler) handler;

    if (extractor.subscription == null)
    {
      throw new NeuPathsException("Extractor cells require a subscription");
    }

    if (extractor.subscription.subType != SubscriptionType.EXTRACTOR)
    {
      throw new NeuPathsException("Extractor cells require extractor subscriptions");
    }
  }
  
  protected Cell instantiate ()
  {
    Cfg2_ExtractorCellHandler extractor = (Cfg2_ExtractorCellHandler) handler;
    Cell newCell = null;

    ExtractorSubscriptionSpec subscription =
      new ExtractorSubscriptionSpec(extractor.subscription.cellName,
                                    extractor.subscription.transmitterName,
                                    extractor.subscription.domain,
                                    extractor.subscription.filterTransactions);

    newCell = new ExtractorCell(handler.name,
                                handler.synapseNames.toArray(String[]::new),
                                subscription,
                                Cfg2_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

    applyCellOptions(newCell);

    return newCell;
  }
}
