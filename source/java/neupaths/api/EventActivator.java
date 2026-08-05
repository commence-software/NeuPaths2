// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * The base class for NeuPaths event stimuli activators.
 * 
 * @author Aaron Caraveo
 */
public abstract class EventActivator extends Activator
{
  /**
   * Creates a new {@code EventActivator} object.
   * 
   * @param domain The domain in which to subscribe for the events.
   */
  public
  EventActivator (String domain)
  {
    super("EventActivator",
          new ReceptorSpec[] {
              new ReceptorSpec("LogEvent",
                               ReceptorMode.BUFFERED,
                               EventStimulus.TYPE_ID) },
          new TransmitterSpec[] {},
          new LogicSubscriptionSpec[] {
              new LogicSubscriptionSpec(".*",
                                        Evt.EVENT_TRANSMITTER,
                                        "LogEvent",
                                        domain,
                                        TransactionFilter.DISABLED) });
  }
  
  /**
   * Processes an event stimulus.
   * 
   * @param event The event stimulus.
   */
  protected abstract void processEvent (EventStimulus event);

  /**
   * Overrides {@link Activator#evaluate} to process the Event stimulus.
   * Users must provide an implementation for {@code processEvent}.
   */
  @Override
  protected final void evaluate ()
  {
    EventStimulus event = getStimulus("LogEvent");
    processEvent(event);
  }
}
