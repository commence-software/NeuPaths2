import neupaths.api.*;
import neupaths.stim.BooleanStimulus;

/**
 * Heartbeats the valve's state on every cell pulse (1 s).  This is the
 * slow input of the ValveGuard's join, which bounds the reflex loop
 * rate - rate limiting by dataflow, no timers in the control logic.
 */
public class ValveHeartbeat extends PulsedActivator
{
  public ValveHeartbeat ()
  {
    super("ValveHeartbeat",
          new TransmitterSpec[] {
            new TransmitterSpec("ValveState", BooleanStimulus.TYPE_ID) });
  }

  public void evaluate ()
  {
    setStimulus("ValveState", new BooleanStimulus(PlantState.valveOpen()));
  }
}
