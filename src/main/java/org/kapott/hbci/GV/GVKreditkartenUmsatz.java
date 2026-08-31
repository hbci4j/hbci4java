/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2026 Franz Bettag
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

import org.kapott.hbci.GV_Result.GVRKreditkartenUmsatz;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Implementierung des von einigen Kreditinstituten angebotenen
 * Geschaeftsvorfalls zum Abruf von Kreditkartenumsaetzen (DKKKU/DIKKU).
 */
public class GVKreditkartenUmsatz extends HBCIJobImpl<GVRKreditkartenUmsatz>
{
    /**
     * @return der Lowlevelname.
     */
    public static String getLowlevelName()
    {
        return "KreditkartenUmsatz";
    }

    /**
     * ct.
     * @param handler der HBCI-Handler.
     */
    public GVKreditkartenUmsatz(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRKreditkartenUmsatz());

        addConstraint("my.country",      "KTV.KIK.country", "DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz",          "KTV.KIK.blz",     null, LogFilter.FILTER_MOST);
        addConstraint("my.number",       "KTV.number",      null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber",    "KTV.subnumber",   "",   LogFilter.FILTER_MOST);
        addConstraint("cardnumber",      "cardnumber",      null, LogFilter.FILTER_IDS);
        addConstraint("cardsubnumber",   "cardsubnumber",   "",   LogFilter.FILTER_MOST);
        addConstraint("startdate",       "startdate",       "",   LogFilter.FILTER_NONE);
        addConstraint("enddate",         "enddate",         "",   LogFilter.FILTER_NONE);
        addConstraint("maxentries",      "maxentries",      "",   LogFilter.FILTER_NONE);
        addConstraint("offset",          "offset",          "",   LogFilter.FILTER_MOST);
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    @Override
    protected void extractResults(HBCIMsgStatus msgstatus, String header, int idx)
    {
        Properties result = msgstatus.getData();
        GVRKreditkartenUmsatz target = jobResult;

        for (int i=0; ; i++)
        {
            String transactionHeader = HBCIUtilsInternal.withCounter(header + ".transactions",i);
            if (result.getProperty(transactionHeader + ".cardnumber") == null)
                break;

            try
            {
                target.addTransaction(result,transactionHeader);
            }
            catch (IllegalArgumentException e)
            {
                throw new HBCI_Exception("Kreditkartenumsatz konnte nicht gelesen werden",e);
            }
        }
    }
}
