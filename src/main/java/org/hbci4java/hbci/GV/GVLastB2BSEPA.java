/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.hbci4java.hbci.GV;

import java.util.Objects;

import org.hbci4java.hbci.GV_Result.AbstractGVRLastSEPA;
import org.hbci4java.hbci.GV_Result.GVRLastB2BSEPA;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.LogFilter;

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
        addConstraint("type", "sepa.type", "B2B", LogFilter.FILTER_NONE);
        
        // Siehe https://homebanking-hilfe.de/forum/topic.php?p=155881#real155881
        if (Objects.equals(lowlevelName,getLowlevelName())) // Nur bei Einzelauftraegen ausfuehren - GVLastB2BSEPA wird in GVMultiLastB2BSEPA ueberschrieben - und dort wird das Flag ja user-spezifisch gefuellt
          addConstraint("batchbook", "sepa.batchbook", "0", LogFilter.FILTER_NONE);
    }
}
