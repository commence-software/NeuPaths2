// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A NeuPaths stimulus message.
 *
 * @author Aaron Caraveo
 */
final class Msg_Stimulus extends Msg_NeuPaths
{
  Msg_Stimulus ()
  {
    producerCellName = null;
    producerTransmitterName = null;
    value = null;
    trace = null;
    traceEnabled = false;
    typeName = null;
    typeID = null;
    instanceID = null;
    transactionID = null;
  }

  Msg_Stimulus
    (String producerCellName,
     String producerTransmitterName,
     String typeName,
     UUID   typeID,
     UUID   instanceID,
     UUID   transactionID,
     Object value)
  {
    this.producerCellName = producerCellName;
    this.producerTransmitterName = producerTransmitterName;
    this.typeName = typeName;
    this.typeID = typeID;
    this.instanceID = instanceID;
    this.transactionID = transactionID;
    this.value = value;

    trace = null;
    traceEnabled = false;
  }

  Msg_Stimulus (Msg_Stimulus msg)
  {
    producerCellName = msg.producerCellName;
    producerTransmitterName = msg.producerTransmitterName;
    typeName = msg.typeName;
    typeID = msg.typeID;
    instanceID = msg.instanceID;
    transactionID = msg.transactionID;
    value = msg.value;
    trace = msg.trace;
    traceEnabled = msg.traceEnabled;
  }

  @Override
  public
  String
  toString ()
  {
    String image = "Stimulus[" +
                   "producerCellName=" + producerCellName +
                   ", producerTransmitterName=" + producerTransmitterName +
                   ", typeName=" + typeName +
                   ", typeID=" + typeID +
                   ", instanceID=" + instanceID +
                   ", transactionID=" + transactionID + "]";

    return image;
  }

  final
  void
  enableTrace ()
  {
    traceEnabled = true;
    trace = new ArrayList<>(50);
  }
  
  final
  void
  addTrace (Stim_Trace traceInfo)
  {
    if (traceEnabled && trace.size() < 50)
      trace.add(traceInfo);
  }

  final
  void
  copyTrace (Msg_Stimulus s)
  {
    if (traceEnabled && s.traceEnabled)
    {
      for (Stim_Trace t : s.trace)
      {
        trace.add(t);
      }
    }
  }
  
  String producerCellName;
  String producerTransmitterName;
  String typeName;
  UUID   typeID;
  UUID   instanceID;
  UUID   transactionID;
  Object value;
  
  ArrayList<Stim_Trace> trace;
  boolean traceEnabled;

  static final long serialVersionUID = -7833595640972949487L;
}
