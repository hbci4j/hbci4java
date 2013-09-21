
/*  $Id: GVUebSEPA.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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

package org.kapott.hbci.GV;

import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Job-Implementierung fuer SEPA-Ueberweisungen.
 */
public class GVUebSEPA extends AbstractSEPAGV
{
    private final static String SCHEMA_DEFAULT = "pain.001.001.02";
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getDefaultSchema()
     */
    @Override
    protected String getDefaultSchema() {
        return SCHEMA_DEFAULT;
    }
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getSchemaPattern()
     */
    @Override
    protected String getSchemaPattern() {
        return "pain\\.(001\\.\\d\\d\\d\\.\\d\\d)";
    }

    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName()
    {
        return "UebSEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVUebSEPA(HBCIHandler handler)
    {
        super(handler,getLowlevelName());

        addConstraint("src.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);

        /*
        addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
        addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
        addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber",  "", LogFilter.FILTER_MOST);
        */

        addConstraint("_sepadescriptor", "sepadescr", this.getSEPADescriptor(), LogFilter.FILTER_NONE);
        addConstraint("_sepapain",       "sepapain", null, LogFilter.FILTER_IDS);

        /* dummy constraints to allow an application to set these values. the
         * overriden setLowlevelParam() stores these values in a special structure
         * which is later used to create the SEPA pain document. */
        addConstraint("src.bic",   "sepa.src.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("src.iban",  "sepa.src.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("src.name",  "sepa.src.name",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.bic",   "sepa.dst.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("dst.iban",  "sepa.dst.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.name",  "sepa.dst.name",  null, LogFilter.FILTER_IDS);
        addConstraint("btg.value", "sepa.btg.value", null, LogFilter.FILTER_NONE);
        addConstraint("btg.curr",  "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE);
        addConstraint("usage",     "sepa.usage",     null, LogFilter.FILTER_NONE);
      
        //Constraints für die PmtInfId (eindeutige SEPA Message ID) und EndToEndId (eindeutige ID um Transaktion zu identifizieren)
        addConstraint("sepaid",    "sepa.sepaid",      getSEPAMessageId(), LogFilter.FILTER_NONE);
        
        // Pflicht bei neueren PAIN-Versionen - daher mit Default-Wert
        addConstraint("endtoendid", "sepa.endtoendid", createEndToEndId(), LogFilter.FILTER_NONE);
    }
}
