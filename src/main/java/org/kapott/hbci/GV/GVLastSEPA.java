/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.kapott.hbci.GV;

import java.util.Objects;

import org.kapott.hbci.GV_Result.AbstractGVRLastSEPA;
import org.kapott.hbci.GV_Result.GVRLastSEPA;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des HBCI-Jobs fuer die SEPA-Basis-Lastschrift.
 */
public class GVLastSEPA extends AbstractGVLastSEPA
{
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
        this(handler, getLowlevelName(), new GVRLastSEPA());
    }

    /**
     * ct.
     * @param handler
     * @param lowlevelName
     * @param result
     */
    public GVLastSEPA(HBCIHandler handler, String lowlevelName, AbstractGVRLastSEPA result)
    {
        super(handler, lowlevelName, result);

    	// Typ der Lastschrift. Moegliche Werte:
    	// CORE = Basis-Lastschrift (Default)
    	// COR1 = Basis-Lastschrift mit verkuerzter Vorlaufzeit
    	// B2B  = Business-2-Business-Lastschrift mit eingeschraenkter Rueckgabe-Moeglichkeit
        //
        // TODO: Wobei eigentlich nur "CORE" erlaubt ist, da dieser GV nur die CORE-Lastschrift
        // kapselt. Eigentlich sollte das gar nicht konfigurierbar sein
        addConstraint("type", "sepa.type", "CORE", LogFilter.FILTER_NONE);
        
        // Siehe https://homebanking-hilfe.de/forum/topic.php?p=155881#real155881
        if (Objects.equals(lowlevelName,getLowlevelName())) // Nur bei Einzelauftraegen ausfuehren - GVLastSEPA wird in GVMultiLastSEPA ueberschrieben - und dort wird das Flag ja user-spezifisch gefuellt
          addConstraint("batchbook", "sepa.batchbook", "0", LogFilter.FILTER_NONE);
    }
}
