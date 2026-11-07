// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

final class Mdl_Rx
{
  Mdl_Rx (String name, String type)
  {
    this.name = name;
    this.type = type;
  }

  public int
  hashCode ()
  {
    String concat = name + "/" + type;
    return concat.hashCode();
  }

  public String
  toString()
  {
    return (name + "/" + type);
  }

  public boolean
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
    
    final Mdl_Rx other = (Mdl_Rx) obj;

    if ((this.name == null) ?
        (other.name != null) :
        !this.name.equals(other.name))
    {
      return false;
    }
    
    if ((this.type == null) ?
        (other.type != null) :
        !this.type.equals(other.type))
    {
      return false;
    }

    return true;
  }

  String name;
  String type;
}
