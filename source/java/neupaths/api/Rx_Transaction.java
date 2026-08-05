// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * A collection of receptors for a particular transaction.
 *
 * @author Aaron Caraveo
 */
final class Rx_Transaction implements Iterable<Rx_Receptor>
{
  Rx_Transaction (UUID transactionID)
  {
    this.transactionID = transactionID;
    receptors = new HashMap<>();
  }
  
  Rx_Transaction (UUID transactionID, ReceptorSpecSet receptors)
    throws Excp_Receptor
  {
    if (receptors == null)
    {
      throw new Excp_Receptor("Parameter 'receptors' is required");
    }

    this.transactionID = transactionID;
    this.receptors = new HashMap<String, Rx_Receptor>();

    for (ReceptorSpec r : receptors)
    {
      this.receptors.put(r.getName(),
                         new Rx_Receptor(r.getName(),
                                         r.getMode(),
                                         r.getStimulusTypeID()));
    }
  }

  private
  Rx_Transaction (UUID transactionID, Collection<Rx_Receptor> receptors)
  {
    this.transactionID = transactionID;
    this.receptors = new HashMap<String, Rx_Receptor>();

    for (Rx_Receptor r : receptors)
    {
      this.receptors.put(r.getName(),
                         new Rx_Receptor(r.getName(),
                                         ReceptorMode.NON_BUFFERED,
                                         r.getStimulusTypeID()));
    }
  }

  synchronized
  void
  addReceptors (ReceptorSpecSet receptors)
    throws Excp_Receptor
  {
    if (receptors == null)
    {
      throw new Excp_Receptor("Parameter 'receptors' is required");
    }

    for (ReceptorSpec r : receptors)
    {
      this.receptors.put(r.getName(),
                         new Rx_Receptor(r.getName(),
                                         r.getMode(),
                                         r.getStimulusTypeID()));
    }
  }

  synchronized
  UUID
  getStimulusTypeID (String name)
    throws Excp_Receptor
  {
    return getReceptor(name).getStimulusTypeID();
  }

  synchronized
  boolean
  hasStimulus (String name)
    throws Excp_Receptor
  {
    return getReceptor(name).hasStimulus();
  }

  synchronized
  int
  getDepth (String name)
    throws Excp_Receptor
  {
    return getReceptor(name).depth();
  }

  synchronized
  <T extends Stimulus> T
  getStimulus (String name)
    throws Excp_Receptor
  {
    return getReceptor(name).getStimulus();
  }

  synchronized
  <T extends Stimulus> T
  peekStimulus (String name)
    throws Excp_Receptor
  {
    return getReceptor(name).peekStimulus();
  }

  synchronized
  void
  setStimulus (String name, Stimulus stimulus)
    throws Excp_Receptor
  {
    if (stimulus == null)
    {
      throw new Excp_Receptor("Cannot add null stimulus");
    }

    getReceptor(name).setStimulus(stimulus);
  }

  synchronized
  boolean
  isComplete ()
  {
    boolean rc = true;

    for (Rx_Receptor r : receptors.values())
    {
      if (!r.hasStimulus())
      {
        rc = false;
        break;
      }
    }

    return rc;
  }

  synchronized
  Rx_Transaction
  getSnapshot ()
  {
    Rx_Transaction rt = new Rx_Transaction(transactionID, receptors.values());
    
    if (isComplete())
    {
      try
      {
        for (String name : receptors.keySet())
        {
          Stimulus stimulus = getStimulus(name);
          rt.setStimulus(name, stimulus);
        }
      }
      catch (Excp_Receptor re)
      {
        // Cannot happen since this is an internal call
        // (receptors will always exist)
      }
    }
    
    return rt;
  }
  
  synchronized
  void
  clear ()
  {
    for (Rx_Receptor r : receptors.values())
    {
      r.clear();
    }
  }

  synchronized
  int
  depth ()
  {
    int maxDepth = 0;

    for (Rx_Receptor r : receptors.values())
    {
      if (r.depth() > maxDepth)
      {
        maxDepth = r.depth();
      }
    }
    
    return maxDepth;
  }

  @Override
  public
  Iterator<Rx_Receptor>
  iterator ()
  {
    return receptors.values().iterator();
  }
  
  private
  Rx_Receptor
  getReceptor (String name)
    throws Excp_Receptor
  {
    Rx_Receptor r = receptors.get(name);

    if (r == null)
    {
      throw new Excp_Receptor(name,
                              "Receptor not in collection");
    }
    
    return r;
  }
  
  void
  removeReceptor (String name)
  {
    receptors.remove(name);
  }

  UUID
  getTransactionID ()
  {
    return transactionID;
  }
  
  private UUID transactionID;
  private HashMap<String, Rx_Receptor> receptors;
}
