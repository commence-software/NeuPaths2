// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * The run-time state of a cell nucleus.
 *
 * @author Aaron Caraveo
 */
enum Nuc_State
{
  OFFLINE,
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
      case ONLINE:
        value = "Online";
        break;
      default:
        value = "Unknown";
    }
    
    return value;
  }
}
