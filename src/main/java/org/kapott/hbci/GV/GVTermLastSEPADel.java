package org.kapott.hbci.GV;

import org.kapott.hbci.GV_Result.GVRLastSEPA;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;

/**
 * Geschaeftsvorfall zum Loeschen einer terminierten SEPA-Einzellastschrift (HKDSL).
 * Die Lastschrift wird ueber die Auftrags-ID identifiziert. Die Auftragsdaten werden,
 * wie bei GVTermUebSEPADel, immer als pain-Nachricht mitgeschickt.
 */
public class GVTermLastSEPADel extends AbstractGVLastSEPA
{
    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName()
    {
        return "TermLastSEPADel";
    }

    /**
     * ct.
     * @param handler
     */
    public GVTermLastSEPADel(HBCIHandler handler)
    {
        // Die Bank liefert keine Datensegmente zurueck, der Result-Container bleibt leer.
        super(handler, getLowlevelName(), new GVRLastSEPA());

        // Typ der Lastschrift: CORE (Default) oder COR1
        addConstraint("type",      "sepa.type",      "CORE", LogFilter.FILTER_NONE);
        addConstraint("batchbook", "sepa.batchbook", "0",    LogFilter.FILTER_NONE); // wie in GVLastSEPA
        addConstraint("orderid",   "orderid",        null,   LogFilter.FILTER_NONE);
    }

    /**
     * Laut Spezifikation muss dasselbe pain-Schema verwendet werden, welches die
     * Bank zuvor in der Bestandsabfrage geliefert hat. Wurde der Parameter
     * "_sepadescriptor" gesetzt (siehe GVRTermLastSEPAList.Entry#sepadescr), wird die
     * pain-Nachricht daher in dieser Version erzeugt.
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainVersion()
     */
    @Override
    protected SepaVersion getPainVersion()
    {
        String urn=getLowlevelParam(getName()+".sepadescr");
        return (urn!=null && urn.length()!=0) ? SepaVersion.byURN(urn) : super.getPainVersion();
    }
}
