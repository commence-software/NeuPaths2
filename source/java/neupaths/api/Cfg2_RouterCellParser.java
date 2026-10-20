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

final class Cfg2_RouterCellParser extends Cfg2_CellDefinitionParser
{
  Cfg2_RouterCellParser (String cellDefinitionFile)
  {
    super(CellType.ROUTER,
          cellDefinitionFile,
          new Cfg2_RouterCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg2_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    // nothing to validate
  }
  
  protected Cell instantiate ()
  {
    return
      new RouterCell(handler.name,
                     handler.synapseNames.toArray(String[]::new),
                     Cfg2_Utils.cryptoKeyBytes(handler.cryptoKeyFile));
  }
}
