// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import java.util.Date;
import java.util.UUID;
import neupaths.api.Stimulus;

public final class AlarmRequest extends Stimulus
{
  public
  AlarmRequest (String         id,
                AlarmType      type,
                AlarmFrequency frequency,
                long           intervalMs,
                Date           dateTime)
  {
    super(TYPE_NAME, TYPE_ID);
    this.id = id;
    this.type = type;
    this.frequency = frequency;
    this.intervalMs = intervalMs;
    this.dateTime = dateTime;
  }

  public String toString()
  {
    return TYPE_NAME + "/" + id;
  }

  String         id;
  AlarmType      type;
  AlarmFrequency frequency;
  long           intervalMs;
  Date           dateTime;
  
  public static final String TYPE_NAME = "AlarmRequest";
  public static final UUID TYPE_ID = UUID.fromString("2f8b612c-c245-47a1-828e-5b1647e0f7a0");
}
