// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * A thread-safe Long value.
 * 
 * @author Aaron Caraveo
 */
class SafeLong extends SynchronizedValue<Long>
{
  /**
   * Creates a thread-safe Long value with an initial value of <i>zero</i>.
   */
  SafeLong ()
  {
    super(Long.valueOf(0L));
  }
  
  /**
   * Creates a thread-safe Long value.
   * 
   * @param value The initial value.
   */
  SafeLong (long value)
  {
    super(Long.valueOf(value));
  }
  
  /**
   * Creates a thread-safe Long value.
   * 
   * @param value The initial value.
   */
  SafeLong (Long value)
  {
    super(value);
  }
  
  // Inherits synchronized setValue() and getValue() methods
}
