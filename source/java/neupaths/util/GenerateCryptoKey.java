// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import neupaths.api.Cryp;

/**
 * Utility for creating a new encryption key.
 * <p>
 * Generates a Java source file containing a static member with the key data
 * and a raw data file with the key data.  The Java source file can be used
 * to specify a crypto key for cells that are created programmatically.
 * The data file can be used by utilities and during deployment
 * by specifying the file name in a cell definition XML file.
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.util.GenerateCryptoKey <cipher> [<cryptoKeyName>]}</pre>
 * <p>
 * <i>cipher</i>: Type of cipher.  Currently allowed: AES or Blowfish.
 * </p>
 * <p>
 * <i>cryptoKeyName</i>: Optional name of crypto key.  If not specified, the
 * crypto key name defaults to {@code CryptoKey}.  Two files are generated:
 * <i>cryptoKeyName</i>.java and <i>cryptoKeyName</i>.dat.
 * </p>
 * </ul>
 * 
 * @author Aaron Caraveo
 */
public class GenerateCryptoKey
{
  private GenerateCryptoKey ()
  {
    // Construction is not necessary
  }
  
  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
    String className = "CryptoKey";
    KeyGenerator keyGen = null;
    SecretKey key = null;
    byte[] keyData = null;
    byte[] algData = null;
    int i = 0;
    
    if (args.length == 0 || args.length > 2)
    {
      System.out.println("usage: java -classpath neupaths.jar neupaths.util.GenerateCryptoKey <cipher> [<cryptoKeyName>]");
      System.out.println("  cipher - Allowed values: AES or Blowfish");
      return;
    }
    
    if (args.length == 2)
    {
      className = args[1];
    }

    try
    {
      if (args[0].equals("AES"))
      {
        keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128, new SecureRandom());
        key = keyGen.generateKey();
        keyData = key.getEncoded();
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        // Collect data for the key
        // The key will consist of:
        //    1 byte  for the cipher type
        //   16 bytes for the initialization vector (128 bits)
        //   16 bytes for the algorithm key (128 bits)
        algData = new byte[1 + keyData.length + iv.length];

        // Add the cipher type and initialization vector
        algData[0] = Cryp.AES_CIPHER;
        for (byte b: iv)
        {
          algData[++i] = b;
        }
      }
      else if (args[0].equals("Blowfish"))
      {
        keyGen = KeyGenerator.getInstance("Blowfish");
        key = keyGen.generateKey();
        keyData = key.getEncoded();

        // Collect data for the key
        // The key will consist of:
        //    1 byte  for the cipher type
        //   16 bytes for the algorithm key (128 bits)
        algData = new byte[1 + keyData.length];

        // Add the cihper type
        algData[0] = Cryp.BLOWFISH_CIPHER;
      }
      else
      {
        System.out.println("ERROR: Invalid cipher '" + args[0] + "'");
        return;
      }

      // Add the key data
      for (byte b: keyData)
      {
        algData[++i] = b;
      }
      
      // Generate Java source file
      
      System.out.print("Generating source file ... ");
      
      File sourceFile = new File(className + ".java");
      PrintWriter sourceOutput = new PrintWriter(new FileWriter(sourceFile));
      
      sourceOutput.println("//Put your package name here");
      sourceOutput.println("//package ...\n");
      
      sourceOutput.println("public class " + className);
      sourceOutput.println("{");
      sourceOutput.println("  public static final byte[] data = {");
      
      boolean firstValue = true;
      
      for (Byte b : algData)
      {
        String hexVal = Integer.toHexString(b);

        if (firstValue)
        {
          sourceOutput.print("      0x" + hexVal);

          firstValue = false;
        }
        else
        {
          sourceOutput.print(",\n      0x" + hexVal);
        }
      }
      
      sourceOutput.println("\n    };");
      sourceOutput.println("}");
      
      sourceOutput.flush();
      sourceOutput.close();
      
      System.out.println("done.");
      
      // Generate data file
      
      System.out.print("Generating data file ... ");
      
      File dataFile = new File(className + ".dat");
      FileOutputStream dataOutput = new FileOutputStream(dataFile);
      dataOutput.write(algData);
      dataOutput.flush();
      dataOutput.close();
      
      System.out.println("done.");
    }
    catch (NoSuchAlgorithmException nsae)
    {
      System.out.println("error: could not generate files: " + nsae);
    }
    catch (IOException ioe)
    {
      System.out.println("error: could not generate files: " + ioe);
    }
  }
}
