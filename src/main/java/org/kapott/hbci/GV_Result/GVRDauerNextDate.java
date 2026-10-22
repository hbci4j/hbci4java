package org.kapott.hbci.GV_Result;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.manager.HBCIUtils;

/** Hilfs-Methoden für das Datum der nächsten Ausführung eines Dauerauftrags.
    Der Termin wird ausschließlich von der Bank geliefert und hier lediglich geparst
    und geprüft. Er wird nicht berechnet. Liefert die Bank keinen bzw. keinen
    brauchbaren Termin, bleibt das Datum der nächsten Ausführung unbekannt. */
public final class GVRDauerNextDate
{
    /**
     * Parst und prüft das von der Bank gelieferte Datum der nächsten Ausführung.
     * <p>
     * Als brauchbar gilt ein Termin, der
     * <ul>
     *   <li>kein Platzhalter ist (in SEPA-Nachrichten wird "1999-01-01" für "kein
     *       Datum gesetzt" verwendet),</li>
     *   <li>nicht vor dem Datum der ersten Ausführung liegt,</li>
     *   <li>nicht nach dem Datum der letzten Ausführung liegt und</li>
     *   <li>nicht in der Vergangenheit liegt.</li>
     * </ul>
     * </p>
     *
     * @param value der von der Bank gelieferte Termin im Format JJJJ-MM-TT
     *              (null, wenn die Bank keinen Termin geliefert hat)
     * @param firstdate Datum der ersten Ausführung oder null, wenn unbekannt
     * @param lastdate Datum der letzten Ausführung oder null, wenn unbekannt
     * @return das Datum der nächsten Ausführung oder null, wenn kein brauchbarer Termin vorliegt
     */
    public static Date parse(String value,Date firstdate,Date lastdate)
    {
        if (value==null || value.length()==0)
            return null;

        Date date;
        try
        {
            date=HBCIUtils.string2DateISO(value);
        }
        catch (Exception e)
        {
            HBCIUtils.log("Das von der Bank gelieferte Datum der nächsten Ausführung ('"+value+"') kann nicht gelesen werden.",HBCIUtils.LOG_WARN);
            return null;
        }

        if (!isPlausible(date,firstdate,lastdate))
        {
            HBCIUtils.log("Das von der Bank gelieferte Datum der nächsten Ausführung ("+HBCIUtils.date2StringISO(date)+") ist nicht plausibel und wird verworfen.",HBCIUtils.LOG_WARN);
            return null;
        }

        return date;
    }

    /**
     * Prüft, ob ein Wert eine gültige Uhrzeit im Format HH:mm:ss darstellt.
     *
     * @param value der zu prüfende Wert
     * @return true, wenn der Wert eine gültige Uhrzeit ist
     */
    public static boolean isValidTime(String value)
    {
        if (value==null || value.length()==0)
            return false;

        try
        {
            LocalTime.parse(value);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Prüft, ob ein Datum als Termin der nächsten Ausführung plausibel ist.
     *
     * @param date das zu prüfende Datum
     * @param firstdate Datum der ersten Ausführung oder null, wenn unbekannt
     * @param lastdate Datum der letzten Ausführung oder null, wenn unbekannt
     * @return true, wenn das Datum plausibel ist
     */
    private static boolean isPlausible(Date date,Date firstdate,Date lastdate)
    {
        if (date==null)
            return false;

        if (SepaUtil.DATE_UNDEFINED.equals(HBCIUtils.date2StringISO(date)))
            return false;

        if (firstdate!=null && date.before(firstdate))
            return false;

        if (lastdate!=null && date.after(lastdate))
            return false;

        return !date.before(today());
    }

    /**
     * @return das heutige Datum (Mitternacht) als Date-Objekt
     */
    private static Date today()
    {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
