// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * The runtime exception raised by all public NeuPaths interfaces.
 * <p>
 * {@code NeuPathsException} will be raised under the following
 * conditions:
 * </p>
 * <ul>
 * <li>
 * For unspecified parameters that are required (e.g. <i>null</i> values
 * where values are expected.)<p>
 * </li>
 * <li>
 * For incorrectly specified parameters (e.g. a malformed synapse name.)<p>
 * </li>
 * <li>
 * For runtime errors prior to agent execution (e.g. failure to create a
 * synapse during cell construction.)
 * </li>
 * </ul>
 * <p>
 * As most NeuPaths operations are performed by autonomous agents, exceptions
 * are not useful in most cases.  The cases above identify programmer and system
 * errors, which must be resolved for the NeuPaths system to operate.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public final class NeuPathsException extends RuntimeException
{
  NeuPathsException ()
  {
    super("NeuPaths runtime error");
  }

  NeuPathsException (String message)
  {
    super("NeuPaths runtime error: " + message);
  }

  NeuPathsException (Throwable cause)
  {
    super("NeuPaths runtime error", cause);
  }

  NeuPathsException (String message, Throwable cause)
  {
    super("NeuPaths runtime error: " + message, cause);
  }

  NeuPathsException (CellType type, String message)
  {
    super("NeuPaths runtime error in " + type + ": " + message);
  }

  NeuPathsException
    (CellType type,
     String   name,
     String   message)
  {
    super("NeuPaths runtime error in " + type + " '" + name + "': " + message);
  }

  NeuPathsException
    (CellType  type,
     String    name,
     String    message,
     Throwable cause)
  {
    super("NeuPaths runtime error in " + type + " '" + name + "': " + message, cause);
  }

  /**
   * Provides the detail message, the chain of exceptions that resulted
   * in this {@code RuntimeException}, and a traceback for the root cause.
   * 
   * @return The detail message, chain of exceptions and root cause traceback.
   */
  public
  String
  getDetails ()
  {
    String causeTrace = "";
    Throwable currCause = this;
    while (currCause != null)
    {
      // If root cause, get stack trace
      if (currCause.getCause() == null)
      {
        for (StackTraceElement e : currCause.getStackTrace())
        {
          causeTrace += "\n  " + e;
        }
      }
      
      currCause = currCause.getCause();
    }

    String allDetails = getMessage();
    if (getCause() != null)
      allDetails += causeTrace;

    return allDetails;    
  }
  
  /**
   * Provides the detail message and the chain of exceptions that resulted
   * in this {@code RuntimeException}.
   * 
   * @return The detail message and chain of exceptions.
   */
  @Override
  public
  String
  getMessage ()
  {
    String causeChain = "";
    Throwable currCause = getCause();
    while (currCause != null)
    {
      causeChain += "\n=> " + currCause;
      currCause = currCause.getCause();
    }

    String message = super.getMessage();
    if (getCause() != null)
      message += causeChain;

    return message;
  }
  
  static final long serialVersionUID = -7796137406905478893L;
}
