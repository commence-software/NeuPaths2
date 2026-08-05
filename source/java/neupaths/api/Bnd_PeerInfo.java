// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Information about a binder's peer.
 *
 * @author Aaron Caraveo
 */
final class Bnd_PeerInfo
{
  Bnd_PeerInfo
    (UUID        binderInstanceID,
     UUID        cellInstanceID,
     String      cellName,
     String      synapseDomain,
     UUID        fromPeerSynapseInstanceID,
     UUID        toPeerSynapseInstanceID,
     Syn_Synapse fromPeerSynapse,
     Syn_Synapse toPeerSynapse,
     Thread      peerThread)
  {
    this.binderInstanceID = binderInstanceID;
    this.cellInstanceID = cellInstanceID;
    this.cellName = cellName;
    this.synapseDomain = synapseDomain;
    this.fromPeerSynapseInstanceID = fromPeerSynapseInstanceID;
    this.toPeerSynapseInstanceID = toPeerSynapseInstanceID;
    this.fromPeerSynapse = fromPeerSynapse;
    this.toPeerSynapse = toPeerSynapse;
    this.peerThread = peerThread;
  }

  Bnd_PeerInfo (Bnd_PeerInfo pi)
  {
    binderInstanceID = pi.binderInstanceID;
    cellInstanceID = pi.cellInstanceID;
    cellName = pi.cellName;
    synapseDomain = pi.synapseDomain;
    fromPeerSynapseInstanceID = pi.fromPeerSynapseInstanceID;
    toPeerSynapseInstanceID = pi.toPeerSynapseInstanceID;
    fromPeerSynapse = pi.fromPeerSynapse;
    toPeerSynapse = pi.toPeerSynapse;
    peerThread = pi.peerThread;
  }
  
  @Override
  public String toString ()
  {
    String image = "\nPeerInfo" +
                   "\n[" +
                   "\n  binderInstanceID=" + binderInstanceID +
                   "\n  cellInstanceID=" + cellInstanceID +
                   "\n  cellName=" + cellName +
                   "\n  synapseDomain=" + synapseDomain +
                   "\n  fromPeerSynapseInstanceID=" + fromPeerSynapseInstanceID +
                   "\n  toPeerSynapseInstanceID=" + toPeerSynapseInstanceID +
                   "\n]";

    return image;
  }

  UUID        binderInstanceID;
  UUID        cellInstanceID;
  String      cellName;
  String      synapseDomain;
  UUID        fromPeerSynapseInstanceID;
  UUID        toPeerSynapseInstanceID;
  Syn_Synapse fromPeerSynapse;
  Syn_Synapse toPeerSynapse;
  Thread      peerThread;
}
