/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.kapott.hbci.GV;

import java.util.Objects;

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
        addConstraint("type", "sepa.type", "COR1", LogFilter.FILTER_NONE);
        
        // Siehe https://homebanking-hilfe.de/forum/topic.php?p=155881#real155881
        if (Objects.equals(lowlevelName,getLowlevelName())) // Nur bei Einzelauftraegen ausfuehren - GVLastCOR1SEPA wird in GVMultiLastCOR1SEPA ueberschrieben - und dort wird das Flag ja user-spezifisch gefuellt
          addConstraint("batchbook", "sepa.batchbook", "0", LogFilter.FILTER_NONE);
    }
}
