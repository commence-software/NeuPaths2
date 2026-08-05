// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import java.util.Collections;
import java.util.LinkedList;

public class AlarmServiceCell extends LogicCell
{
  public
  AlarmServiceCell (String name,
                    String[]  synapseNames,
                    Activator[] activators,
                    byte[] cryptoKey)
  {
    super(name, synapseNames, activators, cryptoKey);
  }

  protected
  void
  initialize ()
  {
    setProperty("Alarms",
                Collections.synchronizedList(new LinkedList<AlarmInfo>()));
  }
}
