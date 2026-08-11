import neupaths.api.*;
import neupaths.stim.DoubleStimulus;

/**
 * Simulated acoustic (leak-noise) sensor.  Deliberately not started by
 * run.sh - hot-plugging this cell mid-demo is what lights up the
 * LeakLocalizer capability.
 */
public class AcousticSim extends PulsedActivator
{
  public AcousticSim ()
  {
    super("AcousticSim",
          new TransmitterSpec[] {
            new TransmitterSpec("Acoustic", DoubleStimulus.TYPE_ID) });
  }

  public void evaluate ()
  {
    boolean open = PlantState.valveOpen();
    boolean burst = PlantState.burstActive();

    double a = (burst && open) ? 0.85 : 0.05;
    a += (rng.nextDouble() - 0.5) * 0.04;
    if (a < 0.0) a = 0.0;

    setStimulus("Acoustic", new DoubleStimulus(a));
  }

  private final java.util.Random rng = new java.util.Random();
}
