// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Maintains a collection of {@link BridgeSubscriptionSpec} objects.
 *
 * @author Aaron Caraveo
 */
final class BridgeSubscriptionSpecSet extends SubscriptionSpecSet
{
  /**
   * Creates an empty subscription set.
   */
  public
  BridgeSubscriptionSpecSet ()
  {
    // Relies on SubscriptionSpecSet default constructor
  }

  /**
   * Creates a subscription set with a single element.
   * 
   * @param subscription The subscription.
   */
  public
  BridgeSubscriptionSpecSet (BridgeSubscriptionSpec subscription)
  {
    super(subscription);
  }

  /**
   * Creates a subscription set from an array of subscription specifications.
   * 
   * @param subscriptions The array of subscription specifications.
   */
  public
  BridgeSubscriptionSpecSet (BridgeSubscriptionSpec[] subscriptions)
  {
    super(subscriptions);
  }

  /**
   * Creates a subscription set with a single bridge subscription element.
   * 
   * @param cellName           The name of the cell or injector that created
   *                           the stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   */
  public
  BridgeSubscriptionSpecSet
    (String cellName,
     String transmitterName,
     String domain)
  {
    add(new BridgeSubscriptionSpec(cellName,
                                   transmitterName,
                                   domain));
  }

  /**
   * Adds a bridge subscription to the set.
   * 
   * @param cellName           The name of the cell or injector that created
   *                           the stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   */
  public
  void
  add
    (String            cellName,
     String            transmitterName,
     String            domain)
  {
    add(new BridgeSubscriptionSpec(cellName,
                                   transmitterName,
                                   domain));
  }

}
