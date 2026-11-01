// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;
import neupaths.util.*;

import java.util.Date;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

class Main
{
  public static void main (String[] args)
  {
    // Construct and start the cluster
    CellCluster cluster = new CellCluster("cfg/AlarmCluster.xml");
    cluster.start();

    // Retrieve injector and extractor from cluster
    InjectorCell inj = cluster.getCell("AlarmInjector");
    ExtractorCell extr = cluster.getCell("AlarmExtractor");

    try { Thread.sleep(2000); } catch (InterruptedException ie) { /* do nothing */ }

    // Schedule a periodic timer
    inj.inject(
        new AlarmRequest("Interval_Periodic",
                         AlarmType.INTERVAL,
                         AlarmFrequency.PERIODIC,
                         10_000L,  // 10 sec
                         null));

    Date current = new Date();
    Date dt = Date.from(current.toInstant().plusSeconds(60));
    
    // Schedule a date-time timer for 1 minute in the future
    inj.inject(
        new AlarmRequest("DateTime_One_Time",
                         AlarmType.DATE_TIME,
                         AlarmFrequency.ONE_TIME,
                         0,
                         dt));
    
    while (true)
    {
      AlarmExpiration alarm = extr.extract();
      System.out.println("Alarm " + alarm.id + " at " + (new Date()));
    }
  }
}
