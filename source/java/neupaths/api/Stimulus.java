// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.Serializable;
import java.util.UUID;

/**
 * The base class for NeuPaths stimulus types.  A stimulus is an atomic data
 * element that is produced and consumed by cells.  All stimulus types in a
 * NeuPaths system must be derived from the {@code Stimulus} class.
 * <h2>Defining a Stimulus Type</h2>
 * <p>
 * Each stimulus type should have a unique ID.  The NeuPaths runtime performs
 * type checking at the time of stimulus receipt and transmission.  If a
 * stimulus does not match the type of the receptor or transmitter, an
 * error will be logged and the stimulus will be discarded.
 * </p>
 * <p>
 * A convention has been established for defining stimulus type names and IDs:
 * Each extending class of {@code Stimulus} should define a
 * {@code pubic static final} value for the type name and ID.  These values
 * should be named {@code TYPE_NAME} and {@code TYPE_ID} respectively.  The
 * {@code TYPE_ID} value should be a unique UUID.  Stimulus types that have
 * the same type ID but different type names are considered equivalent (i.e.
 * they are considered aliases.)  The NeuPaths runtime expects aliases to have the
 * same internal representation, so using the same type ID for two different
 * internal representations will result in an error and discarded stimulus.
 * The proper way to create a stimulus type alias is to extend the original
 * stimulus type and use a different type name.
 * The {@link neupaths.util.GenerateStimulusType} tool is
 * provided to simplify stimulus type creation.  The tool creates a basic
 * stimulus class with a unique TYPE_ID.  The developer can then tailor the
 * class as desired.  The generated class also has a constructor that can be
 * used to create an alias.
 * </p>
 * <p>
 * The developer should also generate a {@code serialVersionUID} for the
 * stimulus type using the JDK {@code serialver} tool.  This will ensure that
 * all serialized versions of the stimulus are equivalent.
 * </p>
 * <h2>Runtime</h2>
 * <p>
 * As previously mentioned, the NeuPaths runtime ensures that a stimulus type
 * matches the type specified for a receptor or transmitter.  Type mismatches
 * are logged and discarded.
 * </p>
 * <p>
 * Every instance of a {@code Stimulus}-derived class has a unique
 * instance ID that will not change during the lifespan of the stimulus.
 * This provides a facility for tracing stimuli through the cell
 * system.  Each time a stimulus is received by a receptor, a trace
 * event will be logged containing the instance ID and path through the system.
 * </p>
 * <p>
 * Two conditions must be met for the trace event to be logged.
 * </p>
 * <ol>
 * <li>
 * The {@code trace} flag in the {@link TransmitterSpec} for the sending
 * transmitter must be {@link StimulusTrace#ENABLED}.
 * </li>
 * <li>
 * Trace logging must be enabled for the cell where the stimulus is received.
 * </li>
 * </ol>
 * 
 * @author Aaron Caraveo
 */
public abstract class Stimulus implements Serializable
{
  /**
   * Creates a new {@code Stimulus} object.  All derived classes must use
   * this constructor.
   * 
   * @param typeName The type name for the stimulus.
   * @param typeID   The unique type ID for the stimulus.
   */
  protected
  Stimulus (String typeName, UUID typeID)
  {
    this.typeName = typeName;
    this.typeID = typeID;
    instanceID = UUID.randomUUID();
    transactionID = null;
    producerCellID = null;
    producerCellName = null;
    producerTransmitterName = null;
    consumerCellName = null;
  }

  /**
   * Returns the producing cell's ID.
   * 
   * @return The producing cell ID.
   */
  public final
  UUID
  getProducerCellID ()
  {
    return producerCellID;
  }
  
  /**
   * Returns the producing cell's name.
   * 
   * @return The producing cell name.
   */
  public final
  String
  getProducerCellName ()
  {
    return producerCellName;
  }
  
  /**
   * Returns the producing transmitter's name.
   * 
   * @return The producing transmitter name.
   */
  public final
  String
  getProducerTransmitterName ()
  {
    return producerTransmitterName;
  }

  /**
   * Returns the consuming cell's name.  This value is set when a stimulus is
   * placed on a cell receptor.
   * 
   * @return The consuming cell name.
   */  
  public final
  String
  getConsumerCellName ()
  {
    return consumerCellName;
  }
  
  /**
   * Returns the stimulus type name.
   * 
   * @return The stimulus type name.
   */
  public final
  String
  getTypeName ()
  {
    return typeName;
  }

  /**
   * Returns the stimulus type ID.
   * 
   * @return The stimulus type ID.
   */
  public final
  UUID
  getTypeID ()
  {
    return typeID;
  }
  
  /**
   * Returns the instance ID of this stimulus.
   * 
   * @return The stimulus instance ID.
   */
  public final
  UUID
  getInstanceID ()
  {
    return instanceID;
  }
  
  /**
   * Returns the transaction ID of this stimulus.
   * Transaction IDs can be set when injecting stimuli or transmitting stimuli
   * from activators.  Refer to the following methods for details:
   * <ul>
   * <li>{@link InjectorCell#injectWithTransaction}</li>
   * <li>{@link InjectorCell#injectAsTransaction}</li>
   * <li>{@link Activator#createTransaction}</li>
   * <li>{@link Activator#setStimulus}</li>
   * </ul>
   * 
   * @return The stimulus transaction ID.  Will return {@code null} when the
   *         stimulus is not associated with a transaction.
   */
  public final
  UUID
  getTransactionID ()
  {
    return transactionID;
  }
  
  /**
   * Sets the producing cell's ID.  Set automatically by the NeuPaths framework
   * when a stimulus is transmitted.
   * 
   * @param id The producing cell ID.
   */
  final
  void
  setProducerCellID (UUID id)
  {
    producerCellID = id;
  }
  
  /**
   * Sets the producing cell's name.  Set automatically by the NeuPaths framework
   * when a stimulus is transmitted.
   * 
   * @param name The producing cell name.
   */
  final
  void
  setProducerCellName (String name)
  {
    producerCellName = name;
  }
  
  /**
   * Sets the producing transmitter's name.  Set automatically by the NeuPaths 
   * framework when a stimulus is transmitted.
   * 
   * @param name The producing transmitter name.
   */
  final
  void
  setProducerTransmitterName (String name)
  {
    producerTransmitterName = name;
  }
  
  /**
   * Sets the consuming cell's name.  Set automatically by the NeuPaths framework
   * when a stimulus is received.
   * 
   * @param name The consuming cell name.
   */
  final
  void
  setConsumerCellName (String name)
  {
    consumerCellName = name;
  }
  
  /**
   * Sets the transaction ID.  Set by the NeuPaths framework when transmitting
   * stimuli with an associated transaction.
   * 
   * @param id The stimulus transaction ID.
   */
  final
  void
  setTransactionID (UUID id)
  {
    transactionID = id;
  }
  
  /**
   * Default override for NeuPaths stimulus types.
   * <p>
   * Derived stimulus types can override to provide details for logged events.
   * </p>
   * 
   * @return A string representation of the stimulus.
   */
  @Override
  public
  String
  toString ()
  {
    return
        "{Override toString() in class " + getClass().getName() + " for details}";
  }
  
  private final String typeName;
  private final UUID   typeID;
  private UUID         instanceID;
  private UUID         transactionID;
  private UUID         producerCellID;
  private String       producerCellName;
  private String       producerTransmitterName;
  private String       consumerCellName;

  static final long serialVersionUID = 8496950165203939318L;
}
