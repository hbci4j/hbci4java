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

import java.util.Objects;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;

/**
 * Job-Implementierung fuer SEPA-Ueberweisungen.
 */
public class GVUebSEPA extends AbstractSEPAGV
{
    private final static SepaVersion DEFAULT = SepaVersion.PAIN_001_001_02;
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getDefaultPainVersion()
     */
    @Override
    protected SepaVersion getDefaultPainVersion()
    {
        return DEFAULT;
    }

    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainType()
     */
    @Override
    protected Type getPainType()
    {
        return Type.PAIN_001;
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
        this(handler, getLowlevelName());
    }

    /**
     * ct.
     * @param handler
     * @param name
     */
    public GVUebSEPA(HBCIHandler handler, String name)
    {
        this(handler, name, new HBCIJobResultImpl());
    }

    /**
     * ct.
     * @param handler
     * @param name
     * @param jobResult
     */
    public GVUebSEPA(HBCIHandler handler, String name, HBCIJobResultImpl jobResult)
    {
        super(handler, name, jobResult);

        addConstraint("src.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);
        
        if (this.canNationalAcc(handler)) // nationale Bankverbindung mitschicken, wenn erlaubt
        {
            addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
            addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
        }

        addConstraint("_sepadescriptor", "sepadescr", this.getPainVersion().getURN(), LogFilter.FILTER_NONE);
        addConstraint("_sepapain",       "sepapain", null, LogFilter.FILTER_IDS);

        /* dummy constraints to allow an application to set these values. the
         * overriden setLowlevelParam() stores these values in a special structure
         * which is later used to create the SEPA pain document. */
        addConstraint("src.bic",   "sepa.src.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("src.iban",  "sepa.src.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("src.name",  "sepa.src.name",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.bic",   "sepa.dst.bic",   "",   LogFilter.FILTER_MOST, true); // Kann eventuell entfallen, da BIC optional
        addConstraint("dst.iban",  "sepa.dst.iban",  null, LogFilter.FILTER_IDS, true);
        addConstraint("dst.name",  "sepa.dst.name",  null, LogFilter.FILTER_IDS, true);
        addConstraint("btg.value", "sepa.btg.value", null, LogFilter.FILTER_NONE, true);
        addConstraint("btg.curr",  "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE, true);
        addConstraint("usage",     "sepa.usage",     "",   LogFilter.FILTER_NONE, true);
      
        // Siehe https://homebanking-hilfe.de/forum/topic.php?p=155881#real155881
        if (Objects.equals(name,getLowlevelName())) // Nur bei Einzelauftraegen ausfuehren - GVUebSEPA wird in GVMultiUebSEPA ueberschrieben - und dort wird das Flag ja user-spezifisch gefuellt
          addConstraint("batchbook", "sepa.batchbook", "0", LogFilter.FILTER_NONE);
        
        //Constraints für die PmtInfId (eindeutige SEPA Message ID) und EndToEndId (eindeutige ID um Transaktion zu identifizieren)
        addConstraint("sepaid",    "sepa.sepaid",      getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("pmtinfid",  "sepa.pmtinfid",    getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("endtoendid", "sepa.endtoendid", ENDTOEND_ID_NOTPROVIDED, LogFilter.FILTER_NONE, true);
        addConstraint("purposecode","sepa.purposecode", "",                     LogFilter.FILTER_NONE, true);
    }
}
