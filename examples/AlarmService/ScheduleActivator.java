// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.util.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ScheduleActivator extends Activator
{
  public ScheduleActivator ()
  {
    super("ScheduleActivator",
          new ReceptorSpec[] {
            new ReceptorSpec("AlarmRequest",
                             ReceptorMode.NON_BUFFERED,
                             AlarmRequest.TYPE_ID) },
          new TransmitterSpec[0],
          new LogicSubscriptionSpec[] {
            new LogicMapSubscriptionSpec(".*",
                                         "AlarmRequest",
                                         "@",
                                         TransactionFilter.ENABLED) });
  }

  public void evaluate ()
  {
    AlarmRequest request = getStimulus("AlarmRequest");
    List<AlarmInfo> alarms = getProperty("Alarms");

    AlarmInfo info = null;

    if (request.type == AlarmType.INTERVAL)
    {
      Date currentTime = new Date();
      
      info = new AlarmInfo(request,
                           currentTime.toInstant().
                             plusMillis(request.intervalMs));
    }
    else
    {
      info = new AlarmInfo(request,
                           request.dateTime.toInstant());
    }

    alarms.add(info);
  }
}
