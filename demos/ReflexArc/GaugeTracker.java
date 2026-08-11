import neupaths.api.*;
import neupaths.stim.DoubleStimulus;

/**
 * Base class for the watchdog's per-sensor trackers.  Each tracker fires
 * on every reading from one sensor and records the arrival time in a
 * shared cell property; the Watchdog (a PulsedActivator in the same
 * cell) compares those timestamps against the clock.  Two activators
 * cooperating through cell properties - the dataflow gate alone cannot
 * detect the ABSENCE of a stimulus.
 */
public abstract class GaugeTracker extends Activator
{
  protected GaugeTracker (String sensor)
  {
    super(sensor + "Tracker",
          new ReceptorSpec[] {
            new ReceptorSpec("Reading",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID) },
          new TransmitterSpec[0],
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec(PlantState.prefix() + sensor,
                                      sensor,
                                      "Reading",
                                      PlantState.domain()) });

    this.sensor = sensor;
  }

  public void evaluate ()
  {
    getStimulus("Reading");  // consume

    setProperty("wd.last." + sensor, Long.valueOf(System.currentTimeMillis()));
  }

  private final String sensor;
}
