// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;
import neupaths.util.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.time.Instant;

public class AlarmActivator extends PulsedActivator
{
  public AlarmActivator ()
  {
    super("AlarmActivator",
          new TransmitterSpec[] {
            new TransmitterSpec("AlarmExpiration",
                                AlarmExpiration.TYPE_ID) });
  }

  public void evaluate ()
  {
    DateStimulus pulse = getStimulus("Pulse");
    
    List<AlarmInfo> alarms = getProperty("Alarms");

    Instant currentTime = new Date().toInstant();

    synchronized (alarms)
    {
      Iterator<AlarmInfo> i = alarms.iterator();
      while (i.hasNext())
      {
        AlarmInfo info = i.next();

        if (info.alarmTime.compareTo(currentTime) <= 0)
        {
          setStimulus("AlarmExpiration",
                      new AlarmExpiration(info.request.id),
                      info.request.getTransactionID());

          if (info.request.type == AlarmType.INTERVAL &&
              info.request.frequency == AlarmFrequency.PERIODIC)
          {
            info.alarmTime = currentTime.plusMillis(info.request.intervalMs);
          }
          else
          {
            i.remove();
          }
        }
      }
    }
  }
}
