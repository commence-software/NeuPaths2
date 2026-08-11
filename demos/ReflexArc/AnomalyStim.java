import java.util.UUID;
import neupaths.api.Stimulus;

/**
 * Emitted by the BurstDetector when pressure and flow diverge.
 */
public final class AnomalyStim extends Stimulus
{
  public
  AnomalyStim (double severity,
               String kind,
               String zone)
  {
    super(TYPE_NAME, TYPE_ID);
    this.severity = severity;
    this.kind = kind;
    this.zone = zone;
    this.atMs = System.currentTimeMillis();
  }

  public String toString ()
  {
    return TYPE_NAME + "(" + zone + " " + kind + " severity=" + severity + ")";
  }

  double severity;
  String kind;
  String zone;
  long   atMs;

  public static final String TYPE_NAME = "Anomaly";
  public static final UUID TYPE_ID = UUID.fromString("34d0e3ad-2da4-42d6-a8c4-cf0cc386e17f");
}
