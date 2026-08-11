import neupaths.api.*;
import neupaths.stim.DoubleStimulus;

/**
 * Simulated pressure sensor.  Each cell pulse (250 ms) samples the plant
 * state and emits a reading (bar).
 */
public class PressureSim extends PulsedActivator
{
  public PressureSim ()
  {
    super("PressureSim",
          new TransmitterSpec[] {
            new TransmitterSpec("Pressure", DoubleStimulus.TYPE_ID) });
  }

  public void evaluate ()
  {
    boolean open = PlantState.valveOpen();
    boolean burst = PlantState.burstActive();

    double p = !open ? 6.5 : (burst ? 2.5 : 6.0);
    p += (rng.nextDouble() - 0.5) * 0.1;

    setStimulus("Pressure", new DoubleStimulus(p));
  }

  private final java.util.Random rng = new java.util.Random();
}
