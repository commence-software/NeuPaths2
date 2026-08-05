// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedList;

/**
 * Keeps track of cycles detected in a Cellular System.
 *
 * @author Aaron Caraveo
 */
final class Actv_CycleDetected extends Activator
{
  Actv_CycleDetected (String name)
  {
    super(name,
          new ReceptorSpec[] {
              new ReceptorSpec("CycleDetected",
                               ReceptorMode.BUFFERED,
                               Stim_CycleDetected.TYPE_ID) },
          new TransmitterSpec[] {},
          new LogicSubscriptionSpec[] {
              new LogicSubscriptionSpec(".*",
                                        Cdet.CDET_DETECTED_TRANSMITTER,
                                        "CycleDetected",
                                        Syn.GLOBAL_DOMAIN,
                                        TransactionFilter.DISABLED) });

    cycles = new LinkedList<>();
  }
  
  Actv_CycleDetected ()
  {
    this("CdetDetectedActivator");
  }
  
  @Override
  protected void evaluate ()
  {
    Stim_CycleDetected cdet = getStimulus("CycleDetected");

    cycles.addLast(cdet.trace);
  }
  
  LinkedList<String> cycles;
}
