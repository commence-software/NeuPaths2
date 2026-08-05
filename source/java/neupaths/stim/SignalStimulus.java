// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

/**
 * An alias for {@link BooleanStimulus} where {@code true} means <i>on</i> and
 * {@code false} means <i>off</i>.
 * 
 * @author Aaron Caraveo
 */
public class SignalStimulus extends BooleanStimulus
{
  /**
   * Allocates a new {@code SignalStimulus} object with an <i>off</i> value.
   */
  public
  SignalStimulus ()
  {
    super("SignalStimulus", true);
  }

  /**
   * Allocates a new {@code SignalStimulus} object with the specified
   * boolean value.
   * 
   * @param on {@code true} for <i>on</i>, {@code false} for <i>off</i>.
   */
  public
  SignalStimulus (boolean on)
  {
    super("SignalStimulus", on);
  }

  static final long serialVersionUID = -2045791185845576913L;
}
