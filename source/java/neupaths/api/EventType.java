// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * NeuPaths log event types.  Specifies the type of event to log.
 * See {@link Activator}.
 * <p>
 * The {@code AUDIT 1-9}, {@code INFORMATION}, {@code WARNING} and {@code ERROR}
 * types are considered user-level logging.  The {@code DEBUG} and {@code TRACE}
 * types can also be used by NeuPaths API clients, but both are disabled by default.
 * See {@link Cell} for a discussion of the logging facilities.  The
 * {@code RUNTIME} type is reserved for the NeuPaths runtime.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public enum EventType
{
  /**
   * Runtime information; Logged when a NeuPaths cell has enabled Runtime
   * logging; Reserved for internal use.
   */
  RUNTIME,
  
  /**
   * Trace information; Logged when a NeuPaths cell has enabled Trace
   * logging.
   */
  TRACE,
  
  /**
   * Debug information; Logged when a NeuPaths cell has enabled Debug logging.
   */
  DEBUG,

  /**
   * General information.
   */
  INFORMATION,
  
  /**
   * Information about a possible problem.
   */
  WARNING,
  
  /**
   * Information about an error.
   */
  ERROR,
  
  /**
   * Audit information level 1.
   */
  AUDIT1,
  
  /**
   * Audit information level 2.
   */
  AUDIT2,
  
  /**
   * Audit information level 3.
   */
  AUDIT3,
  
  /**
   * Audit information level 4.
   */
  AUDIT4,
  
  /**
   * Audit information level 5.
   */
  AUDIT5,
  
  /**
   * Audit information level 6.
   */
  AUDIT6,
  
  /**
   * Audit information level 7.
   */
  AUDIT7,
  
  /**
   * Audit information level 8.
   */
  AUDIT8,
  
  /**
   * Audit information level 9.
   */
  AUDIT9
}
