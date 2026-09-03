// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.stim;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import neupaths.api.Stimulus;

/**
 * A NeuPaths stimulus type that encapsulates a dictionary.
 *
 * @author Aaron Caraveo
 */
public final class DictionaryStimulus extends Stimulus
{
  /**
   * Allocates a new {@code DictionaryStimulus} object that is empty.
   */
  public DictionaryStimulus ()
  {
    super(TYPE_NAME, TYPE_ID);
    map = new HashMap<>();
  }

  /**
   * Allocates a new {@code DictionaryStimulus} object with the specified
   * dictionary.
   *
   * @param dict The dictionary to use.
   */
  public DictionaryStimulus (Map<String, Object> dict)
  {
    super(TYPE_NAME, TYPE_ID);
    this.map = new HashMap<>(dict);
  }

  /**
   * Allocates a new {@code DictionaryStimulus} object that is empty.
   * This constructor can be used to create aliases of this stimulus type.
   * The alias will extend this class, thereby using the same TYPE_ID but
   * having a new type name.
   * 
   * @param typeName The name of the alias type.
   */
  protected DictionaryStimulus (String typeName)
  {
    super(typeName, TYPE_ID);
    map = new HashMap<>();
  }

  /**
   * Allocates a new {@code DictionaryStimulus} object with the specified
   * dictionary.  This constructor can be used to create aliases of this
   * stimulus type.  The alias will extend this class, thereby using the same
   * TYPE_ID but having a new type name.
   * 
   * @param typeName The name of the alias type.
   * @param dict     The dictionary to use.
   */
  protected DictionaryStimulus (String typeName, Map<String, Object> dict)
  {
    super(typeName, TYPE_ID);
    this.map = new HashMap<>(dict);
  }

  /**
   * Retrieves a value from the dictionary.
   *
   * @param name The dictionary value to retrieve
   */
  public <T> T get (String name)
  {
    return (T) map.get(name);
  }
  
  /**
   * Adds a value to the dictionary.
   *
   * @param name  The value's name
   * @param value The value
   */
  public void put (String name, Object value)
  {
    map.put(name, value);
  }
  
  public String toString()
  {
    return TYPE_NAME + "(" + getInstanceID() + ") " + map;
  }

  public
  Set<Map.Entry<String, Object>>
  entries ()
  {
    return map.entrySet();
  }
  
  private HashMap<String, Object> map;
  
  public static final String TYPE_NAME = "DictionaryStimulus";
  public static final UUID TYPE_ID = UUID.fromString("e795b00a-6a62-464d-847e-aa7f6e3f31bb");
}
