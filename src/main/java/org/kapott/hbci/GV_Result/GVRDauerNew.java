
/*  $Id: GVRDauerNew.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.GV_Result;

import org.kapott.hbci.manager.HBCIUtilsInternal;

/** Ergebnis einer Dauerauftragseinreichung. Falls die Bank die Vergabe von
    Auftrags-Identifikationsnummern unterstützt, wird die ID für den neuen
    Dauerauftrag hier bereitgestellt. */
public class GVRDauerNew
    extends HBCIJobResultImpl
{
    private String orderid;
    
    public void setOrderId(String orderid)
    {
        this.orderid=orderid;
    }

    /** Gibt die Auftragsnummer zurück, unter der der eingereichte Dauerauftrag
        bei der Bank registriert ist 
        @return die Auftragsidentifikationsnummer oder <code>null</code>, wenn die
                Bank das nicht unterstützt. */
    public String getOrderId()
    {
        return orderid;
    }
    
    public String toString()
    {
        return HBCIUtilsInternal.getLocMsg("ORDERID")+": "+getOrderId();
    }
}
