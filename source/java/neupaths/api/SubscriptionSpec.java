// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * NeuPaths subscription specification.  A subscription specifies that stimuli
 * transmitted by a cell be placed on a receptor.  The cell and transmitter
 * names together define the producer of the stimuli.  The receptor name
 * defines the consumer.
 *
 * @see BridgeSubscriptionSpec
 * @see ExtractorSubscriptionSpec
 * @see LogicSubscriptionSpec
 * @see LogicLoopbackSubscriptionSpec
 * @see LogicMapSubscriptionSpec
 *
 * @author Aaron Caraveo
 */
public class SubscriptionSpec
{
  SubscriptionSpec
    (SubscriptionType  type,
     String            cellName,
     String            transmitterName,
     String            receptorName,
     String            domain,
     TransactionFilter filterTransactions)
  {
    if (cellName == null)
    {
      throw new NeuPathsException("Parameter 'cellName' is required");
    }

    if (transmitterName == null)
    {
      throw new NeuPathsException("Parameter 'transmitterName' is required");
    }
    
    if (receptorName == null)
    {
      throw new NeuPathsException("Parameter 'receptorName' is required");
    }
    
    if (domain == null)
    {
      throw new NeuPathsException("Parameter 'domain' is required");
    }
    
    if (filterTransactions == null)
    {
      throw new NeuPathsException("Parameter 'filterTransactions' is required");
    }
    
    this.type = type;
    this.cellName = cellName;
    this.transmitterName = transmitterName;
    this.receptorName = receptorName;
    this.domain = domain;
    this.filterTransactions = filterTransactions;
  }

  /**
   * Copies a subscription specification.
   * 
   * @param s The source specification.
   */
  SubscriptionSpec (SubscriptionSpec s)
  {
    type = s.type;
    cellName = s.cellName;
    transmitterName = s.transmitterName;
    receptorName = s.receptorName;
    domain = s.domain;
    filterTransactions = s.filterTransactions;
  }

  /**
   * Returns the subscription's type.
   *
   * @return The subscription's type.
   */
  public
  SubscriptionType
  getType ()
  {
    return type;
  }

  void
  setCellName (String cellName)
  {
    this.cellName = cellName;
  }
  
  boolean
  isLoopback ()
  {
    return type == SubscriptionType.LOOPBACK;
  }

  boolean
  filterTransactionResults ()
  {
    return (filterTransactions == TransactionFilter.ENABLED);
  }

  TransactionFilter
  getTransactionFilter ()
  {
    return filterTransactions;
  }

  /**
   * Returns the producer cell name.
   * 
   * @return The producer cell name.
   */
  public
  String
  getCellName ()
  {
    return cellName;
  }

  /**
   * Returns the producer transmitter name.
   * 
   * @return The producer transmitter name.
   */
  public
  String
  getTransmitterName ()
  {
    return transmitterName;
  }
  
  /**
   * Returns the consumer receptor name.
   * 
   * @return The consumer receptor name.
   */
  public
  String
  getReceptorName ()
  {
    return receptorName;
  }

  /**
   * Returns the domain.
   * 
   * @return The domain.
   */
  public
  String
  getDomain ()
  {
    return domain;
  }

  @Override
  public
  String
  toString ()
  {
    String image = "[" +
                   type + "/" +
                   cellName + "/" +
                   transmitterName + "/" +
                   receptorName + "/" +
                   domain + "/" +
                   filterTransactions +
                   "]";

    return image;
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

    final SubscriptionSpec other = (SubscriptionSpec) obj;

    if (this.type != other.type)
    {
      return false;
    }
    
    if ((this.cellName == null) ?
        (other.cellName != null) :
        !this.cellName.equals(other.cellName))
    {
      return false;
    }

    if ((this.transmitterName == null) ?
        (other.transmitterName != null) :
        !this.transmitterName.equals(other.transmitterName))
    {
      return false;
    }

    if ((this.receptorName == null) ?
        (other.receptorName != null) :
        !this.receptorName.equals(other.receptorName))
    {
      return false;
    }

    if ((this.domain == null) ?
        (other.domain != null) :
        !this.domain.equals(other.domain))
    {
      return false;
    }

    return true;
  }

  @Override
  public
  int
  hashCode()
  {
    String concat = type.toString() +
                    cellName +
                    transmitterName +
                    receptorName +
                    domain;
    return concat.hashCode();
  }

  private SubscriptionType type;
  private String cellName;
  private String transmitterName;
  private String receptorName;
  private String domain;
  private TransactionFilter filterTransactions;
}
