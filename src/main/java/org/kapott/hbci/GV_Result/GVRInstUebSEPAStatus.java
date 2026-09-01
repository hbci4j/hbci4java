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

package org.kapott.hbci.GV_Result;

/**
 * Rueckgabedaten fuer die Statusabfrage einer SEPA-Instant Ueberweisung.
 */
public class GVRInstUebSEPAStatus extends HBCIJobResultImpl
{
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String orderStatus;
    private String cancellationCode;

    /**
     * Liefert die Auftrags-ID der Echtzeitueberweisung.
     * @return die Auftrags-ID.
     */
    public String getOrderId()
    {
        return orderId;
    }

    /**
     * Speichert die Auftrags-ID der Echtzeitueberweisung.
     * @param orderId die Auftrags-ID.
     */
    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    /**
     * Liefert den Auftragsstatus.
     * @return der Auftragsstatus.
     */
    public String getOrderStatus()
    {
        return orderStatus;
    }

    /**
     * Speichert den Auftragsstatus.
     * @param orderStatus der Auftragsstatus.
     */
    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    /**
     * Liefert den Rueckgabecode zur Nichtausfuehrung.
     * @return der Rueckgabecode oder {@code null}.
     */
    public String getCancellationCode()
    {
        return cancellationCode;
    }

    /**
     * Speichert den Rueckgabecode zur Nichtausfuehrung.
     * @param cancellationCode der Rueckgabecode.
     */
    public void setCancellationCode(String cancellationCode)
    {
        this.cancellationCode = cancellationCode;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("GVRInstUebSEPAStatus{orderId=%s, orderStatus=%s, cancellationCode=%s}",
            orderId, orderStatus, cancellationCode);
    }
}
