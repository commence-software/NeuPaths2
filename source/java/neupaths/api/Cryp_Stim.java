// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * The default stimulus cipher.  Used when a client does not provide
 * a cryptoKey.
 *
 * @author Aaron Caraveo
 */
final class Cryp_Stim extends Cryp_AES
{
  Cryp_Stim () throws Excp_Cipher
  {
    super(k);
  }
  
  private static final byte[] k = {
      0xffffffac,
      0xa,
      0xfffffff6,
      0x28,
      0x9,
      0xffffffec,
      0xfffffff6,
      0xffffffb3,
      0x1c,
      0xfffffffd,
      0xffffffd7,
      0x4e,
      0x1d,
      0xffffff89,
      0xffffffa1,
      0xffffff80,
      0x53,
      0xffffffc3,
      0x69,
      0x74,
      0x47,
      0xc,
      0x6e,
      0x44,
      0x3c,
      0x3f,
      0x60,
      0x7d,
      0x3b,
      0xffffffe6,
      0xffffffe7,
      0x3d
    };
}
