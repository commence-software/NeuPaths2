// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Subscription types used by NeuPaths cells.
 *
 * @see BridgeSubscriptionSpec
 * @see ExtractorSubscriptionSpec
 * @see LogicSubscriptionSpec
 * @see LogicLoopbackSubscriptionSpec
 * @see LogicMapSubscriptionSpec
 *
 * @author Aaron Caraveo
 */
public enum SubscriptionType
{
  /**
   * A logic subscription.  See {@link LogicSubscriptionSpec} for details.
   */
  LOGIC,
  
  /**
   * A logic loopback subscription.  See {@link LogicLoopbackSubscriptionSpec}
   * for details.
   */
  LOOPBACK,
  
  /**
   * A logic map subscription.  See {@link LogicMapSubscriptionSpec} for
   * details.
   */
  MAP,
  
  /**
   * A bridge subscription.  See {@link BridgeSubscriptionSpec} for details.
   */
  BRIDGE,
  
  /**
   * An extractor subscription.  See {@link ExtractorSubscriptionSpec} for
   * details.
   */
  EXTRACTOR;
  
  @Override
  public
  String
  toString ()
  {
    String value = null;
    
    switch (this)
    {
      case LOGIC:
        value = "Logic";
        break;
      case LOOPBACK:
        value = "Loopback";
        break;
      case MAP:
        value = "Map";
        break;
      case BRIDGE:
        value = "Bridge";
        break;
      case EXTRACTOR:
        value = "Extractor";
        break;
      default:
        value = "Unknown";
    }
    
    return value;
  }  
}
