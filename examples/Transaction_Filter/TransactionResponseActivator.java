// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.UUID;

class TransactionResponseActivator extends Activator
{
  TransactionResponseActivator ()
  {
    super("HandleTransactionResponse",
          new ReceptorSpec[] {
            new ReceptorSpec("ServiceResponse",
                             ReceptorMode.BUFFERED,
                             StringStimulus.TYPE_ID)
          },
          new TransmitterSpec[] {
            new TransmitterSpec("TransactionResponse",
                                StringStimulus.TYPE_ID,
                                StimulusTrace.ENABLED)
          },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec("ServiceCell",
                                      "ServiceResponse",
                                      "ServiceResponse",
                                      "Service",
                                      TransactionFilter.DISABLED)
	  });
  }

  protected void evaluate ()
  {
    StringStimulus servResp = getStimulus("ServiceResponse");

    if (isTransactionOriginator(servResp.getTransactionID()))
    {
      UUID responseTransactionID =
         getResponseTransactionID(servResp.getTransactionID());

      setStimulus("TransactionResponse",
                  new StringStimulus(servResp.toString()),
                  responseTransactionID);

      terminateTransaction(servResp.getTransactionID());
    }
  }
}
