// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;
import java.util.UUID;

/**
 * NeuPaths cell type for routing stimuli in a NeuPaths cell system.
 * 
 * @author Aaron Caraveo
 */
public class RouterCell extends Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code RouterCell} object with multiple synapses.
   * 
   * @param name           The cell's name at runtime.  This name should be
   *                       unique across the entire cell system.
   * @param synapseNames   The synapses this cell listens on and/or
   *                       connects to.
   * @param cryptoKey      The stimulus encryption key.  Specify {@code null}
   *                       to disable encryption using a user-specified
   *                       key.  If disabled, the stimuli will still be
   *                       encrypted as part of NeuPaths protocol encryption.
   */
  public
  RouterCell
    (String   name,
     String[] synapseNames,
     byte[]   cryptoKey)
  {
    super(CellType.ROUTER,
          name,
          new PropertySet(),
          synapseNames,
          cryptoKey);

    // Routers don't have subscriptions    
    setSubscriptionRefreshInterval(0L);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code RouterCell} object with a single synapse.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  RouterCell
    (String name,
     String synapseName,
     byte[] cryptoKey)
  {
    super(CellType.ROUTER,
          name,
          new PropertySet(),
          synapseName,
          cryptoKey);

    // Routers don't have subscriptions    
    setSubscriptionRefreshInterval(0L);
  }
  
  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  //===========================================================================
  //  MEMBERS
  //===========================================================================

}
