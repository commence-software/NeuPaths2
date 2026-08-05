// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Synapse types.
 *
 * @author Aaron Caraveo
 */
enum Syn_Type
{
  STREAM,
  UNICAST,
  MULTICAST;

  @Override
  public
  String
  toString ()
  {
    String value = null;
    
    switch (this)
    {
      case STREAM:
        value = "Stream";
        break;
      case UNICAST:
        value = "Unicast";
        break;
      case MULTICAST:
        value = "Multicast";
        break;
      default:
        value = "Unknown";
    }
    
    return value;
  }
  
  static Syn_Type translate (String name)
  {
    String upperCaseName = name.toUpperCase();
    return Syn_Type.valueOf(upperCaseName);
  }
}
