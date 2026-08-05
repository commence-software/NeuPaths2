// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;

/**
 * NeuPaths cell type for sharing workload among a pool of cells.  A
 * Load-Balanced cell uses a {@link LoadBalancedActivator} to register with
 * a {@link LoadControllerCell}, which distributes the workload among all
 * registered cells.
 * <p>
 * At least one {@link LoadBalancedActivator} must be provided to a
 * Load-Balanced cell to benefit from workload distribution.  Additional
 * {@link Activator}s can be provided for complex computational tasks.
 * If multiple {@link LoadBalancedActivator}s are provided, the
 * Load-Controller cell's receptors and subscriptions should be the
 * closure of all receptors/subscriptions provided in Load-Balanced
 * activators (see {@link LoadControllerCell}).
 * </p>
 * 
 * @author Aaron Caraveo
 */
public class LoadBalancedCell extends Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code LoadBalancedCell} object with multiple synapses and
   * activators.
   * 
   * @param name              The cell's name at runtime.  This name should be
   *                          unique across the entire cell system.  For
   *                          {@code LoadBalancedCells}, the name should use
   *                          a prefix that is shared among all cells in the pool.
   * @param properties        A dictionary of named properties.  Properties are
   *                          shared/accessible by all activators.
   * @param synapseNames      The synapses this cell listens on and/or
   *                          connects to.
   * @param activators        The cell's activators.
   * @param controllerName    {@link LoadControllerCell}'s name.
   * @param controllerDomain  {@link LoadControllerCell}'s domain.
   * @param cryptoKey         The stimulus encryption key.  Specify {@code null}
   *                          to disable encryption using a user-specified
   *                          key.  If disabled, the stimuli will still be
   *                          encrypted as part of NeuPaths protocol encryption.
   */
  public LoadBalancedCell
    (String      name,
     PropertySet properties,
     String[]    synapseNames,
     Activator[] activators,
     String      controllerName,
     String      controllerDomain,
     byte[]      cryptoKey)
  {
    super(CellType.LOAD_BALANCED,
          name,
          properties,
          synapseNames,
          activators,
          cryptoKey);

    if (controllerName == null)
    {
      throw new NeuPathsException(CellType.LOAD_BALANCED,
                                  "Parameter 'controllerName' is required");
    }
    
    if (controllerDomain == null)
    {
      throw new NeuPathsException(CellType.LOAD_BALANCED,
                                  "Parameter 'controllerDomain' is required");
    }
    
    Actv_LoadBalanceRegistration registration =
        new Actv_LoadBalanceRegistration(controllerName,
                                         controllerDomain);

    addActivator(registration);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LoadBalancedCell} object with multiple synapses and
   * activators.
   * 
   * @param name              The cell's name at runtime.  This name should be
   *                          unique across the entire cell system.  For
   *                          {@code LoadBalancedCells}, the name should use
   *                          a prefix that is shared among all cells in the pool.
   * @param synapseNames      The synapses this cell listens on and/or
   *                          connects to.
   * @param activators        The cell's activators.
   * @param controllerName    {@link LoadControllerCell}'s name.
   * @param controllerDomain  {@link LoadControllerCell}'s domain.
   * @param cryptoKey         The stimulus encryption key.  Specify {@code null}
   *                          to disable encryption using a user-specified
   *                          key.  If disabled, the stimuli will still be
   *                          encrypted as part of NeuPaths protocol encryption.
   */
  public LoadBalancedCell
    (String      name,
     String[]    synapseNames,
     Activator[] activators,
     String      controllerName,
     String      controllerDomain,
     byte[]      cryptoKey)
  {
    this(name,
         new PropertySet(),
         synapseNames,
         activators,
         controllerName,
         controllerDomain,
         cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LoadBalancedCell} object with a single synapse and
   * activator.
   * 
   * @param name              The cell's name at runtime.  This name should be
   *                          unique across the entire cell system.  For
   *                          {@code LoadBalancedCells}, the name should use
   *                          a prefix that is shared among all cells in the pool.
   * @param properties        A dictionary of named properties.
   * @param synapseName       The synapse this cell listens on or connects to.
   * @param activator         The cell's activator.
   * @param controllerName    {@link LoadControllerCell}'s name.
   * @param controllerDomain  {@link LoadControllerCell}'s domain.
   * @param cryptoKey         The stimulus encryption key.  Specify {@code null}
   *                          to disable encryption using a user-specified
   *                          key.  If disabled, the stimuli will still be
   *                          encrypted as part of NeuPaths protocol encryption.
   */
  public LoadBalancedCell
    (String                name,
     PropertySet           properties,
     String                synapseName,
     LoadBalancedActivator activator,
     String                controllerName,
     String                controllerDomain,
     byte[]                cryptoKey)
  {
    this(name,
         properties,
         new String[] { synapseName },
         new Activator[] { activator },
         controllerName,
         controllerDomain,
         cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LoadBalancedCell} object with a single synapse and
   * activator.
   * 
   * @param name              The cell's name at runtime.  This name should be
   *                          unique across the entire cell system.  For
   *                          {@code LoadBalancedCells}, the name should use
   *                          a prefix that is shared among all cells in the pool.
   * @param synapseName       The synapse this cell listens on or connects to.
   * @param activator         The cell's activator.
   * @param controllerName    {@link LoadControllerCell}'s name.
   * @param controllerDomain  {@link LoadControllerCell}'s domain.
   * @param cryptoKey         The stimulus encryption key.  Specify {@code null}
   *                          to disable encryption using a user-specified
   *                          key.  If disabled, the stimuli will still be
   *                          encrypted as part of NeuPaths protocol encryption.
   */
  public LoadBalancedCell
    (String                name,
     String                synapseName,
     LoadBalancedActivator activator,
     String                controllerName,
     String                controllerDomain,
     byte[]                cryptoKey)
  {
    this(name,
         new PropertySet(),
         new String[] { synapseName },
         new Activator[] { activator },
         controllerName,
         controllerDomain,
         cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LoadBalancedCell} object with multiple synapses and
   * a single activator.
   * 
   * @param name              The cell's name at runtime.  This name should be
   *                          unique across the entire cell system.  For
   *                          {@code LoadBalancedCells}, the name should use
   *                          a prefix that is shared among all cells in the pool.
   * @param properties        A dictionary of named properties.
   * @param synapseNames      The synapses this cell listens on or connects to.
   * @param activator         The cell's activator.
   * @param controllerName    {@link LoadControllerCell}'s name.
   * @param controllerDomain  {@link LoadControllerCell}'s domain.
   * @param cryptoKey         The stimulus encryption key.  Specify {@code null}
   *                          to disable encryption using a user-specified
   *                          key.  If disabled, the stimuli will still be
   *                          encrypted as part of NeuPaths protocol encryption.
   */
  public LoadBalancedCell
    (String                name,
     PropertySet           properties,
     String[]              synapseNames,
     LoadBalancedActivator activator,
     String                controllerName,
     String                controllerDomain,
     byte[]                cryptoKey)
  {
    this(name,
         properties,
         synapseNames,
         new Activator[] { activator },
         controllerName,
         controllerDomain,
         cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LoadBalancedCell} object with multiple synapses and
   * a single activator.
   * 
   * @param name              The cell's name at runtime.  This name should be
   *                          unique across the entire cell system.  For
   *                          {@code LoadBalancedCells}, the name should use
   *                          a prefix that is shared among all cells in the pool.
   * @param synapseNames      The synapses this cell listens on or connects to.
   * @param activator         The cell's activator.
   * @param controllerName    {@link LoadControllerCell}'s name.
   * @param controllerDomain  {@link LoadControllerCell}'s domain.
   * @param cryptoKey         The stimulus encryption key.  Specify {@code null}
   *                          to disable encryption using a user-specified
   *                          key.  If disabled, the stimuli will still be
   *                          encrypted as part of NeuPaths protocol encryption.
   */
  public LoadBalancedCell
    (String                name,
     String[]              synapseNames,
     LoadBalancedActivator activator,
     String                controllerName,
     String                controllerDomain,
     byte[]                cryptoKey)
  {
    this(name,
         new PropertySet(),
         synapseNames,
         new Activator[] { activator },
         controllerName,
         controllerDomain,
         cryptoKey);
  }
}
