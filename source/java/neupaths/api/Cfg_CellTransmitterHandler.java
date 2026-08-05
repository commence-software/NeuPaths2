// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;
import org.w3c.dom.Node;

/**
 * Handler for parsing an {@link InjectorCell}'s transmitter.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellTransmitterHandler extends Cfg_ConfigHandler
{
  Cfg_CellTransmitterHandler (String xPath)
  {
    super(xPath);
    
    name = null;
    stimulusClassName = null;
    trace = StimulusTrace.ENABLED;
  }

  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();
    
    if (nodeName.equals("NAME"))
    {
      name = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("STIMULUS_CLASS"))
    {
      stimulusClassName = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("TRACE"))
    {
      try
      {
        trace = StimulusTrace.valueOf(Cfg_Utils.getNodeText(node).toUpperCase());
      }
      catch (IllegalArgumentException iae)
      {
        throw new NeuPathsException("Illegal value for " + xPath + "/Trace '" +
                                    Cfg_Utils.getNodeText(node) + "' in file " + configFile);
      }
    }
    else
    {
      throw new NeuPathsException("Unexpected " + xPath + " configuration tag '" +
                                     nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_TransmitterHandlerInt handler = (Cfg_TransmitterHandlerInt) parentHandler;

    try
    {
      // Load the stimulus class
      Class<?> stimClass = Class.forName(stimulusClassName);
      UUID stimTypeID = (UUID) stimClass.getField("TYPE_ID").get(null);

      handler.addTransmitter(
          new TransmitterSpec(name,
                              stimTypeID,  // need to load class
                              trace));
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new NeuPathsException("Could not resolve TYPE_ID for transmitter in file " + configFile, cnfe);
    }
    catch (IllegalAccessException iae)
    {
      throw new NeuPathsException("Could not resolve TYPE_ID for transmitter in file " + configFile, iae);
    }
    catch (NoSuchFieldException nsfe)
    {
      throw new NeuPathsException("Could not resolve TYPE_ID for transmitter in file " + configFile, nsfe);
    }
  }

  String name;
  String stimulusClassName;
  StimulusTrace trace;
}
