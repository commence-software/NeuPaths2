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
 * NeuPaths cipher that uses the Blowfish algorithm.
 *
 * @author Aaron Caraveo
 */
class Cryp_Blowfish implements Cryp_Cipher
{
  Cryp_Blowfish (byte[] k) throws Excp_Cipher
  {
    try
    {
      keySpec = new SecretKeySpec(k, "Blowfish");
      cipher = Cipher.getInstance("Blowfish");
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
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      return new SealedObject((Serializable)data, cipher);
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
      cipher.init(Cipher.DECRYPT_MODE, keySpec);
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
  
  private SecretKeySpec keySpec;
  private Cipher cipher;
}
