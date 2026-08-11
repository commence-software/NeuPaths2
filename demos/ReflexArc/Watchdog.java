import java.util.HashSet;
import neupaths.api.*;
import neupaths.stim.StringStimulus;

/**
 * Sensor-liveness watchdog (see documents/design/07-case-study-reflex-arc.md
 * section 7.4).
 * <p>
 * A dataflow gate cannot fire on the ABSENCE of a stimulus - a dead
 * sensor silently stalls every join it feeds.  This activator closes
 * that gap: the per-sensor GaugeTrackers stamp arrival times into
 * shared cell properties, and this PulsedActivator compares them
 * against the clock on every cell pulse (1 s).  A sensor more than
 * STALE_MS old raises a WARNING event and a SensorAlarm stimulus;
 * recovery is announced the same way.
 * </p>
 * <p>
 * A sensor that has never produced a reading is deliberately NOT
 * alarmed - otherwise the hot-pluggable acoustic sensor would page
 * from the moment the system starts.
 * </p>
 */
public class Watchdog extends PulsedActivator
{
  public Watchdog ()
  {
    super("Watchdog",
          new TransmitterSpec[] {
            new TransmitterSpec("SensorAlarm", StringStimulus.TYPE_ID) });
  }

  public void evaluate ()
  {
    long now = System.currentTimeMillis();

    for (String sensor : SENSORS)
    {
      Long last = getProperty("wd.last." + sensor);

      if (last == null)
      {
        continue;  // never seen (e.g. acoustic before hot-plug)
      }

      boolean stale = (now - last.longValue()) > STALE_MS;
      String cellName = PlantState.prefix() + sensor;

      if (stale && !down.contains(sensor))
      {
        down.add(sensor);
        logEvent(EventType.WARNING,
                 "WATCHDOG: sensor " + cellName + " is DOWN (last reading " +
                 (now - last.longValue()) + " ms ago)");
        setStimulus("SensorAlarm",
                    new StringStimulus("SENSOR DOWN: " + cellName));
      }
      else if (!stale && down.contains(sensor))
      {
        down.remove(sensor);
        logEvent(EventType.INFORMATION,
                 "WATCHDOG: sensor " + cellName + " RECOVERED");
        setStimulus("SensorAlarm",
                    new StringStimulus("SENSOR RECOVERED: " + cellName));
      }
    }
  }

  private static final String[] SENSORS = { "Pressure", "Flow", "Acoustic" };
  private static final long STALE_MS = 2000L;   // sensors pulse at 250 ms

  private final HashSet<String> down = new HashSet<>();
}
