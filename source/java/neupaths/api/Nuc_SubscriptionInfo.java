// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.regex.PatternSyntaxException;

/**
 * Subscription information maintained by a cell nucleus.
 *
 * @author Aaron Caraveo
 */
final class Nuc_SubscriptionInfo
{
  Nuc_SubscriptionInfo
    (String cellName,
     String transmitterName,
     String domain)
  {
    this.cellName = cellName;
    this.transmitterName = transmitterName;
    this.domain = domain;
  }

  Nuc_SubscriptionInfo (Msg_Subscription msg)
  {
    cellName = msg.producerCellName;
    transmitterName = msg.producerTransmitterName;
    domain = msg.domain;
  }

  Nuc_SubscriptionInfo (Msg_Stimulus msg)
  {
    cellName = msg.producerCellName;
    transmitterName = msg.producerTransmitterName;
    domain = Syn.GLOBAL_DOMAIN;
  }

  Nuc_SubscriptionInfo (Stimulus stimulus)
  {
    cellName = stimulus.getProducerCellName();
    transmitterName = stimulus.getProducerTransmitterName();
    domain = Syn.GLOBAL_DOMAIN;
  }
  
  boolean
  matches (Nuc_SubscriptionInfo sub)
  {
    boolean rc = false;
    
    try
    {
      if (cellName.matches(sub.cellName) &&
          transmitterName.matches(sub.transmitterName))
      {
        rc = true;
      }
    }
    catch (PatternSyntaxException pse)
    {
      // consider pattern match syntax error as a non-match
    }
    
    return rc;
  }

  boolean
  matches (SubscriptionSpec subSpec)
  {
    boolean rc = false;
    
    try
    {
      if (cellName.matches(subSpec.getCellName()) &&
          transmitterName.matches(subSpec.getTransmitterName()))
      {
        rc = true;
      }
    }
    catch (PatternSyntaxException pse)
    {
      // consider pattern match syntax error as a non-match
    }
    
    return rc;
  }

  @Override
  public
  boolean
  equals (Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    final Nuc_SubscriptionInfo other = (Nuc_SubscriptionInfo) obj;

    if ( !(cellName.equals(other.cellName) &&
           transmitterName.equals(other.transmitterName)) )
    {
      return false;
    }

    return true;
  }

  @Override
  public
  int
  hashCode ()
  {
    String concat = cellName + transmitterName + domain;
    return concat.hashCode();
  }

  @Override
  public
  String
  toString ()
  {
    String image = "Cell: " + cellName +
                   ", Transmitter: " + transmitterName +
                   ", Domain: " + domain;
    return image;
  }
  
  public String cellName;
  public String transmitterName;
  public String domain;
}
