// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * Maintains a collection of {@link TransmitterSpec} objects.
 * This type is passed to {@link Activator} constructors.
 * 
 * @author Aaron Caraveo
 */
final class TransmitterSpecSet implements Iterable<TransmitterSpec>
{
  /**
   * Creates an empty transmitter set.
   */
  public
  TransmitterSpecSet ()
  {
    data = new HashSet<TransmitterSpec>();
  }
  
  /**
   * Creates a transmitter set with a single element.
   * 
   * @param transmitter The transmitter.
   */
  public
  TransmitterSpecSet (TransmitterSpec transmitter)
  {
    if (transmitter == null)
    {
      throw new NeuPathsException("Parameter 'transmitter' is required");
    }
    
    data = new HashSet<TransmitterSpec>();
    data.add(transmitter);    
  }
  
  /**
   * Creates a transmitter set with a single element.
   * Specifies the name and stimulus type ID.  {@code trace} defaults to
   * {@link StimulusTrace#ENABLED}.
   * 
   * @param name           The name of the transmitter.
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   */
  public
  TransmitterSpecSet (String name, UUID stimulusTypeID)
  {
    data = new HashSet<TransmitterSpec>();
    data.add(new TransmitterSpec(name,
                                 stimulusTypeID));
  }
  
  /**
   * Creates a transmitter set with a single element.
   * 
   * @param name           The name of the transmitter.
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   * @param trace          The trace flag.  A value of
   *                       {@link StimulusTrace#ENABLED} requests that
   *                       synapse path trace be recorded.
   */
  public
  TransmitterSpecSet
    (String        name,
     UUID          stimulusTypeID,
     StimulusTrace trace)
  {
    data = new HashSet<TransmitterSpec>();
    data.add(new TransmitterSpec(name,
                                 stimulusTypeID,
                                 trace));
  }

  /**
   * Creates a transmitter set from an array of transmitter specifications.
   * 
   * @param transmitters The array of transmitter specifications.
   */
  public
  TransmitterSpecSet (TransmitterSpec[] transmitters)
  {
    if (transmitters == null)
    {
      throw new NeuPathsException("Parameter 'transmitters' is required");
    }
    
    data = new HashSet<TransmitterSpec>();
    
    for (TransmitterSpec transmitter : transmitters)
    {
      if (transmitter == null)
      {
        throw new NeuPathsException("Null TransmitterSpec specified");
      }
    
      for (TransmitterSpec t : data)
      {
        if (t.getName().equals(transmitter.getName()))
          throw new NeuPathsException("Transmitter " + transmitter.getName() +
                                         " defined multiple times");
      }
      
      data.add(transmitter);
    }
  }
  
  /**
   * Adds all transmitters in the specified set to this set.
   * 
   * @param transmitters The transmitter set.
   */
  public
  void
  add (TransmitterSpecSet transmitters)
  {
    if (transmitters == null)
    {
      throw new NeuPathsException("Parameter 'transmitters' is required");
    }
    
    for (TransmitterSpec transmitter : transmitters)
    {
      for (TransmitterSpec t : data)
      {
        if (t.getName().equals(transmitter.getName()))
          throw new NeuPathsException("Transmitter " + transmitter.getName() +
                                         " defined multiple times");
      }
      
      data.add(transmitter);
    }
  }
  
  /**
   * Adds all transmitters in the specified array to this set.
   * 
   * @param transmitters The transmitter array.
   */
  public
  void
  add (TransmitterSpec[] transmitters)
  {
    if (transmitters == null)
    {
      throw new NeuPathsException("Parameter 'transmitters' is required");
    }
    
    for (TransmitterSpec transmitter : transmitters)
    {
      if (transmitter == null)
      {
        throw new NeuPathsException("Null TransmitterSpec specified");
      }
    
      for (TransmitterSpec t : data)
      {
        if (t.getName().equals(transmitter.getName()))
          throw new NeuPathsException("Transmitter " + transmitter.getName() +
                                         " defined multiple times");
      }
      
      data.add(transmitter);
    }
  }
  
  /**
   * Adds a transmitter to the set.
   * 
   * @param transmitter The transmitter.
   */
  public
  void
  add (TransmitterSpec transmitter)
  {
    if (transmitter == null)
    {
      throw new NeuPathsException("Parameter 'transmitter' is required");
    }
    
    for (TransmitterSpec t : data)
    {
      if (t.getName().equals(transmitter.getName()))
        throw new NeuPathsException("Transmitter " + transmitter.getName() +
                                       " defined multiple times");
    }

    data.add(transmitter);    
  }
  
  /**
   * Adds a transmitter to the set.
   * Specifies the name and stimulus type ID.  {@code trace} defaults to
   * {@link StimulusTrace#ENABLED}.
   * 
   * @param name           The name of the transmitter.
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   */
  public
  void
  add (String name, UUID stimulusTypeID)
  {
    for (TransmitterSpec t : data)
    {
      if (t.getName().equals(name))
        throw new NeuPathsException("Transmitter " + name +
                                       " defined multiple times");
    }

    data.add(new TransmitterSpec(name,
                                 stimulusTypeID));
  }
  
  /**
   * Adds a transmitter to the set.
   * 
   * @param name           The name of the transmitter.
   * @param stimulusTypeID The ID of the stimulus type accepted by the transmitter.
   * @param trace          The trace flag.  A value of
   *                       {@link StimulusTrace#ENABLED} requests that
   *                       synapse path trace be recorded.
   */
  public
  void
  add
    (String        name,
     UUID          stimulusTypeID,
     StimulusTrace trace)
  {
    for (TransmitterSpec t : data)
    {
      if (t.getName().equals(name))
        throw new NeuPathsException("Transmitter " + name +
                                       " defined multiple times");
    }

    data.add(new TransmitterSpec(name,
                                 stimulusTypeID,
                                 trace));
  }
  
  /**
   * Indicates if the set is empty.
   * 
   * @return {@code true} if empty, {@code false} otherwise.
   */
  public
  boolean
  isEmpty ()
  {
    return data.isEmpty();
  }

  /**
   * Returns an iterator for the set.
   * 
   * @return The iterator.
   */
  @Override
  public
  Iterator<TransmitterSpec>
  iterator ()
  {
    return data.iterator();
  }
  
  public
  <T> T[]
  toArray (T[] a)
  {
    return data.toArray(a);
  }
  
  private HashSet<TransmitterSpec> data;
}
