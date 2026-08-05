// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.UUID;

/**
 * NeuPaths cell type for distributing workload among a pool of {@link LoadBalancedCell}s.
 * {@link LoadBalancedCell}s in a pool are identified by a cell name prefix and cell
 * domain.
 * <p>
 * A Load-Controller's receptors and subscriptions should match those used by the
 * {@link LoadBalancedActivator}(s) of the {@link LoadBalancedCell}s in the cell pool.
 * If multiple {@link LoadBalancedActivator}s are used by the cells in the pool, then
 * the Load-Controller's receptors and subscriptions should be the closure of all the
 * receptors/subscriptions used by the Load-Balanced cells.  When multiple
 * {@link LoadBalancedActivator}s are used, the Load-Controller will wait for stimuli
 * to arrive on all receptors before distributing work to a {@link LoadBalancedCell}.
 * </p>
 *
 * @author Aaron Caraveo
 */
public class LoadControllerCell extends Cell
{
  /**
   * Creates a new {@code LoadControllerCell} object.
   * 
   * @param name            The cell's name at runtime.  This name should be
   *                        unique across the entire cell system.
   * @param synapseNames    The synapses this cell listens on and/or
   *                        connects to.
   * @param receptors       The receptors this cell receives stimuli on.  These
   *                        receptors should match the receptors used by the
   *                        {@link LoadBalancedActivator}s of the
   *                        {@link LoadBalancedCell}s in the cell pool.
   * @param subscriptions   The subscriptions this cell advertises.  These
   *                        subscriptions should match the subscriptions used by
   *                        the {@link LoadBalancedActivator}s of the
   *                        {@link LoadBalancedCell}s in the cell pool.
   * @param cellNamePrefix  The name prefix of the {@link LoadBalancedCell}s in
   *                        the cell pool.
   * @param cellDomain      The domain of the {@link LoadBalancedCell}s in the
   *                        cell pool.
   * @param cryptoKey       The stimulus encryption key.  Specify {@code null}
   *                        to disable encryption using a user-specified
   *                        key.  If disabled, the stimuli will still be
   *                        encrypted as part of NeuPaths protocol encryption.
   */
  public LoadControllerCell
    (String                  name,
     String[]                synapseNames,
     ReceptorSpec[]          receptors,
     LogicSubscriptionSpec[] subscriptions,
     String                  cellNamePrefix,
     String                  cellDomain,
     byte[]                  cryptoKey)
  {
    super(CellType.LOAD_CONTROLLER,
          name,
          new PropertySet(),
          synapseNames,
          cryptoKey);

    addActivator(new CellRegistration(cellNamePrefix, cellDomain));
    addActivator(new TransactionExtractor(receptors, subscriptions));
    addActivator(new TransactionRequest(cellNamePrefix, cellDomain));
    
    //-------------------------------------------------------------------------
    // Create SignalGenerator
    //-------------------------------------------------------------------------
    
    ReceptorSpecSet receptorSpecs = new ReceptorSpecSet(receptors);

    receptorSpecs.add(
        new ReceptorSpec(LB.REQUEST,
                         ReceptorMode.NON_BUFFERED,
                         Stim_LoadBalanceRequest.TYPE_ID));

    LogicSubscriptionSpecSet subscriptionSpecs =
        new LogicSubscriptionSpecSet(subscriptions);

    subscriptionSpecs.add(
        new LogicLoopbackSubscriptionSpec(LB.TRANS_REQ,
                                          LB.REQUEST));

    SignalGenerator signalGenerator =
        new SignalGenerator(receptorSpecs.toArray(new ReceptorSpec[0]),
                            subscriptionSpecs.toArray(new LogicSubscriptionSpec[0]));

    addActivator(signalGenerator);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LoadControllerCell} object.
   * 
   * @param name            The cell's name at runtime.  This name should be
   *                        unique across the entire cell system.
   * @param synapseName     The synapse this cell listens on and/or
   *                        connects to.
   * @param receptors       The receptors this cell receives stimuli on.  These
   *                        receptors should match the receptors used by the
   *                        {@link LoadBalancedActivator}s of the
   *                        {@link LoadBalancedCell}s in the cell pool.
   * @param subscriptions   The subscriptions this cell advertises.  These
   *                        subscriptions should match the subscriptions used by
   *                        the {@link LoadBalancedActivator}s of the
   *                        {@link LoadBalancedCell}s in the cell pool.
   * @param cellNamePrefix  The name prefix of the {@link LoadBalancedCell}s in
   *                        the cell pool.
   * @param cellDomain      The domain of the {@link LoadBalancedCell}s in the
   *                        cell pool.
   * @param cryptoKey       The stimulus encryption key.  Specify {@code null}
   *                        to disable encryption using a user-specified
   *                        key.  If disabled, the stimuli will still be
   *                        encrypted as part of NeuPaths protocol encryption.
   */
  public LoadControllerCell
    (String                  name,
     String                  synapseName,
     ReceptorSpec[]          receptors,
     LogicSubscriptionSpec[] subscriptions,
     String                  cellNamePrefix,
     String                  cellDomain,
     byte[]                  cryptoKey)
  {
    this(name,
         new String[] { synapseName },
         receptors,
         subscriptions,
         cellNamePrefix,
         cellDomain,
         cryptoKey);
  }

