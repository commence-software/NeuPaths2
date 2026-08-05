// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * NeuPaths cell types.
 *
 * @see BridgeCell
 * @see EventCell
 * @see ExtractorCell
 * @see InjectorCell
 * @see LoadBalancedCell
 * @see LoadControllerCell
 * @see LogicCell
 * @see RouterCell
 *
 * @author Aaron Caraveo
 */
public enum CellType
{
  /**
   * A bridge cell.
   */
  BRIDGE,
  
  /**
   * An event cell.
   */
  EVENT,
  
  /**
   * An extractor cell.
   */
  EXTRACTOR,
  
  /**
   * An injector cell.
   */
  INJECTOR,

  /**
   * A load balanced cell.
   */
  LOAD_BALANCED,

  /**
   * A load controller cell.
   */
  LOAD_CONTROLLER,
  
  /**
   * A logic cell.
   */
  LOGIC,
  
  /**
   * A router cell.
   */
  ROUTER;
  
  @Override
  public
  String
  toString ()
  {
    String value = null;
    
    switch (this)
    {
      case BRIDGE:
        value = "Bridge";
        break;
      case EVENT:
        value = "Event";
        break;
      case EXTRACTOR:
        value = "Extractor";
        break;
      case INJECTOR:
        value = "Injector";
        break;
      case LOAD_BALANCED:
        value = "Load-Balanced";
        break;
      case LOAD_CONTROLLER:
        value = "Load-Controller";
        break;
      case LOGIC:
        value = "Logic";
        break;
      case ROUTER:
        value = "Router";
        break;
      default:
        value = "Unknown";
    }
    
    return value;
  }  
}
