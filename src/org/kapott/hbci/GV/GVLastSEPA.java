/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.kapott.hbci.GV;

import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des HBCI-Jobs fuer die SEPA-Basis-Lastschrift.
 */
public class GVLastSEPA extends AbstractSEPAGV
{
    private final static String SCHEMA_DEFAULT = "pain.008.001.01";
    
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
        return "pain\\.(008\\.\\d\\d\\d\\.\\d\\d)";
    }

    /**
     * Liefert den Lowlevel-Jobnamen.
     * @return der Lowlevel-Jobname.
     */
    public static String getLowlevelName()
    {
        return "LastSEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVLastSEPA(HBCIHandler handler)
    {
        super(handler, getLowlevelName());

    	// My bzw. src ist das Konto des Ausführenden. Dst ist das Konto des
    	// Belasteten.
    	addConstraint("src.bic",         "My.bic", null, LogFilter.FILTER_MOST);
    	addConstraint("src.iban",        "My.iban", null, LogFilter.FILTER_IDS);
    	addConstraint("_sepadescriptor", "sepadescr", "sepade." + this.getSchema() + ".xsd", LogFilter.FILTER_NONE);
    	addConstraint("_sepapain",       "sepapain", null, LogFilter.FILTER_IDS);
    
    	addConstraint("src.bic",         "sepa.src.bic", null, LogFilter.FILTER_MOST);
    	addConstraint("src.iban",        "sepa.src.iban", null, LogFilter.FILTER_IDS);
    	addConstraint("src.name",        "sepa.src.name", null, LogFilter.FILTER_IDS);
    	addConstraint("dst.bic",         "sepa.dst.bic", null, LogFilter.FILTER_MOST);
    	addConstraint("dst.iban",        "sepa.dst.iban", null, LogFilter.FILTER_IDS);
    	addConstraint("dst.name",        "sepa.dst.name", null, LogFilter.FILTER_IDS);
    	addConstraint("btg.value",       "sepa.btg.value", null,LogFilter.FILTER_NONE);
    	addConstraint("btg.curr",        "sepa.btg.curr", "EUR", LogFilter.FILTER_NONE);
    	addConstraint("usage",           "sepa.usage", null, LogFilter.FILTER_NONE);
    
    	addConstraint("sepaid",          "sepa.sepaid", getSEPAMessageId(),LogFilter.FILTER_NONE);
    	addConstraint("endtoendid",      "sepa.endtoendid", null,LogFilter.FILTER_NONE);
    	addConstraint("mandateid",       "sepa.mandateid", null,LogFilter.FILTER_NONE);
    	addConstraint("manddateofsig",   "sepa.manddateofsig", null,LogFilter.FILTER_NONE);
    	addConstraint("amendmandindic",  "sepa.amendmandindic",Boolean.toString(false), LogFilter.FILTER_NONE);
    }
}
