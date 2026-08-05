// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Message used to acknowledge a join request.
 *
 * @author Aaron Caraveo
 */
final class Msg_JoinAcknowledge extends Msg_NeuPaths
{
  Msg_JoinAcknowledge (Msg_JoinPhase joinPhase,
                       UUID          binderInstanceID,
                       UUID          cellInstanceID,
                       String        cellName,
                       UUID          requesterInstanceID,
                       UUID          sendSynapseInstanceID,
                       String        sendSynapseName,
                       UUID          receiveSynapseInstanceID,
                       String        receiveSynapseName)
  {
    this.joinPhase = joinPhase;
    this.binderInstanceID = binderInstanceID;
    this.cellInstanceID = cellInstanceID;
    this.cellName = cellName;
    this.requesterInstanceID = requesterInstanceID;
    this.sendSynapseInstanceID = sendSynapseInstanceID;
    this.sendSynapseName = sendSynapseName;
    this.receiveSynapseInstanceID = receiveSynapseInstanceID;
    this.receiveSynapseName = receiveSynapseName;
  }

  @Override
  public String toString ()
  {
    String image = "JoinAck[" +
                   "joinPhase=" + joinPhase +
                   ", binderInstanceID=" + binderInstanceID +
                   ", cellInstanceID=" + cellInstanceID +
                   ", cellName=" + cellName +
                   ", requesterInstanceID=" + requesterInstanceID +
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
  UUID          requesterInstanceID;
  UUID          sendSynapseInstanceID;
  String        sendSynapseName;
  UUID          receiveSynapseInstanceID;
  String        receiveSynapseName;

  private static final long serialVersionUID = -393899208331580169L;
}
