// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * NeuPaths cell type for processing stimuli.
 * 
 * @author Aaron Caraveo
 */
public class LogicCell extends Cell
{
  //===========================================================================
  //  CONSTRUCTORS
  //===========================================================================

  /**
   * Creates a new {@code LogicCell} object with multiple synapses and
   * activators.
   * 
   * @param name           The cell's name at runtime.  This name should be
   *                       unique across the entire cell system.
   * @param properties     A dictionary of named properties.  Properties are
   *                       shared/accessible by all activators.
   * @param synapseNames   The synapses this cell listens on and/or
   *                       connects to.
   * @param activators     The cell's activators.
   * @param cryptoKey      The stimulus encryption key.  Specify {@code null}
   *                       to disable encryption using a user-specified
   *                       key.  If disabled, the stimuli will still be
   *                       encrypted as part of NeuPaths protocol encryption.
   */
  public
  LogicCell
    (String      name,
     PropertySet properties,
     String[]    synapseNames,
     Activator[] activators,
     byte[]      cryptoKey)
  {
    super(CellType.LOGIC,
          name,
          properties,
          synapseNames,
          activators,
          cryptoKey);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LogicCell} object with multiple synapses and
   * activators.
   * 
   * @param name           The cell's name at runtime.  This name should be
   *                       unique across the entire cell system.
   * @param synapseNames   The synapses this cell listens on and/or
   *                       connects to.
   * @param activators     The cell's activators.
   * @param cryptoKey      The stimulus encryption key.  Specify {@code null}
   *                       to disable encryption using a user-specified
   *                       key.  If disabled, the stimuli will still be
   *                       encrypted as part of NeuPaths protocol encryption.
   */
  public
  LogicCell
    (String      name,
     String[]    synapseNames,
     Activator[] activators,
     byte[]      cryptoKey)
  {
    this(name,
         new PropertySet(),
         synapseNames,
         activators,
         cryptoKey);
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LogicCell} object with a single synapse and
   * activator.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param properties    A dictionary of named properties.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param activator     The cell's activator.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  LogicCell
    (String      name,
     PropertySet properties,
     String      synapseName,
     Activator   activator,
     byte[]      cryptoKey)
  {
    super(CellType.LOGIC,
          name,
          properties,
          synapseName,
          activator,
          cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LogicCell} object with a single synapse and
   * activator.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param activator     The cell's activator.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  LogicCell
    (String      name,
     String      synapseName,
     Activator   activator,
     byte[]      cryptoKey)
  {
    this(name,
         new PropertySet(),
         synapseName,
         activator,
         cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LogicCell} object with a single synapse and
   * multiple activators.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param properties    A dictionary of named properties.  Properties are
   *                      shared/accessible by all activators.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param activators    The cell's activators.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  LogicCell
    (String      name,
     PropertySet properties,
     String      synapseName,
     Activator[] activators,
     byte[]      cryptoKey)
  {
    super(CellType.LOGIC,
          name,
          properties,
          synapseName,
          activators,
          cryptoKey);
  }

  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code LogicCell} object with a single synapse and
   * multiple activators.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param activators    The cell's activators.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  LogicCell
    (String      name,
     String      synapseName,
     Activator[] activators,
     byte[]      cryptoKey)
  {
    this(name,
         new PropertySet(),
         synapseName,
         activators,
         cryptoKey);
  }
}
