// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import java.util.UUID;
import neupaths.api.Stimulus;

public final class AlarmExpiration extends Stimulus
{
  public
  AlarmExpiration (String id)
  {
    super(TYPE_NAME, TYPE_ID);
    this.id = id;
  }

  public String toString()
  {
    return TYPE_NAME + "(" + id + ")";
  }

  String id;
  
  public static final String TYPE_NAME = "AlarmExpiration";
  public static final UUID TYPE_ID = UUID.fromString("afc7f52b-0d7d-4638-8ae6-54caf525c04c");
}
