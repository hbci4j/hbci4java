/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2026 HBCI4Java contributors
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

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des Geschaeftsvorfalls zum Abruf der Kontoauszugsuebersicht (HKKAU).
 */
public class GVKontoauszugUebersicht extends HBCIJobImpl<HBCIJobResultImpl>
{
    /**
     * Liefert den Lowlevel-Namen.
     * @return der Lowlevel-Name.
     */
    public static String getLowlevelName()
    {
        return "KontoauszugUebersicht";
    }

    /**
     * ct.
     * @param handler der Handler.
     */
    public GVKontoauszugUebersicht(HBCIHandler handler)
    {
        super(handler, getLowlevelName(), new HBCIJobResultImpl());

        addConstraint("my.bic", "My.bic", "", LogFilter.FILTER_MOST);
        addConstraint("my.iban", "My.iban", "", LogFilter.FILTER_IDS);

        if (this.canNationalAcc(handler))
        {
            addConstraint("my.country", "My.KIK.country", "DE", LogFilter.FILTER_NONE);
            addConstraint("my.blz", "My.KIK.blz", "", LogFilter.FILTER_MOST);
            addConstraint("my.number", "My.number", "", LogFilter.FILTER_IDS);
            addConstraint("my.subnumber", "My.subnumber", "", LogFilter.FILTER_MOST);
        }

        addConstraint("maxentries", "maxentries", "", LogFilter.FILTER_NONE);
        addConstraint("offset", "offset", "", LogFilter.FILTER_NONE);
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }
}
