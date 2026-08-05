// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;
import java.util.UUID;

/**
 * NeuPaths cell type for injecting stimuli into a NeuPaths cell system.
 * 
 * @author Aaron Caraveo
 */
public class InjectorCell extends Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code InjectorCell} object with a single synapse.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param transmitter   The transmitter on which the stimulus is injected.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  InjectorCell
    (String          name,
     String          synapseName,
     TransmitterSpec transmitter,
     byte[]          cryptoKey)
  {
    this(name,
         new String[] { synapseName },
         transmitter,
         cryptoKey);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code InjectorCell} object with multiple synapses.
   * 
   * @param name           The cell's name at runtime.  This name should be
   *                       unique across the entire cell system.
   * @param synapseNames   The synapses this cell listens on and/or
   *                       connects to.
   * @param transmitter    The transmitter on which the stimulus is injected.
   * @param cryptoKey      The stimulus encryption key.  Specify {@code null}
   *                       to disable encryption using a user-specified
   *                       key.  If disabled, the stimuli will still be
   *                       encrypted as part of NeuPaths protocol encryption.
   */
  public
  InjectorCell
    (String          name,
     String[]        synapseNames,
     TransmitterSpec transmitter,
     byte[]          cryptoKey)
  {
    super(CellType.INJECTOR,
          name,
          new PropertySet(),
          synapseNames,
          cryptoKey);
    
    if (transmitter == null)
    {
      throw new NeuPathsException(CellType.INJECTOR,
                                  name,
                                  "Parameter 'transmitter' is required");
    }
    
    this.transmitter = transmitter;

    // Injectors don't have subscriptions
    setSubscriptionRefreshInterval(0L);
  }
  
  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  /**
   * Injects a stimulus into the cell system.
   * 
   * @param stimulus The stimulus to inject.
   */
  public final
  void
  inject (Stimulus stimulus)
  {
    stimulus.setProducerCellID(getInstanceID());
    stimulus.setProducerCellName(getName());
    stimulus.setProducerTransmitterName(transmitter.getName());
    
    transmitStimulus(stimulus, transmitter.isTraceEnabled());
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Injects a stimulus in the specified transaction into the cell system.
   * 
   * @param stimulus      The stimulus to inject.
   * @param transactionID The transaction to associate the stimulus with.
   */
  public final
  void
  injectWithTransaction (Stimulus stimulus, UUID transactionID)
  {
    stimulus.setTransactionID(transactionID);
    stimulus.setProducerCellID(getInstanceID());
    stimulus.setProducerCellName(getName());
    stimulus.setProducerTransmitterName(transmitter.getName());
    
    transmitStimulus(stimulus, transmitter.isTraceEnabled());
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Injects a stimulus in a new transaction into the cell system.
   * 
   * @param stimulus The stimulus to inject.
   * @return         The new transaction ID.
   */
  public final
  UUID
  injectAsTransaction (Stimulus stimulus)
  {
    UUID transactionID = UUID.randomUUID();
    
    stimulus.setTransactionID(transactionID);
    stimulus.setProducerCellID(getInstanceID());
    stimulus.setProducerCellName(getName());
    stimulus.setProducerTransmitterName(transmitter.getName());
    
    transmitStimulus(stimulus, transmitter.isTraceEnabled());
    
    return transactionID;
  }
  
  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  //===========================================================================
  //  MEMBERS
  //===========================================================================

  private TransmitterSpec transmitter;
}
