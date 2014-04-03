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
 * Implementierung des HBCI-Jobs fuer die SEPA-B2B-Multi-Lastschrift.
 */
public class GVMultiLastB2BSEPA extends GVLastB2BSEPA
{
    /**
     * Liefert den Lowlevel-Jobnamen.
     * @return der Lowlevel-Jobname.
     */
    public static String getLowlevelName()
    {
        return "SammelLastB2BSEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVMultiLastB2BSEPA(HBCIHandler handler)
    {
        this(handler, getLowlevelName(), new GVRLastB2BSEPA());
    }

    /**
     * ct.
     * @param handler
     * @param lowlevelName
     * @param result
     */
    public GVMultiLastB2BSEPA(HBCIHandler handler, String lowlevelName, AbstractGVRLastSEPA result)
    {
        super(handler, lowlevelName, result);

        addConstraint("batchbook", "sepa.batchbook", "", LogFilter.FILTER_NONE);
        addConstraint("Total.value", "Total.value", null, LogFilter.FILTER_MOST);
        addConstraint("Total.curr", "Total.curr", null, LogFilter.FILTER_NONE);
    }

    @Override protected void createSEPAFromParams()
    {
        super.createSEPAFromParams();
        setParam("Total", SepaUtil.sumBtgValueObject(sepaParams));
    }
}
