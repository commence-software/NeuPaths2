// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Synapse scope.
 *
 * @author Aaron Caraveo
 */
enum Syn_Scope
{
  NETWORK,
  LOCAL;
  
  @Override
  public
  String
  toString ()
  {
    String value = null;
    
    switch (this)
    {
      case NETWORK:
        value = "Network";
        break;
      case LOCAL:
        value = "Local";
        break;
      default:
        value = "Unknown";
    }
    
    return value;
  }
  
  static Syn_Scope translate (String name)
  {
    String upperCaseName = name.toUpperCase();
    return Syn_Scope.valueOf(upperCaseName);
  }
}
