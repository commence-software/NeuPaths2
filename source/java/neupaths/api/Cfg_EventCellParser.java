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

final class Cfg_EventCellParser extends Cfg_CellDefinitionParser
{
  Cfg_EventCellParser (String cellDefinitionFile)
  {
    super(CellType.EVENT,
          cellDefinitionFile,
          new Cfg_EventCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    if (handler.synapseNames.size() != 1)
    {
      throw new NeuPathsException("Event cells require a single synapse");
    }

    Cfg_EventCellHandler event = (Cfg_EventCellHandler) handler;
    
    if (event.outputFileName == null)
    {
      throw new NeuPathsException("Event cells require an output file");
    }
  }
  
  protected Cell instantiate ()
  {
    Cfg_EventCellHandler event = (Cfg_EventCellHandler) handler;

    Cell newCell =  new EventCell(handler.name,
                                  handler.synapseNames.toArray(String[]::new)[0],
                                  event.outputFileName,
                                  Cfg_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

    applyCellOptions(newCell);

    return newCell;
  }
}
