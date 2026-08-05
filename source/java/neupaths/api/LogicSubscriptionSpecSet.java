// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Maintains a collection of {@link LogicSubscriptionSpec} objects.
 * This type is passed to {@link Activator} constructors.
 *
 * @author Aaron Caraveo
 */
final class LogicSubscriptionSpecSet extends SubscriptionSpecSet
{
  /**
   * Creates an empty subscription set.
   */
  public
  LogicSubscriptionSpecSet ()
  {
    // Relies on SubscriptionSpecSet default constructor
  }

  /**
   * Creates a subscription set with a single element.
   * 
   * @param subscription The subscription.
   */
  public
  LogicSubscriptionSpecSet (LogicSubscriptionSpec subscription)
  {
    super(subscription);
  }

  /**
   * Creates a subscription set from an array of subscription specifications.
   * 
   * @param subscriptions The array of subscription specifications.
   */
  public
  LogicSubscriptionSpecSet (LogicSubscriptionSpec[] subscriptions)
  {
    super(subscriptions);
  }

  /**
   * Creates a subscription set with a single logic subscription element.
   * 
   * @param cellName           The name of the cell or injector that created
   *                           the stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   * @param filterTransactions For stimuli that are service responses, filters
   *                           redundant responses.
   */
  public
  LogicSubscriptionSpecSet
    (String            cellName,
     String            transmitterName,
     String            receptorName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    add(new LogicSubscriptionSpec(cellName,
                                  transmitterName,
                                  receptorName,
                                  domain,
                                  filterTransactions));
  }

  /**
   * Creates a subscription set with a single loopback subscription element.
   * 
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   */
  public
  LogicSubscriptionSpecSet
    (String transmitterName,
     String receptorName)
  {
    add(new LogicLoopbackSubscriptionSpec(transmitterName,
                                          receptorName));
  }

  /**
   * Creates a subscription set with a single map subscription element.
   * 
   * @param cellName           The name of the cell or injector that created
   *                           the stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   * @param filterTransactions For stimuli that are service responses, filters
   *                           redundant responses.
   */
  public
  LogicSubscriptionSpecSet
    (String            cellName,
     String            transmitterName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    add(new LogicMapSubscriptionSpec(cellName,
                                     transmitterName,
                                     domain,
                                     filterTransactions));
  }

  /**
   * Adds a logic subscription to the set.
   * 
   * @param cellName           The name of the cell or injector that created
   *                           the stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   * @param filterTransactions For stimuli that are service responses, filters
   *                           redundant responses.
   */
  public
  void
  add
    (String            cellName,
     String            transmitterName,
     String            receptorName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    add(new LogicSubscriptionSpec(cellName,
                                  transmitterName,
                                  receptorName,
                                  domain,
                                  filterTransactions));
  }

  /**
   * Adds a loopback subscription to the set.
   * 
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   */
  public
  void
  add
    (String transmitterName,
     String receptorName)
  {
    add(new LogicLoopbackSubscriptionSpec(transmitterName,
                                          receptorName));
  }
  
  /**
   * Adds a map subscription to the set.
   * 
   * @param cellName           The name of the cell or injector that created
   *                           the stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that sent the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   * @param filterTransactions For stimuli that are service responses, filters
   *                           redundant responses.
   */
  public
  void
  add
    (String            cellName,
     String            transmitterName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    add(new LogicMapSubscriptionSpec(cellName,
                                     transmitterName,
                                     domain,
                                     filterTransactions));
  }
}
