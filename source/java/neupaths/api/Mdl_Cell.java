// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashSet;
import java.util.UUID;

final class Mdl_Cell
{
  Mdl_Cell (String name, CellType type)
  {
    this.name = name;
    this.type = type;
    
    id = UUID.randomUUID();

    syns = new HashSet<>();
    rxs  = new HashSet<>();
    txs  = new HashSet<>();
    subs = new HashSet<>();
  }

  String name;
  CellType type;
  UUID id;
  HashSet<String>  syns;
  HashSet<Mdl_Rx> rxs;
  HashSet<Mdl_Tx> txs;
  HashSet<Mdl_Sub> subs;
}
