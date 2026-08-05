// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.util.HashSet;

/**
 * Parses command-line arguments, placing flag and argument values in a
 * {@link PropertySet}.
 * 
 * @author Aaron Caraveo
 */
public class CommandLine
{
  private CommandLine ()
  {
    // Construction not supported
  }

  /**
   * Parses command-line arguments ensuring only expected flags are
   * provided.  For non-flag arguments, each is stored in key "optN",
   * where N is the instance number (starts at 1).  The number of
   * optional arguments is stored in key "optCnt".
   * 
   * @param args  The command-line arguments.
   * @param valid List of valid flags (exclude the leading '-' or '--').
   * @return      Dictionary of parsed arguments.  Each flag argument
   *              with a value will store the value under a key
   *              equal to the flag name (excluding the '-' or '--').
   *              Non-flag arguments are stored with keys "optN", where
   *              N is the instance number (starting at 1).
   */
  public static PropertySet parse (String[] args, String[] valid)
  {
    HashSet<String> validFlags = new HashSet<>();
    PropertySet arguments = new PropertySet();
    boolean error = false;
    String name = null;
    String value = null;
    int opt_cnt = 0;
    String opt_val = null;

    for (String flag : valid)
    {
      validFlags.add(flag);
    }
    
    for (String arg : args)
    {
      if (arg.length() >= 3 && arg.substring(0, 2).equals("--"))
      {
        if (name != null)
        {
          arguments.set(name, "");
        }
        
        name = arg.substring(2);
        if (!validFlags.contains(name))
        {
          error = true;
          break;
        }
      }
      else if (arg.length() >= 2 && arg.charAt(0) == '-')
      {
        if (name != null)
        {
          arguments.set(name, "");
        }
        
        name = arg.substring(1);
        if (!validFlags.contains(name))
        {
          error = true;
          break;
        }
      }
      else
      {
        if (name != null)
        {
          arguments.set(name, arg);
          name = null;
        }
        else
        {
          opt_cnt++;
          
          arguments.set("opt" + opt_cnt, arg);
        }
      }
    }
    
    arguments.set("optCnt", Integer.valueOf(opt_cnt).toString());

    if (error)
      return null;
    else
      return arguments;
  }
}
