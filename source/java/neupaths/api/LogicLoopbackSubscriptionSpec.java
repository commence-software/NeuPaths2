// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Loopback subscription specification for {@link LogicCell}s, {@link LoadBalancedCell}s and
 * {@link LoadControllerCell}s.  An activator can express interest in stimuli produced by its
 * own cell (either from itself or a different activator.)
 * <p>
 * A loopback subscription is composed of the following:
 * <ul>
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
 * </ul>
 * </p>
 * 
 * @author Aaron Caraveo
 */
public final class LogicLoopbackSubscriptionSpec extends LogicSubscriptionSpec
{
  /**
   * Creates a Loopback subscription specification.
   * 
   * @param transmitterName    The name of the transmitter that emitted the
   *                           stimulus.  This value may contain a regular
   *                           expression.
   * @param receptorName       The name of the receptor to receive the
   *                           stimulus.  This value <b>may not</b> contain
   *                           a regular expression.
   */
  public
  LogicLoopbackSubscriptionSpec
    (String  transmitterName,
     String  receptorName)
  {
    super(SubscriptionType.LOOPBACK,
          "N/A",
          transmitterName,
          receptorName,
          Syn.GLOBAL_DOMAIN,
          TransactionFilter.DISABLED);
  }
}
