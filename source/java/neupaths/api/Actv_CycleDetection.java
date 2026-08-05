// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Activator used by the {@link CycleDetectorTool} to identify circular
 * paths in the cellular system.
 *
 * @author Aaron Caraveo
 */
final class Actv_CycleDetection extends Activator
{
  Actv_CycleDetection (String name)
  {
    super(name,
          new ReceptorSpec[] {
              new ReceptorSpec("CycleDetection",
                               ReceptorMode.BUFFERED,
                               Stim_CycleDetection.TYPE_ID) },
          new TransmitterSpec[] {
              new TransmitterSpec(Cdet.CDET_DETECTED_TRANSMITTER,
                                  Stim_CycleDetected.TYPE_ID,
                                  StimulusTrace.ENABLED) },
          new LogicSubscriptionSpec[] {
              new LogicSubscriptionSpec(".*",
                                        Cdet.CDET_DETECTION_TRANSMITTER,
                                        "CycleDetection",
                                        Syn.GLOBAL_DOMAIN,
                                        TransactionFilter.DISABLED) });
  }
  
  Actv_CycleDetection ()
  {
    this("CdetDetectionActivator");
  }
  
  @Override
  protected void evaluate ()
  {
    Stim_CycleDetection cdet = getStimulus("CycleDetection");
    
    setStimulus(Cdet.CDET_DETECTED_TRANSMITTER, new Stim_CycleDetected(cdet));
  }
}