  //===========================================================================
  //  PRIVATE MEMBERS
  //===========================================================================

  private static class CellRegistration extends Activator
  {
    public CellRegistration (String cellNamePrefix, String cellDomain)
    {
      super("CellRegistration",
            new ReceptorSpec[] {
                new ReceptorSpec(LB.REQUEST,
                                 ReceptorMode.NON_BUFFERED,
                                 Stim_LoadBalanceRequest.TYPE_ID) },
            new TransmitterSpec[] {
                new TransmitterSpec(LB.REGISTRATION,
                                    Stim_LoadBalanceRegistration.TYPE_ID,
                                    StimulusTrace.ENABLED) },
            new LogicSubscriptionSpec[] {
                new LogicSubscriptionSpec(cellNamePrefix + ".*",
                                          LB.REQUEST,
                                          LB.REQUEST,
                                          cellDomain) });
    }
    
    protected void evaluate ()
    {
      Stim_LoadBalanceRequest request = getStimulus(LB.REQUEST);

      setStimulus(LB.REGISTRATION,
                  new Stim_LoadBalanceRegistration(request.cellInstanceID));
    }
  }
  
  //---------------------------------------------------------------------------
  
  private static class TransactionExtractor extends Activator
  {
    public TransactionExtractor
      (ReceptorSpec[]          receptors,
       LogicSubscriptionSpec[] subscriptions)
    {
      super("TransactionExtractor",
            receptors,
            new TransmitterSpec[] {
                new TransmitterSpec(LB.TRANSACTION,
                                    Stim_LoadBalanceTransaction.TYPE_ID,
                                    StimulusTrace.ENABLED) },
            subscriptions);
    }

    protected void evaluate ()
    {
      UUID transactionID = getTransactionID();

      setStimulus(LB.TRANSACTION,
                  new Stim_LoadBalanceTransaction(transactionID));
    }
  }

  //---------------------------------------------------------------------------
  
  private static class TransactionRequest extends Activator
  {
    public TransactionRequest (String cellNamePrefix, String cellDomain)
    {
      super("TransactionRequest",
            new ReceptorSpec[] {
                new ReceptorSpec(LB.TRANSACTION,
                                 ReceptorMode.NON_BUFFERED,
                                 Stim_LoadBalanceTransaction.TYPE_ID),
                new ReceptorSpec(LB.REQUEST,
                                 ReceptorMode.BUFFERED,
                                 Stim_LoadBalanceRequest.TYPE_ID) },
            new TransmitterSpec[] {
                new TransmitterSpec(LB.TRANS_REQ,
                                    Stim_LoadBalanceRequest.TYPE_ID,
                                    StimulusTrace.ENABLED) },
            new LogicSubscriptionSpec[] {
                new LogicLoopbackSubscriptionSpec(LB.TRANSACTION,
                                                  LB.TRANSACTION),
                new LogicSubscriptionSpec(cellNamePrefix + ".*",
                                          LB.REQUEST,
                                          LB.REQUEST,
                                          cellDomain) });
    }

    protected void evaluate ()
    {
      Stim_LoadBalanceTransaction transaction = getStimulus(LB.TRANSACTION);
      Stim_LoadBalanceRequest request = getStimulus(LB.REQUEST);

      setStimulus(LB.TRANS_REQ,
                  new Stim_LoadBalanceRequest(request.cellInstanceID),
                  transaction.transactionID);
    }
  }
  
  //---------------------------------------------------------------------------
  
  private static class SignalGenerator extends Activator
  {
    public SignalGenerator
      (ReceptorSpec[]          receptors,
       LogicSubscriptionSpec[] subscriptions)
    {
      super("SignalGenerator",
            receptors,
            new TransmitterSpec[] {
                new TransmitterSpec(LB.SIGNAL,
                                    Stim_LoadBalanceSignal.TYPE_ID,
                                    StimulusTrace.ENABLED) },
            subscriptions);
    }

    protected void evaluate ()
    {
      Stim_LoadBalanceRequest request = getStimulus(LB.REQUEST);

      setStimulus(LB.SIGNAL,
                  new Stim_LoadBalanceSignal(request.cellInstanceID),
                  request.getTransactionID());
    }
  }
}
