// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Message sent when a cell joins the cellular system.
 * 
 * @author Aaron Caraveo
 */
final class Msg_JoinRequest extends Msg_NeuPaths
{
  Msg_JoinRequest (Msg_JoinPhase joinPhase,
                   UUID          binderInstanceID,
                   UUID          cellInstanceID,
                   String        cellName,
                   String        synapseDomain,
                   UUID          sendSynapseInstanceID,
                   String        sendSynapseName,
                   UUID          receiveSynapseInstanceID,
                   String        receiveSynapseName)
  {
    this.joinPhase = joinPhase;
    this.binderInstanceID = binderInstanceID;
    this.cellInstanceID = cellInstanceID;
    this.cellName = cellName;
    this.synapseDomain = synapseDomain;
    this.sendSynapseInstanceID = sendSynapseInstanceID;
    this.sendSynapseName = sendSynapseName;
    this.receiveSynapseInstanceID = receiveSynapseInstanceID;
    this.receiveSynapseName = receiveSynapseName;
  }

  @Override
  public String toString ()
  {
    String image = "JoinReq[" +
                   "joinPhase=" + joinPhase +
                   ", binderInstanceID=" + binderInstanceID +
                   ", cellInstanceID=" + cellInstanceID +
                   ", cellName=" + cellName +
                   ", synapseDomain=" + synapseDomain +
                   ", sendSynapseInstanceID=" + sendSynapseInstanceID +
                   ", sendSynapseName=" + sendSynapseName +
                   ", receiveSynapseInstanceID=" + receiveSynapseInstanceID +
                   ", receiveSynapseName=" + receiveSynapseName + "]";

    return image;
  }

  Msg_JoinPhase joinPhase;
  UUID          binderInstanceID;
  UUID          cellInstanceID;
  String        cellName;
  String        synapseDomain;
  UUID          sendSynapseInstanceID;
  String        sendSynapseName;
  UUID          receiveSynapseInstanceID;
  String        receiveSynapseName;

  private static final long serialVersionUID = 4258003956209685241L;
}
