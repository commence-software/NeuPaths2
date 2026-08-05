// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.UUID;

class TransactionRequestActivator extends Activator
{
  TransactionRequestActivator ()
  {
    super("HandleTransactionRequest",
          new ReceptorSpec[] {
            new ReceptorSpec("TransactionRequest",
                             ReceptorMode.BUFFERED,
                             SignalStimulus.TYPE_ID)
          },
          new TransmitterSpec[] {
            new TransmitterSpec("ServiceRequest",
                                StringStimulus.TYPE_ID,
                                StimulusTrace.ENABLED)
          },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec("TransactionInjector",
                                      "TransactionRequest",
                                      "TransactionRequest",
                                      "@",
                                      TransactionFilter.ENABLED)
	  });

    seq_num = 0;
  }

  protected void evaluate ()
  {
    SignalStimulus transReq = getStimulus("TransactionRequest");

    UUID transactionID = createTransaction(transReq.getInstanceID(), transReq.getTransactionID());

    seq_num++;

    setStimulus("ServiceRequest",
                new StringStimulus(getName() + seq_num),
                transactionID);
  }

  private int seq_num;
}
