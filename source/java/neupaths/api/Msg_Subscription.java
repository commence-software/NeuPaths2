// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * A NeuPaths subscription message.
 *
 * @author Aaron Caraveo
 */
final class Msg_Subscription extends Msg_NeuPaths
{
  Msg_Subscription ()
  {
    producerCellName = null;
    producerTransmitterName = null;
    consumerReceptorName = null;
    domain = null;
    instanceID = UUID.randomUUID();
  }
  
  Msg_Subscription (String producerCellName,
                    String producerTransmitterName,
                    String consumerReceptorName,
                    String domain)
  {
    this.producerCellName = producerCellName;
    this.producerTransmitterName = producerTransmitterName;
    this.consumerReceptorName = consumerReceptorName;
    this.domain = domain;
    instanceID = UUID.randomUUID();
  }

  @Override
  public String toString ()
  {
    String image = "Subscribe[" +
                   "producerCellName=" + producerCellName +
                   ", producerTransmitterName=" + producerTransmitterName +
                   ", consumerReceptorName=" + consumerReceptorName +
                   ", domain=" + domain +
                   ", instance=" + instanceID + "]";

    return image;
  }

  String producerCellName;
  String producerTransmitterName;
  String consumerReceptorName;
  String domain;
  UUID   instanceID;

  private static final long serialVersionUID = 1479996278322114843L;
}
