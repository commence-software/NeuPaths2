// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

public class WorldHelloActivator extends Activator
{
  public WorldHelloActivator ()
  {
    super("WorldHelloActivator",
          new ReceptorSpec[] {
            new ReceptorSpec("Hail",
                             ReceptorMode.NON_BUFFERED,
                             DateStimulus.TYPE_ID)
          },
          new TransmitterSpec[] {
            new TransmitterSpec("Salutation",
                                WorldHelloSalutation.TYPE_ID,
                                StimulusTrace.ENABLED)
          },
          new LogicSubscriptionSpec[] {
            new LogicSubscriptionSpec("HailInjector",
                                      "Hello",
                                      "Hail",
                                      "Request",
                                      TransactionFilter.DISABLED)});
  }

  @Override
  public void start ()
  {
    cityName = getProperty("city_name");
    salutation = getProperty("salutation");
  }

  @Override
  protected void evaluate ()
  {
    DateStimulus s = getStimulus("Hail");
    System.out.println("... " + cityName + " was hailed on " + s);

    WorldHelloSalutation response = new WorldHelloSalutation(cityName, salutation);
    setStimulus("Salutation", response);
  }
  
  private String cityName;
  private String salutation;
}
