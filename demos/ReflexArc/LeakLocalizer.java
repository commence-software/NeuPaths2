import neupaths.api.*;
import neupaths.stim.DoubleStimulus;
import neupaths.stim.StringStimulus;

/**
 * Capability gating: this activator is configured from day one, but its
 * three-way join cannot complete until the acoustic sensor cell exists.
 * Hot-plugging AcousticSim mid-demo turns this feature on with no
 * reconfiguration anywhere.
 */
public class LeakLocalizer extends Activator
{
  public LeakLocalizer ()
  {
    super("LeakLocalizer",
          new ReceptorSpec[] {
            new ReceptorSpec("Pressure",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID),
            new ReceptorSpec("Flow",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID),
            new ReceptorSpec("Acoustic",
                             ReceptorMode.NON_BUFFERED,
                             DoubleStimulus.TYPE_ID) },
          new TransmitterSpec[] {
            new TransmitterSpec("Leak", StringStimulus.TYPE_ID) },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec(PlantState.prefix() + "Pressure",
                                      "Pressure",
                                      "Pressure",
                                      PlantState.domain()),
            new LogicSubscriptionSpec(PlantState.prefix() + "Flow",
                                      "Flow",
                                      "Flow",
                                      PlantState.domain()),
            new LogicSubscriptionSpec(PlantState.prefix() + "Acoustic",
                                      "Acoustic",
                                      "Acoustic",
                                      PlantState.domain()) });
  }

  public void evaluate ()
  {
    DoubleStimulus acoustic = getStimulus("Acoustic");

    double a = acoustic.get();

    // Emit at most every 5 s while a leak signature is present
    if (a > 0.5 && System.currentTimeMillis() - lastEmitMs > 5000L)
    {
      lastEmitMs = System.currentTimeMillis();

      setStimulus("Leak",
                  new StringStimulus(
                      String.format("%s: leak signature %.2f - est. offset %dm from station",
                                    PlantState.domain(), a, (int) (a * 120.0))));
    }
  }

  private long lastEmitMs = 0L;
}
