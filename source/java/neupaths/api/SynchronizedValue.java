// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * A thread-safe generic wrapper around a value.
 * 
 * @param <T> The type being protected.
 * 
 * @author Aaron Caraveo
 */
class SynchronizedValue<T>
{
  /**
   * Creates a thread-safe value with an initial value of <i>null</i>.
   */
  SynchronizedValue ()
  {
    value = null;
  }
  
  /**
   * Creates a thread-safe value.
   * 
   * @param value The initial value.
   */
  SynchronizedValue (T value)
  {
    this.value = value;
  }
  
  /**
   * Sets the value in a thread-safe manner.
   * 
   * @param value The value.
   */
  synchronized
  void
  setValue (T value)
  {
    this.value = value;
  }
  
  /**
   * Gets the value in a thread-safe manner.
   * 
   * @return The value.
   */
  synchronized
  T
  getValue ()
  {
    return value;
  }
  
  protected T value;
}
