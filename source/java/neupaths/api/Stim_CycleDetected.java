// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Stimulus used when a cycle is detected in the cellular system
 * paths.
 *
 * @author Aaron Caraveo
 */
final class Stim_CycleDetected extends Stimulus
{
  Stim_CycleDetected ()
  {
    super(TYPE_NAME, TYPE_ID);
    trace = "";
  }

  Stim_CycleDetected (Stim_CycleDetection s)
  {
    super(TYPE_NAME, TYPE_ID);

    boolean first = true;
    trace = "";
    String ti = "";
    
    for (Stim_Trace t : s.trace)
    {
      if (t.domainName == null)
      {
        ti = t.cellName;
      }
      else
      {
        ti = t.cellName + " (" + t.domainName + ")";
      }

      if (first)
      {
        trace += ti;
        first = false;
      }
      else
      {
        trace += " => " + ti;
      }
    }
  }
  
  @Override
  public
  String toString ()
  {
    return TYPE_NAME + "[" + trace + "]";
  }
  
  String trace;
  
  public static final String TYPE_NAME = "CdetDetected";
  public static final UUID TYPE_ID = UUID.fromString("743acc68-66e5-474d-970e-912c5a19406b");
  
  private static final long serialVersionUID = -200101538989820493L;
}
