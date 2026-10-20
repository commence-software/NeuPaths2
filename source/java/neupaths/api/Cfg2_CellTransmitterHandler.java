// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell} transmitter.
 *
 * @author Aaron Caraveo
 */
final class Cfg2_CellTransmitterHandler extends Cfg2_ConfigHandler
{
  Cfg2_CellTransmitterHandler (CellType cellType, String xPath)
  {
    super(cellType, xPath);
    
    name = null;
    stimulusClassName = null;
    trace = StimulusTrace.ENABLED;
  }

  void processNode (String definitionFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("NAME"))
    {
      name = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("STIMULUS_CLASS"))
    {
      stimulusClassName = Cfg2_Utils.getNodeText(node);
    }
    else if (nodeName.equals("TRACE"))
    {
      try
      {
        trace = StimulusTrace.valueOf(Cfg2_Utils.getNodeText(node).toUpperCase());
      }
      catch (IllegalArgumentException iae)
      {
        throw new NeuPathsException("Illegal value for " + xPath + "/Trace '" +
                                    Cfg2_Utils.getNodeText(node) + "' in file " + definitionFile);
      }
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                     nodeName + "' in file " + definitionFile);
    }
  }
  
  void processElement (String             definitionFile,
                       Cfg2_ConfigHandler parentHandler)
  {
    Cfg2_TransmittersHandlerInt handler = (Cfg2_TransmittersHandlerInt) parentHandler;
    handler.addTransmitter(this);
  }

  String name;
  String stimulusClassName;
  StimulusTrace trace;
}
