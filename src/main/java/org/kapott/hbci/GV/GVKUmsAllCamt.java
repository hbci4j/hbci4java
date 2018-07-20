/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.GV;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRKUmsCamt;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Implementierung des Geschaeftsvorfalls zum Abruf von Umsaetzen mit Angabe des Zeitraums im CAMT-Format (HKCAZ).
 */
public class GVKUmsAllCamt extends HBCIJobImpl
{
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
        
        // Wir schicken alle Formate mit, weil wir alle unterstuetzen
        for (SepaVersion v:SepaVersion.getKnownVersions(Type.CAMT_052))
        {
            addConstraint("formats","formats.suppformat",v.getURN(),LogFilter.FILTER_NONE,true);
        }
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
        
        FileOutputStream fos = null;
        
        try
        {
            fos = new FileOutputStream("/home/willuhn/download/kumscamt.properties");
            result.store(fos,"");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
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
