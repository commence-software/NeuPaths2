// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

final class Mdl_Sub
{
  Mdl_Sub (String cell, String tx, String rx, String dmn, String act)
  {
    this.cell = cell;
    this.tx = tx;
    this.rx = rx;
    this.dmn = dmn;
    this.act = act;
  }
  
  public int
  hashCode ()
  {
    String concat = cell + "/" + tx + "/" + rx + "/" + dmn + "/" + act;
    return concat.hashCode();
  }

  public String
  toString ()
  {
    return (cell + "/" + tx + "/" + rx + "/" + dmn + "/" + act);
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
    
    final Mdl_Sub other = (Mdl_Sub) obj;

    if ((this.cell == null) ?
        (other.cell != null) :
        !this.cell.equals(other.cell))
    {
      return false;
    }
    
    if ((this.tx == null) ?
        (other.tx != null) :
        !this.tx.equals(other.tx))
    {
      return false;
    }

    if ((this.rx == null) ?
        (other.rx != null) :
        !this.rx.equals(other.rx))
    {
      return false;
    }

    if ((this.dmn == null) ?
        (other.dmn != null) :
        !this.dmn.equals(other.dmn))
    {
      return false;
    }

    if ((this.act == null) ?
        (other.act != null) :
        !this.act.equals(other.act))
    {
      return false;
    }

    return true;
  }

  String cell;
  String tx;
  String rx;
  String dmn;
  String act;
}
