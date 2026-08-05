// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * The base class for NeuPaths API exceptions.  This class can be used in
 * a catch clause to handle any exception raised by the API.
 * 
 * @author Aaron Caraveo
 */
class Excp_NeuPaths extends Exception
{
  Excp_NeuPaths ()
  {
    super("NeuPaths operation failed");
  }

  Excp_NeuPaths (String message)
  {
    super(message);
  }

  Excp_NeuPaths (Throwable cause)
  {
    super("NeuPaths operation failed", cause);
  }

  Excp_NeuPaths (String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Provides the detail message, the chain of exceptions that resulted
   * in this {@code Excp_NeuPaths}, and a traceback for the root cause.
   * 
   * @return The detail message, chain of exceptions and root cause traceback.
   */
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
   * in this {@code Excp_NeuPaths}.
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
  
  static final long serialVersionUID = 6423312535259523191L;
}
