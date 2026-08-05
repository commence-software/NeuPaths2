// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Subscription specification for {@link BridgeCell}s.  Bridge subscriptions
 * are used to pull stimuli to a bridge cell from a particular domain.  They
 * are composed of the following:
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
 * </ul>
 * <p>
 * Subscriptions from other domains will pull the stimuli away from the
 * bridge cell.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public final class BridgeSubscriptionSpec extends SubscriptionSpec
{
  /**
   * Creates a Bridge subscription specification.
   * 
   * @param cellName        The name of the cell that created the
   *                        stimulus.  This value may contain a
   *                        regular expression.
   * @param transmitterName The name of the transmitter that emitted the
   *                        stimulus.  This value may contain a regular
   *                        expression.
   * @param domain          The domain in which the stimulus will be
   *                        processed.
   */
  public
  BridgeSubscriptionSpec
    (String cellName,
     String transmitterName,
     String domain)
  {
    super(SubscriptionType.BRIDGE,
          cellName,
          transmitterName,
          "N/A",
          domain,
          TransactionFilter.DISABLED);
  }  
}
