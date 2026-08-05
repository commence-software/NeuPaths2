// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * A thread-safe integer counter.
 * 
 * @author Aaron Caraveo
 */
final class IntegerCounter extends SynchronizedValue<Integer>
{
  /**
   * Allocates a new {@code IntegerCounter} object with an initial value of zero.
   */
  public
  IntegerCounter ()
  {
    super(Integer.valueOf(0));
  }

  // Inherits synchronized setValue() and getValue() methods
  
  /**
   * Increments the counter.
   */
  public synchronized
  void
  increment ()
  {
    int count = value.intValue();
    count += 1;
    value = Integer.valueOf(count);
  }
  
  /**
   * Resets the counter to zero.
   */
  public
  void
  reset ()
  {
    setValue(Integer.valueOf(0));
  }
  
  /**
   * Retrieves the current value of the counter.
   * @return The current value.
   */
  public
  int
  count ()
  {
    return getValue().intValue();
  }
}
