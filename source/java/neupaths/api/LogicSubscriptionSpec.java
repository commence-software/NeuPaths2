// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Subscription specification for {@link LogicCell}s, {@link LoadBalancedCell}s and
 * {@link LoadControllerCell}s.
 * <p>
 * A logic subscription is composed of the following:
 * <ul>
 * <li>
 * <i>Cell Name</i> - The name of the cell that created the
 *                    stimulus.  This value may contain a
 *                    regular expression.
 * </li>
 * <li>
 * <i>Transmitter Name</i> - The name of the transmitter that emitted
 *                           the stimulus.  This value may contain a
 *                           regular expression.
 * </li>
 * <li>
 * <i>Receptor Name</i> - The name of the receptor to receive the
 *                        stimulus.  This value <b>may not</b> contain
 *                        a regular expression.
 * </li>
 * <li>
 * <i>Domain</i> - The domain in which the stimulus will be processed.
 * </li>
 * <li>
 * <i>Filter Transactions</i> - For stimuli that are service responses, indicates
 *                              if redundant responses should be filtered.
 * </li>
 * </ul>
 * </p>
 * <p>
 * <i>Filter Transactions</i> specifies whether the logic cell should
 * filter redundant service responses for a given transaction ID.  This
 * is useful when multiple cells are servicing the same transaction.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public class LogicSubscriptionSpec extends SubscriptionSpec
{
  // This constructor is used by sub-classes
  LogicSubscriptionSpec
    (SubscriptionType  type,
     String            cellName,
     String            transmitterName,
     String            receptorName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    super(type,
          cellName,
          transmitterName,
          receptorName,
          domain,
          filterTransactions);
  }
    
  /**
   * Creates a Logic subscription specification.  {@code Filter Transactions}
   * defaults to {@link TransactionFilter#DISABLED}.
   * 
   * @param cellName           The name of the cell that created the
   *                           stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that emitted the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   */
  public
  LogicSubscriptionSpec
    (String            cellName,
     String            transmitterName,
     String            receptorName,
     String            domain)
  {
    this(SubscriptionType.LOGIC,
         cellName,
         transmitterName,
         receptorName,
         domain,
         TransactionFilter.DISABLED);
  }

  /**
   * Creates a Logic subscription specification.
   * 
   * @param cellName           The name of the cell that created the
   *                           stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that emitted the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   * @param filterTransactions For stimuli that are service responses, indicates
   *                           if redundant responses should be filtered.
   */
  public
  LogicSubscriptionSpec
    (String            cellName,
     String            transmitterName,
     String            receptorName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    this(SubscriptionType.LOGIC,
         cellName,
         transmitterName,
         receptorName,
         domain,
         filterTransactions);
  }
}
