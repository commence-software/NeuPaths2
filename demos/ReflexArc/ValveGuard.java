import neupaths.api.*;
import neupaths.stim.BooleanStimulus;

/**
 * The second stage of the reflex.  Joins an Anomaly (arriving over an
 * intra-cell LOOPBACK subscription from the BurstDetector) with the
 * valve's actual state.  Hysteresis falls out of the join: a command is
 * emitted only when the desired and actual positions differ, and the
 * loop rate is bounded by the valve's 1 Hz state heartbeat - the slowest
 * input sets the control-loop rate.
 */
public class ValveGuard extends Activator
{
  public ValveGuard ()
  {
    super("ValveGuard",
          new ReceptorSpec[] {
            new ReceptorSpec("Anomaly",
                             ReceptorMode.NON_BUFFERED,
                             AnomalyStim.TYPE_ID),
            new ReceptorSpec("ValveState",
                             ReceptorMode.NON_BUFFERED,
                             BooleanStimulus.TYPE_ID) },
          new TransmitterSpec[] {
            new TransmitterSpec("ValveCmd", ValveCmdStim.TYPE_ID) },
          new LogicSubscriptionSpec[] {
            new LogicLoopbackSubscriptionSpec("Anomaly",
                                              "Anomaly"),
            new LogicSubscriptionSpec(PlantState.prefix() + "Valve",
                                      "ValveState",
                                      "ValveState",
                                      PlantState.domain()) });
  }

  public void evaluate ()
  {
    AnomalyStim anomaly = getStimulus("Anomaly");
    BooleanStimulus valveOpen = getStimulus("ValveState");

    if (valveOpen.get())
    {
      logEvent(EventType.AUDIT1,
               "REFLEX: closing valve on " + anomaly);

      setStimulus("ValveCmd",
                  new ValveCmdStim(false,
                                   anomaly.kind + " severity=" + anomaly.severity,
                                   PlantState.prefix() + "Reflex"));
    }
  }
}
