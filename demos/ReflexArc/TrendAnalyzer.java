import neupaths.api.*;
import neupaths.stim.DoubleStimulus;
import neupaths.stim.StringStimulus;

/**
 * The "brain": long-horizon analytics in the Ops domain.  The producer
 * regexes cover every zone - Zone B, cloned later with a ZB_ prefix,
 * appears here with zero Ops-side configuration changes.
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

    pSum += pressure.get();
    fSum += flow.get();
    samples++;

    if (samples >= 40)   // roughly every 10 s at the 4 Hz join rate
    {
      String trend =
          String.format("TREND: avg pressure %.2f bar, avg flow %.1f m3/h over %d samples",
                        pSum / samples, fSum / samples, samples);

      logEvent(EventType.INFORMATION, trend);
      setStimulus("Trend", new StringStimulus(trend));

      pSum = 0.0;
      fSum = 0.0;
      samples = 0;
    }
  }

  private double pSum = 0.0;
  private double fSum = 0.0;
  private int samples = 0;
}
