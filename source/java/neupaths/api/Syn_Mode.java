// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Synapse modes.
 *
 * @author Aaron Caraveo
 */
enum Syn_Mode
{
  PEER,
  LISTENER;
  
  @Override
  public
  String
  toString ()
  {
    String value = null;
    
    switch (this)
    {
      case PEER:
        value = "Peer";
        break;
      case LISTENER:
        value = "Listener";
        break;
      default:
        value = "Unknown";
        break;
    }
    
    return value;
  }

  static Syn_Mode translate (String mode)
  {
    String upperCaseMode = mode.toUpperCase();
    return Syn_Mode.valueOf(upperCaseMode);
  }
}

