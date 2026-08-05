// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;
/**
 * Activator used by {@link LoadBalancedCell} to register with a
 * {@link LoadControllerCell}.
 *
 * @author Aaron Caraveo
 */
final class Actv_LoadBalanceRegistration extends Activator
{
  Actv_LoadBalanceRegistration
    (String loadControllerName,
     String loadControllerDomain)
  {
    super("LoadBalanceRegistration",
          new ReceptorSpec[] {
              new ReceptorSpec(LB.REGISTRATION,
                               ReceptorMode.NON_BUFFERED,
                               Stim_LoadBalanceRegistration.TYPE_ID) },
          new TransmitterSpec[] {
              new TransmitterSpec(LB.REQUEST,
                                  Stim_LoadBalanceRequest.TYPE_ID,
                                  StimulusTrace.ENABLED) },
          new LogicSubscriptionSpec[] {
              new LogicSubscriptionSpec(loadControllerName,
                                        LB.REGISTRATION,
                                        LB.REGISTRATION,
                                        loadControllerDomain) });

    terminateRegistration = new SafeBoolean();
  }

  protected
  void
  start ()
  {
    terminateRegistration.clear();
    registrationThread = new RegistrationThread();
    registrationThread.start();
  }
    
  protected
  void
  stop ()
  {
    if (registrationThread != null)
    {
      terminateRegistration.set();
      registrationThread.interrupt();
      registrationThread = null;
    }
  }
  
  protected void evaluate ()
  {
    Stim_LoadBalanceRegistration registration = getStimulus(LB.REGISTRATION);
    if (getCell().getInstanceID().equals(registration.cellInstanceID))
    {
      if (registrationThread != null)
      {
        terminateRegistration.set();
        registrationThread.interrupt();
        registrationThread = null;
      }
    }
  }
  
  //===========================================================================
  //  THREADS
  //===========================================================================
  
  final class RegistrationThread extends Thread
  {
    RegistrationThread ()
    {
      super(getCell().getName() + " (" + getCell().getInstanceID() + ") Registration");
      setDaemon(true);
    }

    @Override
    public
    void
    run ()
    {
      while (terminateRegistration.isNotSet())
      {
        try
        {
          Thread.sleep(1000);

          postStimulus(new Stim_LoadBalanceRequest(getCell().getInstanceID()),
                       LB.REQUEST,
                       null);
        }
        catch (InterruptedException ie)
        {
          // Ignore
        }
      }
    }
  }
  
  private Thread registrationThread;
  private SafeBoolean terminateRegistration;
}
