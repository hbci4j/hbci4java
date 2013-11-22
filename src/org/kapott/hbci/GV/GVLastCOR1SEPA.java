/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.kapott.hbci.GV;

import org.kapott.hbci.GV_Result.AbstractGVRLastSEPA;
import org.kapott.hbci.GV_Result.GVRLastCOR1SEPA;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des HBCI-Jobs fuer die SEPA-COR1-Lastschrift.
 */
public class GVLastCOR1SEPA extends AbstractGVLastSEPA
{
    /**
     * Liefert den Lowlevel-Jobnamen.
     * @return der Lowlevel-Jobname.
     */
    public static String getLowlevelName()
    {
        return "LastCOR1SEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVLastCOR1SEPA(HBCIHandler handler)
    {
        this(handler, getLowlevelName(), new GVRLastCOR1SEPA());
    }

    /**
     * ct.
     * @param handler
     * @param lowlevelName
     * @param result
     */
    public GVLastCOR1SEPA(HBCIHandler handler, String lowlevelName, AbstractGVRLastSEPA result)
    {
        super(handler, lowlevelName, result);

    	// Typ der Lastschrift. Moegliche Werte:
    	// CORE = Basis-Lastschrift (Default)
    	// COR1 = Basis-Lastschrift mit verkuerzter Vorlaufzeit
    	// B2B  = Business-2-Business-Lastschrift mit eingeschraenkter Rueckgabe-Moeglichkeit
        //
        // TODO: Wobei eigentlich nur "COR1" erlaubt ist, da dieser GV nur die COR1-Lastschrift
        // kapselt. Eigentlich sollte das gar nicht konfigurierbar sein
        addConstraint("type", "sepa.type", "COR1", LogFilter.FILTER_NONE);
    }
}
