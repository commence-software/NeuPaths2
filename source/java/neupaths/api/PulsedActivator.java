// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.stim.DateStimulus;
import neupaths.util.PropertySet;

/**
 * The base class for NeuPaths pulsed activators.
 * <p>
 * Pulses arrive on the <b>Pulse</b> receptor as a {@link neupaths.stim.DateStimulus}.
 * The pulse's timestamp can be retrieved from the stimulus in the {@link Activator#evaluate}
 * method:
 * <ul>
 * {@literal DateStimulus pulse = getStimulus("Pulse");}<br>
 * {@literal Date timestamp = pulse.get();}
 * </ul>
 * </p>
 *
 * @author Aaron Caraveo
 */
public abstract class PulsedActivator extends Activator
{
  /**
   * Creates a new {@code PulsedActivator} object.
   * 
   * @param name            This activator's name at runtime.
   * @param receptors       The receptors this activator receives stimuli on.
   * @param transmitters    The transmitters this activator emits stimuli on.
   *                        If the transmitter array is empty (null or empty
   *                        array), the activator does not emit stimuli.
   * @param subscriptions   The subscriptions this activator advertises.
   */
  protected
  PulsedActivator
    (String                  name,
     ReceptorSpec[]          receptors,
     TransmitterSpec[]       transmitters,
     LogicSubscriptionSpec[] subscriptions)
  {
    super(name, receptors, transmitters, subscriptions);

    addReceptor(new ReceptorSpec("Pulse",
                                 ReceptorMode.NON_BUFFERED,
                                 DateStimulus.TYPE_ID));

    addSubscription(new LogicLoopbackSubscriptionSpec(Cell.PULSE_TRANSMITTER,
                                                      "Pulse"));
  }
  
  /**
   * Creates a new {@code PulsedActivator} object.
   * 
   * @param name            This activator's name at runtime.
   * @param transmitters    The transmitters this activator emits stimuli on.
   *                        If the transmitter array is empty (null or empty
   *                        array), the activator does not emit stimuli.
   */
  protected
  PulsedActivator
    (String             name,
     TransmitterSpec[]  transmitters)
  {
    super(name,
          new ReceptorSpec[] {
              new ReceptorSpec("Pulse",
                               ReceptorMode.NON_BUFFERED,
                               DateStimulus.TYPE_ID) },
          transmitters,
          new LogicLoopbackSubscriptionSpec[] {
              new LogicLoopbackSubscriptionSpec(Cell.PULSE_TRANSMITTER,
                                                "Pulse") });
  }
}
