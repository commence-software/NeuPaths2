import neupaths.api.*;
import neupaths.stim.BooleanStimulus;

/**
 * Applies valve commands to the simulated plant.  Two subscriptions
 * aggregate onto the single Cmd receptor: the local reflex and the
 * operator console (whose commands cross the bridge from the Ops
 * domain).  Emits the resulting state immediately so the ValveGuard's
 * join sees fresh truth without waiting for the next heartbeat.
 */
public class ValveActuator extends Activator
{
  public ValveActuator ()
  {
    super("ValveActuator",
          new ReceptorSpec[] {
            new ReceptorSpec("Cmd",
                             ReceptorMode.NON_BUFFERED,
                             ValveCmdStim.TYPE_ID) },
          new TransmitterSpec[] {
            new TransmitterSpec("ValveState", BooleanStimulus.TYPE_ID) },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec(PlantState.prefix() + "Reflex",
                                      "ValveCmd",
                                      "Cmd",
                                      PlantState.domain()),
            new LogicSubscriptionSpec("OpsConsoleIn",
                                      "ValveCmd",
                                      "Cmd",
                                      PlantState.domain()) });
  }

  public void evaluate ()
  {
    ValveCmdStim cmd = getStimulus("Cmd");

    if (cmd.open != PlantState.valveOpen())
    {
      PlantState.setValveOpen(cmd.open);

      logEvent(EventType.AUDIT1,
               "VALVE " + (cmd.open ? "OPENED" : "CLOSED") +
               " by " + cmd.origin + ": " + cmd.reason);
    }

    setStimulus("ValveState", new BooleanStimulus(PlantState.valveOpen()));
  }
}
