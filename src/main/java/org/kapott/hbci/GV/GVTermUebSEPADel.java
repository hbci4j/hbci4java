package org.kapott.hbci.GV;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.PainVersion.Type;

/**
 * Implementierung des HBCI-Jobs fuer die Löschung einer SEPA-Terminüberweisung.
 */
public class GVTermUebSEPADel extends AbstractSEPAGV
{
    private final static PainVersion DEFAULT = PainVersion.PAIN_001_001_02;
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getDefaultPainVersion()
     */
    @Override
    protected PainVersion getDefaultPainVersion()
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
        return "TermUebSEPADel";
    }
    
    /**
     * ct.
     * @param handler
     */
    public GVTermUebSEPADel(HBCIHandler handler)
    {
        super(handler, getLowlevelName(), new HBCIJobResultImpl());
        
        addConstraint("src.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);
        
        if (this.canNationalAcc(handler)) // nationale Bankverbindung mitschicken, wenn erlaubt
        {
            addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
            addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
        }
        
        addConstraint("orderid",  "orderid", null, LogFilter.FILTER_NONE);
        
        addConstraint("_sepadescriptor", "sepadescr", this.getPainVersion().getURN(), LogFilter.FILTER_NONE);
        addConstraint("_sepapain",       "sepapain",  null, LogFilter.FILTER_IDS);
        
        /* dummy constraints to allow an application to set these values. the
         * overriden setLowlevelParam() stores these values in a special structure
         * which is later used to create the SEPA pain document. */
        addConstraint("src.bic",   "sepa.src.bic",   null,  LogFilter.FILTER_MOST);
        addConstraint("src.iban",  "sepa.src.iban",  null,  LogFilter.FILTER_IDS);
        addConstraint("src.name",  "sepa.src.name",  null,  LogFilter.FILTER_IDS);
        addConstraint("dst.bic",   "sepa.dst.bic",   null,  LogFilter.FILTER_MOST);
        addConstraint("dst.iban",  "sepa.dst.iban",  null,  LogFilter.FILTER_IDS);
        addConstraint("dst.name",  "sepa.dst.name",  null,  LogFilter.FILTER_IDS);
        addConstraint("btg.value", "sepa.btg.value", null,  LogFilter.FILTER_NONE);
        addConstraint("btg.curr",  "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE);
        addConstraint("usage",     "sepa.usage",     "",    LogFilter.FILTER_NONE);
        addConstraint("date",      "sepa.date",      null,  LogFilter.FILTER_NONE);
        
        // Constraints für die PmtInfId (eindeutige SEPA Message ID) und EndToEndId (eindeutige ID um Transaktion zu identifizieren)
        addConstraint("sepaid",    "sepa.sepaid",     getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("pmtinfid",  "sepa.pmtinfid",   getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("endtoendid","sepa.endtoendid", ENDTOEND_ID_NOTPROVIDED, LogFilter.FILTER_NONE);
        addConstraint("purposecode","sepa.purposecode", "",                    LogFilter.FILTER_NONE);
    }
    
    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainJobName()
     */
    public String getPainJobName()
    {
        return "UebSEPA";
    }
}
