// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import org.w3c.dom.Node;

/**
 * Base class for XML DOM parsing.
 *
 * @author Aaron Caraveo
 */
abstract class Cfg_ConfigHandler
{
  Cfg_ConfigHandler (CellType cellType, String xPath)
  {
    this.cellType = cellType;
    this.xPath = xPath;
  }
  
  void processNode (String definitionFile, Node node) {}
  
  void processElement (String             definitionFile,
                       Cfg_ConfigHandler parentHandler) {}

  CellType cellType;
  String xPath;
}
