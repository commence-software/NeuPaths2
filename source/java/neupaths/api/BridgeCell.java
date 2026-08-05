// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;

/**
 * NeuPaths cell type for bridging stimuli across domains.  A bridge cell maintains
 * a presence in multiple domains and uses subscriptions to pull stimuli across
 * domain boundaries.
 * <p>
 * For example, if trying to pull stimuli from domain D<sub>2</sub> to domain
 * D<sub>1</sub>, a bridge cell would have synapses in each domain (Peer or
 * Listener).  The bridge cell would use subscriptions in domain D<sub>2</sub>
 * to pull stimuli to the bridge cell, and cells in domain D<sub>1</sub>
 * would use subscriptions to pull stimuli from the bridge cell.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public class BridgeCell extends Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code BridgeCell} object.
   * 
   * @param name            The cell's name at runtime.  This name should be
   *                        unique across the entire cell system.
   * @param synapseNames    The synapses this cell listens on and/or
   *                        connects to.
   * @param subscriptions   The subscriptions this cell advertises.  If
   *                        the subscription array is empty (null or empty
   *                        array), the cell operates as a {@link RouterCell}.
   * @param cryptoKey       The stimulus encryption key.  Specify {@code null}
   *                        to disable encryption using a user-specified
   *                        key.  If disabled, the stimuli will still be
   *                        encrypted as part of NeuPaths protocol encryption.
   */
  public
  BridgeCell
    (String                   name,
     String[]                 synapseNames,
     BridgeSubscriptionSpec[] subscriptions,
     byte[]                   cryptoKey)
  {
    super(CellType.BRIDGE,
          name,
          new PropertySet(),
          synapseNames,
          cryptoKey);

    if (subscriptions == null)
    {
      subscriptions = new BridgeSubscriptionSpec[0];
    }
    
    addSubscriptions(subscriptions);
  }

  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  //===========================================================================
  //  PROTECTED METHODS
  //===========================================================================

  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  //===========================================================================
  //  PRIVATE METHODS
  //===========================================================================

  //===========================================================================
  //  MEMBERS
  //===========================================================================

}
