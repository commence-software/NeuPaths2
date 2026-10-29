// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

public class PropertyRequestActivator extends Activator
{
  public PropertyRequestActivator
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
    PropertyRequest propertyReq = getStimulus("PropertyRequest");

    // Copy request's transaction ID to maintain transaction chain
    setStimulus("PropertyResponse",
                new PropertyResponse(getProperty(propertyReq.propertyName)),
                propertyReq.getTransactionID());
  }
}
