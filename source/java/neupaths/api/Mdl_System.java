// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

final class Mdl_System
{
  Mdl_System (String name)
  {
    this.name = name;

    id = UUID.randomUUID();
    
    cells = new LinkedHashMap<>();
    flows = new LinkedHashSet<>();
    syns  = new LinkedHashSet<>();
  }

  String name;
  UUID id;
  LinkedHashMap<String, Mdl_Cell> cells;
  LinkedHashSet<Mdl_Flow> flows;
  LinkedHashSet<Mdl_Syn> syns;
}
