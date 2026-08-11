import neupaths.api.*;
import neupaths.stim.DoubleStimulus;

/**
 * The first stage of the reflex: a dataflow join of pressure and flow.
 * The gate fires only when a fresh reading from BOTH sensors coexists;
 * an Anomaly is emitted only on threshold breach.
 */
public class BurstDetector extends Activator
{
  public BurstDetector ()
  {
    super("BurstDetector",
          new ReceptorSpec[] {
            new ReceptorSpec("Pressure",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID),
            new ReceptorSpec("Flow",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID) },
          new TransmitterSpec[] {
            new TransmitterSpec("Anomaly", AnomalyStim.TYPE_ID) },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec(PlantState.prefix() + "Pressure",
                                      "Pressure",
                                      "Pressure",
                                      PlantState.domain()),
            new LogicSubscriptionSpec(PlantState.prefix() + "Flow",
                                      "Flow",
                                      "Flow",
                                      PlantState.domain()) });
  }

  public void evaluate ()
  {
    DoubleStimulus pressure = getStimulus("Pressure");
    DoubleStimulus flow = getStimulus("Flow");

    double p = pressure.get();
    double f = flow.get();

    if (p < 4.5 && f > 160.0)
    {
      double severity =
          Math.round(((4.5 - p) + (f - 160.0) / 50.0) * 100.0) / 100.0;

      setStimulus("Anomaly",
                  new AnomalyStim(severity,
                                  "PRESSURE_FLOW_DIVERGENCE",
                                  PlantState.domain()));
    }
  }
}
