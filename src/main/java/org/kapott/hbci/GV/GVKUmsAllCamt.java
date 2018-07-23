/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.GV;


import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.kapott.hbci.GV_Result.GVRKUmsCamt;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Implementierung des Geschaeftsvorfalls zum Abruf von Umsaetzen mit Angabe des Zeitraums im CAMT-Format (HKCAZ).
 */
public class GVKUmsAllCamt extends AbstractSEPAGV
{
    private final static AtomicInteger count = new AtomicInteger();
    
    /**
     * @return der Lowlevelname.
     */
    public static String getLowlevelName()
    {
        return "KUmsZeitCamt";
    }
    
    /**
     * ct.
     * @param handler
     * @param name
     */
    public GVKUmsAllCamt(HBCIHandler handler,String name)
    {
        super(handler, name, new GVRKUmsCamt());
    }
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getDefaultPainVersion()
     */
    @Override
    protected SepaVersion getDefaultPainVersion()
    {
        return SepaVersion.CAMT_052_001_01;
    }
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainType()
     */
    @Override
    protected Type getPainType()
    {
        return Type.CAMT_052;
    }
    
    /**
     * ct.
     * @param handler
     */
    public GVKUmsAllCamt(HBCIHandler handler)
    {
        this(handler,getLowlevelName());

        addConstraint("my.bic",  "KTV.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("my.iban", "KTV.iban", null, LogFilter.FILTER_IDS);

        if (this.canNationalAcc(handler))
        {
          addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
          addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
          addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
          addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        }
        
        // Das DE erlaubt zwar, dass wir alle CAMT-Versionen mitschicken,
        // die wir unterstuetzen. Einige Banken (u.a. die Sparkassen) kommen
        // damit aber nicht klar. Deswegen schicken wir immer genau eine Version
        // mit. Und zwar genau die hoechste, die die Bank in den GV-spezifischen BPD
        // mitgeteilt hat
        addConstraint("suppformat","formats.suppformat",this.getPainVersion().getURN(),LogFilter.FILTER_NONE);
        addConstraint("dummy","allaccounts","N", LogFilter.FILTER_NONE);

        addConstraint("startdate","startdate","", LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        addConstraint( "offset", "offset", "", LogFilter.FILTER_NONE );
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result     = msgstatus.getData();
        GVRKUmsCamt umsResult = (GVRKUmsCamt)jobResult;

        String booked    = result.getProperty(header+".booked.message");
        String notbooked = result.getProperty(header+".notbooked");

    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#verifyConstraints()
     */
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
