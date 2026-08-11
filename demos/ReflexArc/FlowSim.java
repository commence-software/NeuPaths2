import neupaths.api.*;
import neupaths.stim.DoubleStimulus;

/**
 * Simulated flow sensor.  Each cell pulse (250 ms) samples the plant state
 * and emits a reading (m3/h).
 */
public class FlowSim extends PulsedActivator
{
  public FlowSim ()
  {
    super("FlowSim",
          new TransmitterSpec[] {
            new TransmitterSpec("Flow", DoubleStimulus.TYPE_ID) });
  }

  public void evaluate ()
  {
    boolean open = PlantState.valveOpen();
    boolean burst = PlantState.burstActive();

    double f = !open ? 0.0 : (burst ? 210.0 : 120.0);
    f += (rng.nextDouble() - 0.5) * 5.0;
    if (f < 0.0) f = 0.0;

    setStimulus("Flow", new DoubleStimulus(f));
  }

  private final java.util.Random rng = new java.util.Random();
}
