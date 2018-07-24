/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.GV;


import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Properties;

import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Implementierung des Geschaeftsvorfalls zum Abruf von Umsaetzen mit Angabe des Zeitraums im CAMT-Format (HKCAZ).
 */
public class GVKUmsAllCamt extends AbstractSEPAGV
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
        super(handler, name, new GVRKUms());
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

        addConstraint("startdate","startdate",this.getStartdate(), LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        addConstraint( "offset", "offset", "", LogFilter.FILTER_NONE );
    }
    
    /**
     * Liefert das fruehest moegliche Startdatum fuer den Abruf der Umsaetze.
     * Im Gegensatz zur alten MT940-Version ist es jetzt bei CAMT offensichtlich
     * so, dass man (zumindest bei einigen Banken) nicht mehr pauschal das Start-Datum
     * weglassen kann und die Bank dann alles an Daten liefert. Zumindest bei der
     * Sparkasse kam dann die Fehlermeldung "9010:Abfrage uebersteigt gueltigen Zeitraum".
     * Also muessen wir - falls kein Startdatum angegeben ist (daher als Default-Wert)
     * selbst anhand der BPD herausfinden, was das Limit ist und dieses als Default-Wert
     * verwenden.
     * @return das fruehest moegliche Startdatum fuer den Abruf der Umsaetze.
     */
    private String getStartdate()
    {
        Properties bpd = this.getJobRestrictions();
        String days = bpd.getProperty("timerange");
        
        String date = "";
        if (days != null && days.length() > 0 && days.matches("[0-9]{1,4}"))
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-Integer.parseInt(days));
            date = HBCIUtils.date2StringISO(cal.getTime());
        }
        HBCIUtils.log("earliest start date according to BPD: " + (date != null && date.length() > 0 ? date : "<none>"),HBCIUtils.LOG_INFO);
        return date;
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties data = msgstatus.getData();
        GVRKUms result  = (GVRKUms) jobResult;
        final String format = data.getProperty(header+".format");

        for (int i=0;;i++)
        {
          final String booked = data.getProperty(header+".booked."+HBCIUtilsInternal.withCounter("message",i));
            if (booked == null)
                break;
            
            try
            {
                // Im Prinzip wuerde es reichen, die verwendete CAMT-Version einmalig anhand
                // des uebergebenen camt-Deskriptors in "format" zu ermitteln. Aber es gibt
                // tatsaechlich Banken, die in der HBCI-Nachricht eine andere Version angeben,
                // als sie tatsaechlich senden. Siehe https://www.willuhn.de/bugzilla/show_bug.cgi?id=1806
                // Das betraf PAIN-Messages. Ich weiss nicht, ob das bei CAMT auch vorkommt.
                // Ich gehe aber auf Nummer sicher.
                final SepaVersion version = SepaVersion.choose(format,booked);
                ISEPAParser<GVRKUms> parser = SEPAParserFactory.get(version);
                
                HBCIUtils.log("  parsing camt data: " + booked,HBCIUtils.LOG_DEBUG2);
                parser.parse(new ByteArrayInputStream(booked.getBytes(Comm.ENCODING)),result);
                HBCIUtils.log("  parsed camt data, entries: " + result.getFlatData().size(),HBCIUtils.LOG_INFO);
            }
            catch (Exception e)
            {
                HBCIUtils.log("  unable to parse camt data: " + e.getMessage(),HBCIUtils.LOG_ERR);
                throw new HBCI_Exception("Error parsing CAMT document",e);
            }
        }
        
        final String notbooked = data.getProperty(header+".notbooked");
        if (notbooked != null)
        {
            try
            {
                final SepaVersion version = SepaVersion.choose(format,notbooked);
                ISEPAParser<GVRKUms> parser = SEPAParserFactory.get(version);
                
                HBCIUtils.log("  parsing unbooked camt data: " + notbooked,HBCIUtils.LOG_DEBUG2);
                parser.parse(new ByteArrayInputStream(notbooked.getBytes(Comm.ENCODING)),result);
                HBCIUtils.log("  parsed unbooked camt data, entries: " + result.getFlatDataUnbooked().size(),HBCIUtils.LOG_INFO);
            }
            catch (Exception e)
            {
                HBCIUtils.log("  unable to parse unbooked camt data: " + e.getMessage(),HBCIUtils.LOG_ERR);
                throw new HBCI_Exception("Error parsing CAMT document",e);
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
