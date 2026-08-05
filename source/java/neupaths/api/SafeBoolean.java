// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * A thread-safe Boolean flag.
 * 
 * @author Aaron Caraveo
 */
final class SafeBoolean extends SynchronizedValue<Boolean>
{
  /**
   * Creates a thread-safe Boolean flag with an initial value of {@code false}.
   */
  SafeBoolean ()
  {
    super(Boolean.FALSE);
  }
  
  /**
   * Creates a thread-safe Boolean flag.
   * 
   * @param value The initial value.
   */
  SafeBoolean (boolean value)
  {
    super(Boolean.valueOf(value));
  }
  
  // Inherits synchronized setValue() and getValue() methods
  
  /**
   * Sets the Boolean value to {@code true}.
   */
  void
  set ()
  {
    setValue(Boolean.TRUE);
  }
  
  /**
   * Sets the Boolean value to {@code false}.
   */
  void
  clear ()
  {
    setValue(Boolean.FALSE);
  }

  synchronized  
  void
  toggle ()
  {
    value = Boolean.valueOf(!value.booleanValue());
  }

  /**
   * Indicates if value is <i>true</i>.
   * 
   * @return {@code true} if Boolean value is <i>true</i>, {@code false} otherwise.
   */
  boolean
  isSet ()
  {
    return getValue().booleanValue();
  }
  
  /**
   * Indicates if value is <i>false</i>.
   * 
   * @return {@code true} if Boolean value is <i>false</i>, {@code false} otherwise.
   */
  boolean
  isNotSet ()
  {
    return !isSet();
  }
}
