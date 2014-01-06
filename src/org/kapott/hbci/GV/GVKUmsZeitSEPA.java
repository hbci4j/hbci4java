package org.kapott.hbci.GV;

import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 *  Umsatzabfrage eines SEPA-Kontos 
 */
public class GVKUmsZeitSEPA extends GVKUmsAll
{
    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName()
    {
        return "KUmsZeitSEPA";
    }
    
    public GVKUmsZeitSEPA(HBCIHandler handler)
    {
        super(handler, getLowlevelName());
        
        addConstraint( "my.bic", "KTV.bic", null, LogFilter.FILTER_MOST );
        addConstraint( "my.iban", "KTV.iban", null, LogFilter.FILTER_IDS );
        addConstraint( "startdate", "sepa.startdate", "", LogFilter.FILTER_IDS );
        addConstraint( "enddate", "sepa.enddate", "", LogFilter.FILTER_IDS );
        addConstraint( "maxentries", "maxentries", "", LogFilter.FILTER_NONE );
        addConstraint( "offset", "offset", "", LogFilter.FILTER_NONE );
        addConstraint( "all", "allaccounts", "N", LogFilter.FILTER_NONE );
    }
}
