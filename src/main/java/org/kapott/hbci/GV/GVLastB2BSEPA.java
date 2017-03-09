/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.kapott.hbci.GV;

import org.kapott.hbci.GV_Result.AbstractGVRLastSEPA;
import org.kapott.hbci.GV_Result.GVRLastB2BSEPA;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des HBCI-Jobs fuer die SEPA-B2B-Lastschrift.
 */
public class GVLastB2BSEPA extends AbstractGVLastSEPA
{
    /**
     * Liefert den Lowlevel-Jobnamen.
     * @return der Lowlevel-Jobname.
     */
    public static String getLowlevelName()
    {
        return "LastB2BSEPA";
    }
    
    /**
     * ct.
     * @param handler
     */
    public GVLastB2BSEPA(HBCIHandler handler)
    {
        this(handler, getLowlevelName(), new GVRLastB2BSEPA());
    }

    /**
     * ct.
     * @param handler
     * @param lowlevelName
     * @param result
     */
    public GVLastB2BSEPA(HBCIHandler handler, String lowlevelName, AbstractGVRLastSEPA result)
    {
        super(handler, lowlevelName, result);

    	// Typ der Lastschrift. Moegliche Werte:
    	// CORE = Basis-Lastschrift (Default)
    	// COR1 = Basis-Lastschrift mit verkuerzter Vorlaufzeit
    	// B2B  = Business-2-Business-Lastschrift mit eingeschraenkter Rueckgabe-Moeglichkeit
        //
        // TODO: Wobei eigentlich nur "B2B" erlaubt ist, da dieser GV nur die B2B-Lastschrift
        // kapselt. Eigentlich sollte das gar nicht konfigurierbar sein
        addConstraint("type", "sepa.type", "B2B", LogFilter.FILTER_NONE);
    }
}
