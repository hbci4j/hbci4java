/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.hbci4java.hbci.GV_Result;

import org.hbci4java.hbci.manager.HBCIUtilsInternal;

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
