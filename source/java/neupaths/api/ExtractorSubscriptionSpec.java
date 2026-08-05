// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Subscription specification for {@link ExtractorCell}s.  Extractor
 * subscriptions are used to pull stimuli to an extractor cell.  They are
 * compose of the following:
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
 * <i>Domain</i> - The domain in which the stimulus will be processed.
 * </li>
 * <li>
 * <i>Filter Transactions</i> - For stimuli that are service responses, indicates
 *                              if redundant responses should be filtered.
 * </li>
 * </ul>
 * <p>
 * An extractor cell is essentially a receptor used to extract data from a cell
 * system, hence the omission of <i>Receptor Name</i>.
 * <p>
 * <i>Filter Transactions</i> specifies whether the extractor cell should
 * filter redundant service responses for a given transaction ID.  This
 * is useful when multiple cells are servicing the same transaction.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public final class ExtractorSubscriptionSpec extends SubscriptionSpec
{
  /**
   * Creates an Extractor subscription specification.
   * {@code Filter Transactions} defaults to {@link TransactionFilter#DISABLED}.
   * 
   * @param cellName           The name of the cell that created the
   *                           stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that emitted the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   */
  public
  ExtractorSubscriptionSpec
    (String            cellName,
     String            transmitterName,
     String            domain)
  {
    super(SubscriptionType.EXTRACTOR,
          cellName,
          transmitterName,
          "N/A",
          domain,
          TransactionFilter.DISABLED);
  }  

  /**
   * Creates an Extractor subscription specification.
   * 
   * @param cellName           The name of the cell that created the
   *                           stimulus.  This value may contain a
   *                           regular expression.
   * @param transmitterName    The name of the transmitter that emitted the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param domain             The domain in which the stimulus will be
   *                           processed.
   * @param filterTransactions For stimuli that are service responses, filters
   *                           redundant responses.
   */
  public
  ExtractorSubscriptionSpec
    (String            cellName,
     String            transmitterName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    super(SubscriptionType.EXTRACTOR,
          cellName,
          transmitterName,
          "N/A",
          domain,
          filterTransactions);
  }  
}
