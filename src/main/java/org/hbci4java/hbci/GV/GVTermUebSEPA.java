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

package org.hbci4java.hbci.GV;

import java.util.Enumeration;
import java.util.Properties;

import org.hbci4java.hbci.GV_Result.GVRTermUeb;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.sepa.SepaVersion;
import org.hbci4java.hbci.sepa.SepaVersion.Type;
import org.hbci4java.hbci.status.HBCIMsgStatus;

/**
 * Job-Implementierung fuer SEPA-Ueberweisungen.
 */
public class GVTermUebSEPA extends AbstractSEPAGV
{
    private final static SepaVersion DEFAULT = SepaVersion.PAIN_001_001_02;
    
    /**
     * @see org.hbci4java.hbci.GV.AbstractSEPAGV#getDefaultPainVersion()
     */
    @Override
    protected SepaVersion getDefaultPainVersion()
    {
        return DEFAULT;
    }

    /**
     * @see org.hbci4java.hbci.GV.AbstractSEPAGV#getPainType()
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
        return "TermUebSEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVTermUebSEPA(HBCIHandler handler)
    {
        super(handler,getLowlevelName(), new GVRTermUeb());

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
        addConstraint("dst.bic",   "sepa.dst.bic",   "",   LogFilter.FILTER_MOST); // Kann eventuell entfallen, da BIC optional
        addConstraint("dst.iban",  "sepa.dst.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.name",  "sepa.dst.name",  null, LogFilter.FILTER_IDS);
        addConstraint("btg.value", "sepa.btg.value", null, LogFilter.FILTER_NONE);
        addConstraint("btg.curr",  "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE);
        addConstraint("usage",     "sepa.usage",     "",   LogFilter.FILTER_NONE);
        addConstraint("date",      "sepa.date",      null, LogFilter.FILTER_NONE);
     
        //Constraints für die PmtInfId (eindeutige SEPA Message ID) und EndToEndId (eindeutige ID um Transaktion zu identifizieren)
        addConstraint("sepaid",     "sepa.sepaid",      getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("pmtinfid",   "sepa.pmtinfid",    getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("endtoendid", "sepa.endtoendid", ENDTOEND_ID_NOTPROVIDED,  LogFilter.FILTER_NONE);
        addConstraint("purposecode","sepa.purposecode", "",                      LogFilter.FILTER_NONE);
    }
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        String orderid=result.getProperty(header+".orderid");
        ((GVRTermUeb)(jobResult)).setOrderId(orderid);
        
        if (orderid!=null && orderid.length()!=0) {
            Properties p=getLowlevelParams();
            Properties p2=new Properties();
            
            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                p2.setProperty(key.substring(key.indexOf(".")+1),
                               p.getProperty(key));
            }
            
            getMainPassport().setPersistentData("termueb_"+orderid,p2);
        }
    }
    
    public String getPainJobName() {
        return "UebSEPA";
    }

}
