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

final class Cfg2_InjectorCellParser extends Cfg2_CellDefinitionParser
{
  Cfg2_InjectorCellParser (String cellDefinitionFile)
  {
    super(CellType.INJECTOR,
          cellDefinitionFile,
          new Cfg2_InjectorCellHandler());
  }

  protected void parseNode (Node root)
  {
    Cfg2_Utils.processElement(cellDefinitionFile, null, handler, root);
  }

  protected void validate ()
  {
    Cfg2_InjectorCellHandler ih = (Cfg2_InjectorCellHandler) handler;

    if (ih.transmitter == null)
    {
      throw new NeuPathsException("Injector cells require a transmitter");
    }
  }
  
  protected Cell instantiate ()
  {
    Cfg2_InjectorCellHandler injector = (Cfg2_InjectorCellHandler) handler;
    Cell newCell = null;

    try
    {
      // Load the stimulus class
      Class<?> stimClass = Class.forName(injector.transmitter.stimulusClassName);
      UUID stimTypeID = (UUID) stimClass.getField("TYPE_ID").get(null);

      TransmitterSpec transmitter =
        new TransmitterSpec(injector.transmitter.name,
                            stimTypeID,  // need to load class
                            injector.transmitter.trace);

      newCell = new InjectorCell(handler.name,
                                 handler.synapseNames.toArray(String[]::new),
                                 transmitter,
                                 Cfg2_Utils.cryptoKeyBytes(handler.cryptoKeyFile));

      applyCellOptions(newCell);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new NeuPathsException("Could not resolve TYPE_ID for transmitter in file " + cellDefinitionFile, cnfe);
    }
    catch (IllegalAccessException iae)
    {
      throw new NeuPathsException("Could not resolve TYPE_ID for transmitter in file " + cellDefinitionFile, iae);
    }
    catch (NoSuchFieldException nsfe)
    {
      throw new NeuPathsException("Could not resolve TYPE_ID for transmitter in file " + cellDefinitionFile, nsfe);
    }

    return newCell;
  }
}
