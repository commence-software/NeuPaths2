// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

import java.util.UUID;

public class ClusterResponseActivator extends Activator
{
  public ClusterResponseActivator
    (String                  name,
     ReceptorSpec[]          receptors,
     TransmitterSpec[]       transmitters,
     LogicSubscriptionSpec[] subscriptions)
  {
    super(name, receptors, transmitters, subscriptions);
  }

  @Override
  protected void evaluate ()
  {
    PropertyResponse propertyResp = getStimulus("PropertyResponse");

    if (isTransactionOriginator(propertyResp.getTransactionID()))
    {
      UUID respTransactionID =
          getResponseTransactionID(propertyResp.getTransactionID());

      setStimulus("ClusterResponse",
                  new ClusterResponse(propertyResp.propertyValue),
                  respTransactionID);

      terminateTransaction(propertyResp.getTransactionID());
    }
  }
}
