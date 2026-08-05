// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import java.time.Instant;
import java.util.UUID;

class AlarmInfo
{
  AlarmInfo (AlarmRequest request,
             Instant      alarmTime)
  {
    this.request = request;
    this.alarmTime = alarmTime;
  }
  
  AlarmRequest request;
  Instant      alarmTime;
}
