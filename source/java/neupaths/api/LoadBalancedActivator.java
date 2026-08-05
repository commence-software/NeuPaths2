// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;

/**
 * The base class for NeuPaths Load-Balanced activators.  Load-Balanced
 * activators register with a Load-Controller cell, which distributes the
 * workload among the registered Load-Balanced cells.
 *
 * @see LoadBalancedCell
 * @see LoadControllerCell
 *
 * @author Aaron Caraveo
 */
public abstract class LoadBalancedActivator extends Activator
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code LoadBalancedActivator} object.
   * 
   * @param name             This activator's name at runtime.
   * @param receptors        The receptors this activator receives stimuli on.
   * @param transmitters     The transmitters this activator emits stimuli on.
   *                         If the transmitter array is empty (null or empty
   *                         array), the activator does not emit stimuli.
   * @param subscriptions    The subscriptions this activator advertises.
   * @param controllerName   {@link LoadControllerCell}'s name.
   * @param controllerDomain {@link LoadControllerCell}'s domain.
   */
  protected
  LoadBalancedActivator
    (String                  name,
     ReceptorSpec[]          receptors,
     TransmitterSpec[]       transmitters,
     LogicSubscriptionSpec[] subscriptions,
     String                  controllerName,
     String                  controllerDomain)
  {
    super(name, receptors, transmitters, subscriptions);

    // Add receptor for LoadBalanceSignal
    addReceptor(new ReceptorSpec(LB.SIGNAL,
                                 ReceptorMode.NON_BUFFERED,
                                 Stim_LoadBalanceSignal.TYPE_ID));

    // Add transmitter for LoadBalanceRequest
    addTransmitter(new TransmitterSpec(LB.REQUEST,
                                       Stim_LoadBalanceRequest.TYPE_ID));
    
    // Add subscription for LoadBalanceSignal
    addSubscription(
        new LogicSubscriptionSpec(controllerName,
                                  LB.SIGNAL,
                                  LB.SIGNAL,
                                  controllerDomain,
                                  TransactionFilter.ENABLED));
  }

  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  final void activate ()
  {
    Stim_LoadBalanceSignal signal = getStimulus(LB.SIGNAL);
    if (getCell().getInstanceID().equals(signal.cellInstanceID))
    {
      evaluate();
      
      postStimulus(new Stim_LoadBalanceRequest(getCell().getInstanceID()),
                   LB.REQUEST,
                   null);
    }
  }
}
