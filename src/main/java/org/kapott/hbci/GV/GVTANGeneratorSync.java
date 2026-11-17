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

import java.util.Properties;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des Geschaeftsvorfalls zur expliziten Synchronisierung
 * eines chipTAN-Generators (HKTSY).
 * Der Benutzer bringt den aktuellen ATC im TAN-Generator zur Anzeige und
 * uebermittelt ihn zusammen mit der zugehoerigen TAN. Die Bank sendet
 * keine Datensegmente zurueck, das Ergebnis steht in den Rueckmeldungen.
 * Der Job muss allein in einem eigenen execute() gesendet werden, da die Bank
 * nur dann auf die starke Kundenauthentifizierung verzichtet, wenn der Dialog
 * nichts anderes enthaelt (siehe FinTS PIN/TAN, Kapitel B.4.3.1.4).
 */
public class GVTANGeneratorSync extends HBCIJobImpl<HBCIJobResultImpl>
{
    /**
     * Liefert den Lowlevel-Namen.
     * @return der Lowlevel-Name.
     */
    public static String getLowlevelName()
    {
        return "TANGeneratorSync";
    }

    /**
     * ct.
     * @param handler der Handler.
     */
    public GVTANGeneratorSync(HBCIHandler handler)
    {
        super(handler, getLowlevelName(), new HBCIJobResultImpl());

        addConstraint("atc", "atc", null, LogFilter.FILTER_NONE);
        addConstraint("tan", "tan", null, LogFilter.FILTER_SECRETS);

        // Kartennummer und Kartenfolgenummer sind nur dann erlaubt (und dann Pflicht),
        // wenn die Bank sie in HITSYS mit "J" anfordert.
        Properties restrictions = getJobRestrictions();
        if ("J".equals(restrictions.getProperty("needcardnumber")))
            addConstraint("cardnumber", "cardnumber", null, LogFilter.FILTER_IDS);
        if ("J".equals(restrictions.getProperty("needcardseqnumber")))
            addConstraint("cardseqnumber", "cardseqnumber", null, LogFilter.FILTER_MOST);
    }
}
