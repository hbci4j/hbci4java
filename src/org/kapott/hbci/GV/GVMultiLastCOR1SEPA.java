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
 * Implementierung des HBCI-Jobs fuer die SEPA-COR1-Multi-Lastschrift.
 */
public class GVMultiLastCOR1SEPA extends GVLastCOR1SEPA
{
    /**
     * Liefert den Lowlevel-Jobnamen.
     * @return der Lowlevel-Jobname.
     */
    public static String getLowlevelName()
    {
        return "SammelLastCOR1SEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVMultiLastCOR1SEPA(HBCIHandler handler)
    {
        this(handler, getLowlevelName(), new GVRLastCOR1SEPA());
    }

    /**
     * ct.
     * @param handler
     * @param lowlevelName
     * @param result
     */
    public GVMultiLastCOR1SEPA(HBCIHandler handler, String lowlevelName, AbstractGVRLastSEPA result)
    {
        super(handler, lowlevelName, result);

        // batch-booking, sepa default rule is 'true' = 1
        addConstraint("batchbook", "sepa.batchbook", "1", LogFilter.FILTER_NONE);
    }
}
