// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.UUID;

class ServiceRequestActivator extends Activator
{
  ServiceRequestActivator ()
  {
    super("HandleServiceRequest",
          new ReceptorSpec[] {
            new ReceptorSpec("ServiceRequest",
                             ReceptorMode.BUFFERED,
                             StringStimulus.TYPE_ID)
          },
          new TransmitterSpec[] {
            new TransmitterSpec("ServiceResponse",
                                StringStimulus.TYPE_ID,
                                StimulusTrace.ENABLED)
          },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec("TransactionCell.*",
                                      "ServiceRequest",
                                      "ServiceRequest",
                                      "Service",
                                      TransactionFilter.DISABLED)
	  });

    seq_num = 0;
  }

  protected void evaluate ()
  {
    StringStimulus servReq = getStimulus("ServiceRequest");

    seq_num++;

    setStimulus("ServiceResponse",
                new StringStimulus(getName() + seq_num),
                servReq.getTransactionID());
  }

  private int seq_num;
}
