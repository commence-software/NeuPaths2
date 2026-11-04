// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

public class LBActivator extends LoadBalancedActivator
{
  public LBActivator
    (String                  name,
     ReceptorSpec[]          receptors,
     TransmitterSpec[]       transmitters,
     LogicSubscriptionSpec[] subscriptions,
     String                  controllerName,
     String                  controllerDomain)
  {
    super(name, receptors, transmitters, subscriptions, controllerName, controllerDomain);
  }

  protected void evaluate ()
  {
    SignalStimulus signal = getStimulus("Request");

    setStimulus("Response", new StringStimulus(getCellName()), signal.getTransactionID());

    terminateTransaction(signal.getTransactionID());
  }
}
