import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared simulated plant state, zone-aware.
 * <p>
 * State is kept in tiny files under /tmp/reflex/zone&lt;ID&gt;/ so that the
 * sensor, valve and reflex cells can run as independent, individually
 * killable JVM processes while observing the same simulated physics:
 * <ul>
 * <li><b>valve.pos</b> - "OPEN" or "CLOSED" (missing =&gt; OPEN)</li>
 * <li><b>burst</b>     - a pipe burst is active while this file exists
 *                        (created/removed by chaos.sh)</li>
 * </ul>
 * The zone is selected with the system property {@code reflex.zone}
 * (default "A"), so the same classes serve Zone B clones unchanged.
 */
public final class PlantState
{
  private PlantState () { }

  public static String zoneID () { return System.getProperty("reflex.zone", "A"); }

  /** Cell name prefix for this zone, e.g. "ZA_". */
  public static String prefix () { return "Z" + zoneID() + "_"; }

  /** NeuPaths domain for this zone, e.g. "Zone_A". */
  public static String domain () { return "Zone_" + zoneID(); }

  public static boolean valveOpen ()
  {
    try
    {
      Path p = dir().resolve("valve.pos");
      if (!Files.exists(p)) return true;
      return !Files.readString(p).trim().equalsIgnoreCase("CLOSED");
    }
    catch (Exception e)
    {
      return true;  // unreadable => fail toward the default
    }
  }

  public static void setValveOpen (boolean open)
  {
    try
    {
      Files.createDirectories(dir());
      Files.writeString(dir().resolve("valve.pos"), open ? "OPEN" : "CLOSED");
    }
    catch (Exception e)
    {
      /* sim only - ignore */
    }
  }

  public static boolean burstActive ()
  {
    return Files.exists(dir().resolve("burst"));
  }

  private static Path dir ()
  {
    return Path.of("/tmp/reflex/zone" + zoneID());
  }
}
