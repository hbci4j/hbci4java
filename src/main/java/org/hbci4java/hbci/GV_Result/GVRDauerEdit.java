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

/** Ergebnis einer Dauerauftragsänderung. Ein geänderter Dauerauftrag kann
    u.U. eine andere Auftrags-Identifikationsnummer erhalten als der ursprüngliche
    Auftrag. Die neue und optional die alte Auftrags-ID können mit dieser Klasse
    ermittelt werden. */
public class GVRDauerEdit
    extends HBCIJobResultImpl
{
    private String orderid;
    private String orderidold;
    
    public void setOrderId(String orderid)
    {
        this.orderid=orderid;
    }

    public void setOrderIdOld(String orderidold)
    {
        this.orderidold=orderidold;
    }

    /** Gibt die Auftrags-Identifikationsnummer des geänderten Auftrages zurück.
        @return neue Auftrags-ID */
    public String getOrderId()
    {
        return orderid;
    }
    
    /** Gibt die Auftrags-Identifikationsnummer des ursprünglichen
        Dauerauftrages zurück.
        @return alte Auftrags-ID oder <code>null</code>, wenn diese nicht
                von der Bank bereitgestellt wird */
    public String getOrderIdOld()
    {
        return orderidold;
    }
    
    public String toString()
    {
        return HBCIUtilsInternal.getLocMsg("ORDERID")+": "+getOrderId();
    }
}
