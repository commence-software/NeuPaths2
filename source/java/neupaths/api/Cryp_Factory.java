// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Creates a NeuPaths cipher according to the specified secret key data.
 *
 * @author Aaron Caraveo
 */
final class Cryp_Factory
{
  static
  Cryp_Cipher
  createCipher (byte[] k) throws Excp_Cipher
  {
    // Strip first byte from key data
    byte[] keyData = java.util.Arrays.copyOfRange(k, 1, k.length);
    Cryp_Cipher cipher = null;

    // Check value of first byte
    switch (k[0])
    {
      case Cryp.AES_CIPHER:
        cipher = new Cryp_AES(keyData);
        break;
      case Cryp.BLOWFISH_CIPHER:
        cipher = new Cryp_Blowfish(keyData);
        break;
      default:
        throw new Excp_Cipher("Invalid cipher");
    }

    return cipher;
  }
}
