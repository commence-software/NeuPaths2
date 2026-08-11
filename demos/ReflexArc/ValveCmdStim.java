import java.util.UUID;
import neupaths.api.Stimulus;

/**
 * A valve command.  Produced by the reflex (ValveGuard) or by the operator
 * console (OpsConsoleIn); both routes aggregate onto the valve's single
 * Cmd receptor.
 */
public final class ValveCmdStim extends Stimulus
{
  public
  ValveCmdStim (boolean open,
                String  reason,
                String  origin)
  {
    super(TYPE_NAME, TYPE_ID);
    this.open = open;
    this.reason = reason;
    this.origin = origin;
  }

  public String toString ()
  {
    return TYPE_NAME + "(" + (open ? "OPEN" : "CLOSE") + " by " + origin + ": " + reason + ")";
  }

  boolean open;
  String  reason;
  String  origin;

  public static final String TYPE_NAME = "ValveCmd";
  public static final UUID TYPE_ID = UUID.fromString("3e2eafcc-ed44-419d-bebc-122a0c72eea3");
}
