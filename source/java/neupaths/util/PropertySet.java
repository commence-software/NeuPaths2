// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe dictionary of (<i>name</i>, <i>value</i>) pairs.  Values
 * are stored as {@link java.lang.Object} references.
 * <p>
 * This type is serializable.  Note that all object types stored in a
 * PropertySet must also be serializable.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public final class PropertySet implements Serializable, Iterable<Map.Entry<String, Object>>
{
  /**
   * Allocates an empty {@code PropertySet} dictionary object.
   */
  public
  PropertySet ()
  {
    properties = new HashMap<>();
  }
  
  /**
   * Allocates a new {@code PropertySet} object and populates it with the
   * specified property set's values.
   * 
   * @param ps The source property set.
   */
  public
  PropertySet (PropertySet ps)
  {
    properties = new HashMap<>();
    
    for (Map.Entry<String, Object> entry : ps)
    {
      properties.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Sets a property value. {@code Null} is a valid value.
   * 
   * @param name  The property's name.
   * @param value The property's value.
   */  
  public synchronized
  void
  set (String name, Object value)
  {
    if (value != null)
    {
      properties.put(name, value);
    }
  }
  
  /**
   * Retrieves a property's value.  {@code Null} is returned if the
   * property does not exist.  {@code Null} is also a valid value.
   * Use {@link #contains} to determine if the property exists.
   * 
   * @param name The property to retrieve.
   * @return     The property's value.
   */
  public synchronized
  <T> T
  get (String name)
  {
    return (T) properties.get(name);
  }
  
  /**
   * Removes the named property.
   * 
   * @param name The property to remove.
   */
  public synchronized
  void
  remove (String name)
  {
    properties.remove(name);
  }
  
  /**
   * Indicates if property set contains the specified property.
   * 
   * @param name The property's name
   * @return {@code true} if set contains property, {@code false} otherwise.
   */
  public synchronized
  boolean
  contains (String name)
  {
    return properties.containsKey(name);
  }

  /**
   * Indicates if the property set is empty.
   *
   * @return {@code true} if empty, {@code false} otherwise.
   */
  public synchronized
  boolean
  isEmpty ()
  {
    return properties.isEmpty();
  }
  
  /**
   * Provides an iterator for the dictionary.
   * 
   * @return The property set's iterator.
   */
  @Override
  public
  Iterator<Map.Entry<String, Object>>
  iterator ()
  {
    return properties.entrySet().iterator();
  }
  
  /**
   * Prints the property set's contents to standard output.
   */
  public synchronized
  void
  dump ()
  {
    Set<Map.Entry<String, Object>> entries = properties.entrySet();
    System.out.println("Dumping properties...");
    for (Map.Entry<String, Object> entry : entries)
    {
      System.out.println("  \"" + entry.getKey() + "\"=\"" + entry.getValue() + "\"");
    }
    System.out.println("done.");
  }
  
  private HashMap<String, Object> properties;

  private static final long serialVersionUID = 1233280750566581906L;
}
