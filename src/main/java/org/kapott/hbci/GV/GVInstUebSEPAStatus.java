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

package org.kapott.hbci.GV;

import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRInstUebSEPAStatus;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Job-Implementierung fuer die Statusabfrage einer SEPA-Instant Ueberweisung.
 */
public class GVInstUebSEPAStatus extends HBCIJobImpl<GVRInstUebSEPAStatus>
{
    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Name des Jobs.
     */
    public static String getLowlevelName()
    {
        return "InstUebSEPAStatus";
    }

    /**
     * ct.
     * @param handler der HBCI-Handler.
     */
    public GVInstUebSEPAStatus(HBCIHandler handler)
    {
        super(handler, getLowlevelName(), new GVRInstUebSEPAStatus());

        addConstraint("src.bic", "My.bic", null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);

        if (canNationalAcc(handler))
        {
            addConstraint("src.country", "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz", "My.KIK.blz", "", LogFilter.FILTER_MOST);
            addConstraint("src.number", "My.number", "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber", "My.subnumber", "", LogFilter.FILTER_MOST);
        }

        addConstraint("format", "formats.format", null, LogFilter.FILTER_NONE, true);
        addConstraint("orderid", "orderid", null, LogFilter.FILTER_NONE);
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    @Override
    protected void extractResults(HBCIMsgStatus msgstatus, String header, int idx)
    {
        Properties data = msgstatus.getData();
        jobResult.setOrderId(data.getProperty(header + ".orderid"));
        jobResult.setOrderStatus(data.getProperty(header + ".orderstatus"));
        jobResult.setCancellationCode(data.getProperty(header + ".ccode"));
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#verifyConstraints()
     */
    @Override
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("src");
    }
}
