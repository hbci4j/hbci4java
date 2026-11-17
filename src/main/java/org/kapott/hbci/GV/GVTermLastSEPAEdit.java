package org.kapott.hbci.GV;

import org.kapott.hbci.GV_Result.GVRTermLastSEPAEdit;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Geschaeftsvorfall zum Aendern einer terminierten SEPA-Einzellastschrift (HKDSA).
 * Es muessen alle Daten der Lastschrift so angegeben werden, wie sie nach der
 * Aenderung ausgefuehrt werden soll - nicht nur die geaenderten Felder.
 */
public class GVTermLastSEPAEdit extends AbstractGVLastSEPA
{
    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName()
    {
        return "TermLastSEPAEdit";
    }

    /**
     * ct.
     * @param handler
     */
    public GVTermLastSEPAEdit(HBCIHandler handler)
    {
        super(handler, getLowlevelName(), new GVRTermLastSEPAEdit());

        // Typ der Lastschrift: CORE (Default) oder COR1
        addConstraint("type",      "sepa.type",      "CORE", LogFilter.FILTER_NONE);
        addConstraint("batchbook", "sepa.batchbook", "0",    LogFilter.FILTER_NONE); // wie in GVLastSEPA
        addConstraint("orderid",   "orderid",        null,   LogFilter.FILTER_NONE);
    }

    /**
     * @see org.kapott.hbci.GV.AbstractGVLastSEPA#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    @Override
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        super.extractResults(msgstatus,header,idx);
        ((GVRTermLastSEPAEdit)(jobResult)).setOrderIdOld(msgstatus.getData().getProperty(header+".orderidold"));
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
