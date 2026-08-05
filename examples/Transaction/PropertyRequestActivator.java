// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

public class PropertyRequestActivator extends Activator
{
  public PropertyRequestActivator ()
  {
    super("PropertyRequestActivator",
          new ReceptorSpec[] {
              new ReceptorSpec("PropertyRequest",
                               ReceptorMode.BUFFERED,
                               PropertyRequest.TYPE_ID)
          },
          new TransmitterSpec[] {
              new TransmitterSpec("PropertyResponse",
                                  PropertyResponse.TYPE_ID,
                                  StimulusTrace.ENABLED)
          },
          new LogicSubscriptionSpec[] {
              new LogicSubscriptionSpec(".*",
                                        "PropertyRequest",
                                        "PropertyRequest",
                                        "@",
                                        TransactionFilter.DISABLED)
          });
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
