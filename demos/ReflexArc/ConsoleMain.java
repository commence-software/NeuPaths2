import java.io.BufferedReader;
import java.io.InputStreamReader;
import neupaths.api.*;

/**
 * The operator console: an InjectorCell for valve overrides and an
 * ExtractorCell that pulls anomalies, leaks, sensor alarms and trends
 * out of the Ops domain.  This is ordinary code at the edge of the
 * mesh - during a partition the override cannot reach the zone, and
 * that is the point of the demo: the reflex protects the plant anyway.
 */
public class ConsoleMain
{
  public static void main (String[] args)
  {
    CellCluster cluster = new CellCluster("cfg/Cl_OpsConsole.xml");
    cluster.start();

    InjectorCell inj = cluster.getCell("OpsConsoleIn");
    ExtractorCell extr = cluster.getCell("OpsConsoleOut");

    Thread feed = new Thread(() ->
    {
      while (true)
      {
        Stimulus s = extr.extract();
        System.out.println("[OPS] " + s);
      }
    });
    feed.setDaemon(true);
    feed.start();

    System.out.println("Operator console.  Commands: open | close | quit");

    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String line;

      while ((line = in.readLine()) != null)
      {
        line = line.trim().toLowerCase();

        if (line.equals("open") || line.equals("close"))
        {
          boolean open = line.equals("open");
          inj.inject(new ValveCmdStim(open,
                                      "operator override",
                                      "OpsConsoleIn"));
          System.out.println("[OPS] override injected: " + line);
        }
        else if (line.equals("quit"))
        {
          System.exit(0);
        }
        else if (!line.isEmpty())
        {
          System.out.println("commands: open | close | quit");
        }
      }

      // stdin closed (e.g. backgrounded) - keep extracting
      while (true)
      {
        Thread.sleep(10_000L);
      }
    }
    catch (Exception e)
    {
      System.out.println("ERROR: " + e);
    }
  }
}
