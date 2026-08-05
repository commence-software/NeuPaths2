// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Handler for parsing a {@link Cell} activator configuration.
 *
 * @author Aaron Caraveo
 */
final class Cfg_CellActivatorHandler extends Cfg_ConfigHandler
{
  Cfg_CellActivatorHandler (String xPath)
  {
    super(xPath);
    
    className = null;
    transactionHistoryWindow = 30_000L;
  }

  void processNode (String configFile, Node node)
  {
    String nodeName = node.getNodeName().toUpperCase();

    if (nodeName.equals("CLASS"))
    {
      className = Cfg_Utils.getNodeText(node);
    }
    else if (nodeName.equals("TRANSACTION_HISTORY_WINDOW_MS"))
    {
      transactionHistoryWindow = Long.parseLong(Cfg_Utils.getNodeText(node));
    }
    else
    {
      throw new NeuPathsException("Unexpected Cell/Activators/Activator configuration tag '" +
                                  nodeName + "' in file " + configFile);
    }
  }
  
  void processElement (String configFile, Cfg_ConfigHandler parentHandler)
  {
    Cfg_ActivatorHandlerInt handler = (Cfg_ActivatorHandlerInt) parentHandler;
    handler.addActivator(this);
  }
  
  String className;
  long transactionHistoryWindow;
}
