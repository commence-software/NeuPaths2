// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashSet;
import java.util.UUID;

/**
 * Maintains information about a neighbor cell, including subscriptions.
 *
 * @author Aaron Caraveo
 */
final class Nuc_PeerInfo
{
  Nuc_PeerInfo
    (String  cellName,
     String  synapseDomain,
     UUID    binderInstanceID)
  {
    this.cellName = cellName;
    this.synapseDomain = synapseDomain;
    this.binderInstanceID = binderInstanceID;
    
    subscriptions = new HashSet<>();
  }
  
  @Override
  public
  String
  toString ()
  {
    String image = "\nPeerInfo" +
                   "\n[" +
                   "\n  cellName=" + cellName +
                   "\n  synapseDomain=" + synapseDomain +
                   "\n  binderInstanceID=" + binderInstanceID +
                   "\n]";

    return image;
  }

  String  cellName;
  String  synapseDomain;
  UUID    binderInstanceID;
  
  HashSet<Nuc_SubscriptionInfo> subscriptions;
}
