// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * NeuPaths cipher that uses the AES algorithm.
 *
 * @author Aaron Caraveo
 */
class Cryp_AES implements Cryp_Cipher
{
  Cryp_AES (byte[] k) throws Excp_Cipher
  {
    try
    {
      // Grab 1st 16 bytes as the initialization vector
      byte[] iv = java.util.Arrays.copyOfRange(k, 0, 16);

      // Grab remaining bytes as the key data
      byte[] keyData = java.util.Arrays.copyOfRange(k, 16, k.length);
      
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      keySpec = new SecretKeySpec(keyData, "AES");
      ivSpec = new IvParameterSpec(iv);
    }
    catch (NoSuchAlgorithmException nsae)
    {
      throw new Excp_Cipher(nsae);
    }
    catch (NoSuchPaddingException nspe)
    {
      throw new Excp_Cipher(nspe);
    }
  }
  
  @Override
  public synchronized Object encrypt (Object data) throws Excp_Cipher
  {
    try
    {
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
      return new SealedObject((Serializable)data, cipher);
    }
    catch (InvalidAlgorithmParameterException iape)
    {
      throw new Excp_Cipher(iape);
    }
    catch (InvalidKeyException ike)
    {
      throw new Excp_Cipher(ike);
    }
    catch (IllegalBlockSizeException ibse)
    {
      throw new Excp_Cipher(ibse);            
    }
    catch (IOException ioe)
    {
      throw new Excp_Cipher(ioe);
    }
  }
  
  @Override
  public synchronized Object decrypt (Object data) throws Excp_Cipher
  {
    try
    {
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      return ((SealedObject)data).getObject(cipher);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new Excp_Cipher(cnfe);
    }
    catch (BadPaddingException bpe)
    {
      throw new Excp_Cipher(bpe);
    }
    catch (InvalidAlgorithmParameterException iape)
    {
      throw new Excp_Cipher(iape);
    }
    catch (InvalidKeyException ike)
    {
      throw new Excp_Cipher(ike);
    }
    catch (IllegalBlockSizeException ibse)
    {
      throw new Excp_Cipher(ibse);            
    }
    catch (IOException ioe)
    {
      throw new Excp_Cipher(ioe);
    }
  }
  
  private Cipher cipher;
  private SecretKeySpec keySpec;
  private IvParameterSpec ivSpec;
}
