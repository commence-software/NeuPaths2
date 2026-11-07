// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

final class Mdl_Flow
{
  Mdl_Flow (String src, String tx, String dst, String rx, String dmn, String act)
  {
    this.src = src;
    this.tx = tx;
    this.dst = dst;
    this.rx = rx;
    this.dmn = dmn;
    this.act = act;
  }

  public int
  hashCode ()
  {
    String concat = src + "/" + tx + "/" + dst + "/" + rx + "/" + dmn + "/" + act;
    return concat.hashCode();
  }

  public String
  toString ()
  {
    return (src + "/" + tx + "/" + dst + "/" + rx + "/" + dmn + "/" + act);
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
    
    final Mdl_Flow other = (Mdl_Flow) obj;

    if ((this.src == null) ?
        (other.src != null) :
        !this.src.equals(other.src))
    {
      return false;
    }
    
    if ((this.tx == null) ?
        (other.tx != null) :
        !this.tx.equals(other.tx))
    {
      return false;
    }

    if ((this.dst == null) ?
        (other.dst != null) :
        !this.dst.equals(other.dst))
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

  String src;
  String tx;
  String dst;
  String rx;
  String dmn;
  String act;
}
