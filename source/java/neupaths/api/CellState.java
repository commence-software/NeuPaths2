// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * NeuPaths runtime cell states.
 * 
 * @author Aaron Caraveo
 */
public enum CellState
{
  /**
   * The cell is not participating in the system.
   */
  OFFLINE,
  
  /**
   * The cell is participating but experiencing issues.
   */
  DEGRADED,

  /**
   *
   */
  PAUSED,

  /**
   * The cell is participating in the system and healthy.
   */
  ONLINE;

  @Override
  public
  String
  toString ()
  {
    String value = null;
    
    switch (this)
    {
      case OFFLINE:
        value = "Offline";
        break;
      case DEGRADED:
        value = "Degraded";
        break;
      case PAUSED:
        value = "Paused";
        break;
      case ONLINE:
        value = "Online";
        break;
      default:
        value = "Unknown";
    }
    
    return value;
  }
}
