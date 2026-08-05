// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.function.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * The base class for NeuPaths subscription specification sets.
 *
 * @author Aaron Caraveo
 */
class SubscriptionSpecSet implements Iterable<SubscriptionSpec>
{
  /**
   * Creates an empty subscription set.
   */
  SubscriptionSpecSet ()
  {
    data = new HashSet<>();
  }
  
  /**
   * Creates a subscription set with a single element.
   * 
   * @param subscription The subscription.
   */
  SubscriptionSpecSet (SubscriptionSpec subscription)
  {
    if (subscription == null)
    {
      throw new NeuPathsException("Parameter 'subscription' is required");
    }
    
    data = new HashSet<>();
    data.add(subscription);
  }

  /**
   * Creates a subscription set from an array of subscription specifications.
   * 
   * @param subscriptions The array of subscription specifications.
   */
  SubscriptionSpecSet (SubscriptionSpec[] subscriptions)
  {
    if (subscriptions == null)
    {
      throw new NeuPathsException("Parameter 'subscriptions' is required");
    }
    
    data = new HashSet<>();
    
    for (SubscriptionSpec subscription : subscriptions)
    {
      if (subscription == null)
      {
        throw new NeuPathsException("Null SubscriptionSpec specified");
      }
    
      data.add(subscription);
    }
  }
  
  /**
   * Adds all subscriptions in the specified set to this set.
   * 
   * @param subscriptions The subscription set.
   */
  public
  void
  add (SubscriptionSpecSet subscriptions)
  {
    if (subscriptions == null)
    {
      throw new NeuPathsException("Parameter 'subscriptions' is required");
    }
    
    for (SubscriptionSpec subscription : subscriptions)
    {
      data.add(subscription);
    }
  }
  
  /**
   * Adds all subscriptions in the specified array to this set.
   * 
   * @param subscriptions The subscription array.
   */
  public
  void
  add (SubscriptionSpec[] subscriptions)
  {
    if (subscriptions == null)
    {
      throw new NeuPathsException("Parameter 'subscriptions' is required");
    }
    
    for (SubscriptionSpec subscription : subscriptions)
    {
      if (subscription == null)
      {
        throw new NeuPathsException("Null SubscriptionSpec specified");
      }
    
      data.add(subscription);
    }
  }
  
  /**
   * Adds a subscription to the set.
   * 
   * @param subscription The subscription.
   */
  public
  void
  add (SubscriptionSpec subscription)
  {
    if (subscription == null)
    {
      throw new NeuPathsException("Parameter 'subscription' is required");
    }
    
    data.add(subscription);    
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
  Iterator<SubscriptionSpec>
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
  
  private HashSet<SubscriptionSpec> data;
}
