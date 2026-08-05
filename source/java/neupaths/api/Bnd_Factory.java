// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;

/**
 * Creates a binder according to the synapse name.
 *
 * @author Aaron Caraveo
 */
final class Bnd_Factory
{
  static
  Bnd_Binder
  createBinder
    (String              cellName,
     UUID                cellInstanceID,
     String              synapseName,
     SubscriptionSpecSet subscriptions)
    throws Excp_Binder
  {
    Syn_Name synName = null;
    
    try
    {
      synName = new Syn_Name(synapseName);
    }
    catch (Excp_SynapseFatal tfe)
    {
      throw new Excp_Binder("Synapse Name is invalid", tfe);
    }
    
    return createBinder(cellName,
                        cellInstanceID,
                        synName,
                        subscriptions);
  }
  
  static
  Bnd_Binder
  createBinder
    (String              cellName,
     UUID                cellInstanceID,
     Syn_Name            synName,
     SubscriptionSpecSet subscriptions)
    throws Excp_Binder
  {
    Bnd_Binder binder = null;
    
    switch (synName.getType())
    {
      case STREAM:
        switch (synName.getMode())
        {
          case PEER:
            binder = new Bnd_StreamPeer(cellName,
                                        cellInstanceID,
                                        synName,
                                        subscriptions);
            break;
          case LISTENER:
            binder = new Bnd_StreamListener(cellName,
                                            cellInstanceID,
                                            synName,
                                            subscriptions);
            break;
        }
        break;
      case UNICAST:
        switch (synName.getMode())
        {
          case PEER:
            binder = new Bnd_UnicastPeer(cellName,
                                         cellInstanceID,
                                         synName,
                                         subscriptions);
            break;
          case LISTENER:
            binder = new Bnd_UnicastListener(cellName,
                                             cellInstanceID,
                                             synName,
                                             subscriptions);
            break;
        }
        break;
      case MULTICAST:
        switch (synName.getMode())
        {
          case PEER:
            binder = new Bnd_MulticastPeer(cellName,
                                           cellInstanceID,
                                           synName,
                                           subscriptions);
            break;
          case LISTENER:
            break;
        }
        break;
    }
    
    return binder;
  }
}
