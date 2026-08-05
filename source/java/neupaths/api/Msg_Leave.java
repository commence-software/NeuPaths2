// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Message sent when a cell leaves the cellular system.
 *
 * @author Aaron Caraveo
 */
final class Msg_Leave extends Msg_NeuPaths
{
  Msg_Leave (UUID   cellInstanceID,
             String cellName,
             UUID   binderInstanceID)
  {
    this.cellInstanceID = cellInstanceID;
    this.cellName = cellName;
    this.binderInstanceID = binderInstanceID;
  }

  Msg_Leave (Msg_Leave msg)
  {
    this(msg.cellInstanceID, msg.cellName, msg.binderInstanceID);
  }

  @Override
  public String toString ()
  {
    String image = "\nLeave" +
                   "\n[" +
                   "\n  cellInstanceID=" + cellInstanceID +
                   "\n  cellName=" + cellName +
                   "\n  binderInstanceID=" + binderInstanceID +
                   "\n]";

    return image;
  }

  UUID   cellInstanceID;
  String cellName;
  UUID   binderInstanceID;

  private static final long serialVersionUID = 3547620702916377248L;
}
