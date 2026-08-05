// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Stimulus used in the detection of cycles in the cellular system
 * paths.
 *
 * @author Aaron Caraveo
 */
final class Stim_CycleDetection extends Stimulus
{
  Stim_CycleDetection ()
  {
    super(TYPE_NAME, TYPE_ID);
    trace = null;
  }

  @Override
  public
  String toString ()
  {
    boolean first = true;
    String traceFormatted = "";
    String ti = "";

    for (Stim_Trace t : trace)
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
        traceFormatted += ti;
        first = false;
      }
      else
      {
        traceFormatted += " => " + ti;
      }
    }

    return TYPE_NAME + "[" + traceFormatted + "]";
  }
  
  ArrayList<Stim_Trace> trace;
  
  public static final String TYPE_NAME = "CdetDetection";
  public static final UUID TYPE_ID = UUID.fromString("da24d318-ce1d-4a90-ae7c-76785563ade0");
  
  private static final long serialVersionUID = -7853363809403960741L;
}
