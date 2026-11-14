import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import neupaths.api.*;
import neupaths.stim.DoubleStimulus;
import neupaths.stim.StringStimulus;

/**
 * The "brain": long-horizon analytics in the Ops domain.  The producer
 * regexes cover every zone - Zone B, cloned later with a ZB_ prefix,
 * appears here with zero Ops-side configuration changes.
 * <p>
 * Those wildcards are what make a new zone free, but they also mean every
 * zone lands on the same two receptors.  Accumulating into one running
 * total would therefore report the mean of two independent plants, so the
 * zone is recovered from the producing cell's name and the averages are
 * kept apart.  A new zone then <em>adds</em> a trend line at Ops rather
 * than silently perturbing the existing one.
 */
public class TrendAnalyzer extends Activator
{
  public TrendAnalyzer ()
  {
    super("TrendAnalyzer",
          new ReceptorSpec[] {
            new ReceptorSpec("Pressure",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID),
            new ReceptorSpec("Flow",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID) },
          new TransmitterSpec[] {
            new TransmitterSpec("Trend", StringStimulus.TYPE_ID) },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec(".*_Pressure",
                                      "Pressure",
                                      "Pressure",
                                      "Ops"),
            new LogicSubscriptionSpec(".*_Flow",
                                      "Flow",
                                      "Flow",
                                      "Ops") });
  }

  public void evaluate ()
  {
    DoubleStimulus pressure = getStimulus("Pressure");
    DoubleStimulus flow = getStimulus("Flow");

    // Both receptors are NON_BUFFERED and both subscriptions are wildcarded,
    // so a single firing can pair Zone A's pressure with Zone B's flow.  Each
    // reading is credited to its own producer's zone, and counted separately,
    // because the two receptors need not be carrying the same zone.
    Zone zp = zoneFor(pressure);
    zp.pressureSum += pressure.get();
    zp.pressureSamples++;

    Zone zf = zoneFor(flow);
    zf.flowSum += flow.get();
    zf.flowSamples++;

    for (Map.Entry<String, Zone> entry : zones.entrySet())
    {
      Zone z = entry.getValue();

      // Roughly every 10 s at the 4 Hz join rate, per zone.  A zone whose
      // sensor has died stops trending rather than averaging what is left;
      // the Watchdog is what reports the sensor itself.
      if (z.pressureSamples >= SAMPLE_WINDOW && z.flowSamples >= SAMPLE_WINDOW)
      {
        String trend =
            String.format("TREND: %s avg pressure %.2f bar, avg flow %.1f m3/h over %d samples",
                          entry.getKey(),
                          z.pressureSum / z.pressureSamples,
                          z.flowSum / z.flowSamples,
                          z.pressureSamples);

        logEvent(EventType.INFORMATION, trend);
        setStimulus("Trend", new StringStimulus(trend));

        z.pressureSum = 0.0;
        z.pressureSamples = 0;
        z.flowSum = 0.0;
        z.flowSamples = 0;
      }
    }
  }

  private Zone zoneFor (Stimulus s)
  {
    return zones.computeIfAbsent(zoneOf(s.getProducerCellName()),
                                 k -> new Zone());
  }

  /**
   * Recovers the zone from a producing cell's name: "ZB_Pressure" yields
   * "Zone_B", mirroring {@code PlantState.prefix()} and
   * {@code PlantState.domain()}.  A producer that does not follow the
   * convention is bucketed under its own name, so it reports on its own
   * instead of being folded into some other zone's average.
   */
  private static String zoneOf (String cellName)
  {
    if (cellName == null) return "unattributed";

    Matcher m = ZONE_PREFIX.matcher(cellName);
    return m.find() ? "Zone_" + m.group(1) : cellName;
  }

  private static final class Zone
  {
    double pressureSum = 0.0;
    double flowSum = 0.0;
    int    pressureSamples = 0;
    int    flowSamples = 0;
  }

  /* Insertion-ordered so the zone that joined first also reports first. */
  private final Map<String, Zone> zones = new LinkedHashMap<>();

  private static final Pattern ZONE_PREFIX = Pattern.compile("^Z([^_]+)_");
  private static final int SAMPLE_WINDOW = 40;
}
