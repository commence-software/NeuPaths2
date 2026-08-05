// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * A null-op NeuPaths ciper.
 *
 * @author Aaron Caraveo
 */
class Cryp_Null implements Cryp_Cipher
{
  Cryp_Null ()
  {
    // Nothing to do... this is a null-op cypher
  }
  
  @Override
  public synchronized Object encrypt (Object data) throws Excp_Cipher
  {
    return data;
  }
  
  @Override
  public synchronized Object decrypt (Object data) throws Excp_Cipher
  {
    return data;
  }
}
