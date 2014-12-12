package org.kapott.hbci.GV;

import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.GV_Result.AbstractGVRLastSEPA;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.PainVersion.Type;
import org.kapott.hbci.status.HBCIMsgStatus;

/** 
 * Abstrakte Basisklasse fuer die terminierten SEPA-Lastschriften.
 */
public abstract class AbstractGVLastSEPA extends AbstractSEPAGV
{
    private final static PainVersion DEFAULT = PainVersion.PAIN_008_001_01;
    
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
        return Type.PAIN_008;
    }

    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainJobName()
     */
    @Override
    public String getPainJobName()
    {
        return "LastSEPA";
    }

    /**
     * ct.
     * @param handler
     * @param lowlevelName der Lowlevel-GV-Name.
     * @param result der Result-Container.
     */
    public AbstractGVLastSEPA(HBCIHandler handler, String lowlevelName, AbstractGVRLastSEPA result)
    {
        super(handler, lowlevelName, result);

    	// My bzw. src ist das Konto des Ausführenden. Dst ist das Konto des
    	// Belasteten.
    	addConstraint("src.bic",         "My.bic", null, LogFilter.FILTER_MOST);
    	addConstraint("src.iban",        "My.iban", null, LogFilter.FILTER_IDS);
    	
        if (this.canNationalAcc(handler)) // nationale Bankverbindung mitschicken, wenn erlaubt
        {
            addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
            addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
        }

    	addConstraint("_sepadescriptor", "sepadescr", this.getPainVersion().getURN(), LogFilter.FILTER_NONE);
    	addConstraint("_sepapain",       "sepapain", null, LogFilter.FILTER_IDS);
    
    	addConstraint("src.bic",         "sepa.src.bic",  null,   LogFilter.FILTER_MOST);
    	addConstraint("src.iban",        "sepa.src.iban",  null,  LogFilter.FILTER_IDS);
    	addConstraint("src.name",        "sepa.src.name",  null,  LogFilter.FILTER_IDS);
    	addConstraint("dst.bic",         "sepa.dst.bic",   null,  LogFilter.FILTER_MOST, true);
    	addConstraint("dst.iban",        "sepa.dst.iban",  null,  LogFilter.FILTER_IDS,  true);
    	addConstraint("dst.name",        "sepa.dst.name",  null,  LogFilter.FILTER_IDS,  true);
    	addConstraint("btg.value",       "sepa.btg.value", null,  LogFilter.FILTER_NONE, true);
    	addConstraint("btg.curr",        "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE, true);
    	addConstraint("usage",           "sepa.usage",     "",    LogFilter.FILTER_NONE, true);
    
    	addConstraint("sepaid",          "sepa.sepaid",        getSEPAMessageId(),      LogFilter.FILTER_NONE);
        addConstraint("pmtinfid",        "sepa.pmtinfid",      getSEPAMessageId(),      LogFilter.FILTER_NONE);
    	addConstraint("endtoendid",      "sepa.endtoendid",    ENDTOEND_ID_NOTPROVIDED, LogFilter.FILTER_IDS,  true);
        addConstraint("creditorid",      "sepa.creditorid",    null,                    LogFilter.FILTER_IDS,  true);
    	addConstraint("mandateid",       "sepa.mandateid",     null,                    LogFilter.FILTER_IDS,  true);
        addConstraint("purposecode",     "sepa.purposecode",   "",                      LogFilter.FILTER_IDS,  true);
    	
    	// Datum als java.util.Date oder als ISO-Date-String im Format yyyy-MM-dd
    	addConstraint("manddateofsig",   "sepa.manddateofsig", null,                    LogFilter.FILTER_NONE, true);
    	addConstraint("amendmandindic",  "sepa.amendmandindic",Boolean.toString(false), LogFilter.FILTER_NONE, true);
    	
    	// Moegliche Werte:
    	//   FRST = Erst-Einzug
    	//   RCUR = Folge-Einzug
    	//   OOFF = Einmal-Einzug
    	//   FNAL = letztmaliger Einzug
    	//
    	// Ueblicherweise verwendet man bei einem Mandat bei der ersten Abbuchung "FRST"
    	// und bei allen Folgeabbuchungen des selben Mandats "RCUR".
    	addConstraint("sequencetype",    "sepa.sequencetype",  "FRST", LogFilter.FILTER_NONE);
    	
        // Ziel-Datum fuer den Einzug. Default: 1999-01-01 als Platzhalter fuer "zum naechstmoeglichen Zeitpunkt
        // Datum als java.util.Date oder als ISO-Date-String im Format yyyy-MM-dd
        addConstraint("targetdate",      "sepa.targetdate",    SepaUtil.DATE_UNDEFINED, LogFilter.FILTER_NONE);
        
        // Der folgende Constraint muss in der jeweiligen abgeleiteten Klasse passend gesetzt werden.
        // Typ der Lastschrift. Moegliche Werte:
        // CORE = Basis-Lastschrift (Default)
        // COR1 = Basis-Lastschrift mit verkuerzter Vorlaufzeit
        // B2B  = Business-2-Business-Lastschrift mit eingeschraenkter Rueckgabe-Moeglichkeit
        // addConstraint("type",            "sepa.type",          "CORE", LogFilter.FILTER_NONE);

    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        String orderid=result.getProperty(header+".orderid");
        ((AbstractGVRLastSEPA)(jobResult)).setOrderId(orderid);
        
        if (orderid!=null && orderid.length()!=0) {
            Properties p=getLowlevelParams();
            Properties p2=new Properties();
            
            for (Enumeration e=p.propertyNames();e.hasMoreElements();)
            {
                String key=(String)e.nextElement();
                p2.setProperty(key.substring(key.indexOf(".")+1),p.getProperty(key));
            }
            
            // TODO: Fuer den Fall, dass sich die Order-IDs zwischen CORE, COR1 und B2B
            // ueberschneiden koennen, muessen hier unterschiedliche Keys vergeben werden.
            getMainPassport().setPersistentData("termlast_"+orderid,p2);
        }
    }
}
