// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Interface used by NeuPaths ciphers.
 *
 * @author Aaron Caraveo
 */
interface Cryp_Cipher
{
  public Object encrypt (Object data) throws Excp_Cipher;
  public Object decrypt (Object data) throws Excp_Cipher;
}
