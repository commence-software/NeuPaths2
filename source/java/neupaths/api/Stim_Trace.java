// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.Serializable;

/**
 * Information about a stimulus visit during its propagation through
 * the cellular system.
 *
 * @author Aaron Caraveo
 */
final class Stim_Trace implements Serializable
{
  Stim_Trace (String cellName, String domainName)
  {
    this.cellName = cellName;
    this.domainName = domainName;
  }
  
  String cellName;
  String domainName;

  private static final long serialVersionUID = 1461477642412824900L;
}
