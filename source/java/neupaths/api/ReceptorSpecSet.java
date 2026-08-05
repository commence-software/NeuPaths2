// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.function.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * Maintains a collection of {@link ReceptorSpec} objects.
 * This type is passed to {@link Activator} constructors.
 *
 * @author Aaron Caraveo
 */
final class ReceptorSpecSet implements Iterable<ReceptorSpec>
{
  /**
   * Creates an empty receptor set.
   */
  public
  ReceptorSpecSet ()
  {
    data = new HashSet<ReceptorSpec>();
  }
  
  /**
   * Creates a receptor set with a single element.
   * 
   * @param receptor The receptor.
   */
  public
  ReceptorSpecSet (ReceptorSpec receptor)
  {
    if (receptor == null)
    {
      throw new NeuPathsException("Parameter 'receptor' is required");
    }
    
    data = new HashSet<ReceptorSpec>();
    data.add(receptor);
  }
  
  /**
   * Creates a receptor set with a single element.
   * 
   * @param name           The name of the receptor.
   * @param mode           The mode of the receptor.
   * @param stimulusTypeID The ID of the stimulus type accepted by the receptor.
   */
  public
  ReceptorSpecSet
    (String       name,
     ReceptorMode mode,
     UUID         stimulusTypeID)
  {
    data = new HashSet<ReceptorSpec>();
    data.add(new ReceptorSpec(name,  mode, stimulusTypeID));
  }

  /**
   * Creates a receptor set from an array of receptor specifications.
   * 
   * @param receptors The array of receptor specifications.
   */
  public
  ReceptorSpecSet (ReceptorSpec[] receptors)
  {
    if (receptors == null)
    {
      throw new NeuPathsException("Parameter 'receptors' is required");
    }
    
    data = new HashSet<ReceptorSpec>();
    
    for (ReceptorSpec receptor : receptors)
    {
      if (receptor == null)
      {
        throw new NeuPathsException("Null ReceptorSpec specified");
      }
    
      for (ReceptorSpec r : data)
      {
        if (r.getName().equals(receptor.getName()))
          throw new NeuPathsException("Receptor " + receptor.getName() +
                                         " defined multiple times");
      }
      
      data.add(receptor);
    }
  }
  
  /**
   * Adds all receptors in the specified set to this set.
   * 
   * @param receptors The receptor set.
   */
  public
  void
  add (ReceptorSpecSet receptors)
  {
    if (receptors == null)
    {
      throw new NeuPathsException("Parameter 'receptors' is required");
    }
    
    for (ReceptorSpec receptor : receptors)
    {
      for (ReceptorSpec r : data)
      {
        if (r.getName().equals(receptor.getName()))
          throw new NeuPathsException("Receptor " + receptor.getName() +
                                         " defined multiple times");
      }
      
      data.add(receptor);
    }
  }

  /**
   * Adds all receptors in the specified array to this set.
   * 
   * @param receptors The receptor array.
   */
  public
  void
  add (ReceptorSpec[] receptors)
  {
    if (receptors == null)
    {
      throw new NeuPathsException("Parameter 'receptors' is required");
    }
    
    for (ReceptorSpec receptor : receptors)
    {
      if (receptor == null)
      {
        throw new NeuPathsException("Null ReceptorSpec specified");
      }
    
      for (ReceptorSpec r : data)
      {
        if (r.getName().equals(receptor.getName()))
          throw new NeuPathsException("Receptor " + receptor.getName() +
                                         " defined multiple times");
      }
      
      data.add(receptor);
    }
  }

  /**
   * Adds a receptor to the set.
   * 
   * @param receptor The receptor to add.
   */
  public
  void
  add (ReceptorSpec receptor)
  {
    if (receptor == null)
    {
      throw new NeuPathsException("Parameter 'receptor' is required");
    }
    
    for (ReceptorSpec r : data)
    {
      if (r.getName().equals(receptor.getName()))
        throw new NeuPathsException("Receptor " + receptor.getName() +
                                       " defined multiple times");
    }
    
    data.add(receptor);
  }

  /**
   * Adds a receptor to the set.
   * 
   * @param name           The name of the receptor.
   * @param mode           The mode of the receptor.
   * @param stimulusTypeID The ID of the stimulus type accepted by the receptor.
   */
  public
  void
  add
    (String       name,
     ReceptorMode mode,
     UUID         stimulusTypeID)
  {
    for (ReceptorSpec r : data)
    {
      if (r.getName().equals(name))
        throw new NeuPathsException("Receptor " + name +
                                       " defined multiple times");
    }
    
    data.add(new ReceptorSpec(name, mode, stimulusTypeID));
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
  Iterator<ReceptorSpec>
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

  public
  <T> T[]
  toArray(IntFunction<T[]> generator)
  {
    return toArray(generator.apply(0));
  }
  
  private HashSet<ReceptorSpec> data;
}
