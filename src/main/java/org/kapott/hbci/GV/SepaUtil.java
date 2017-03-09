package org.kapott.hbci.GV;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.structures.Value;

/**
 * Ein paar statische Hilfs-Methoden fuer die Generierung der SEPA-Nachrichten.
 */
public class SepaUtil
{
    public final static String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public final static String DATE_FORMAT     = "yyyy-MM-dd";
    
    /**
     * Das Platzhalter-Datum, welches verwendet werden soll, wenn kein Datum angegeben ist.
     */
    public final static String DATE_UNDEFINED  = "1999-01-01";
    
    private final static Pattern INDEX_PATTERN = Pattern.compile("\\w+\\[(\\d+)\\](\\..*)?");

    /**
     * Erzeugt ein neues XMLCalender-Objekt.
     * @param isoDate optional. Das zu verwendende Datum.
     * Wird es weggelassen, dann wird das aktuelle Datum (mit Uhrzeit) verwendet.
     * @return das XML-Calendar-Objekt.
     * @throws Exception
     */
    public static XMLGregorianCalendar createCalendar(String isoDate) throws Exception
    {
        if (isoDate == null)
        {
            SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT);
            isoDate = format.format(new Date());
        }
        
        DatatypeFactory df = DatatypeFactory.newInstance();
        return df.newXMLGregorianCalendar(isoDate);
    }
    
    /**
     * Formatiert den XML-Kalender im angegebenen Format.
     * @param cal der Kalender.
     * @param format das zu verwendende Format. Fuer Beispiele siehe
     * {@link SepaUtil#DATE_FORMAT}
     * {@link SepaUtil#DATETIME_FORMAT}
     * Wenn keines angegeben ist, wird per Default {@link SepaUtil#DATE_FORMAT} verwendet.
     * @return die String das formatierte Datum.
     */
    public static String format(XMLGregorianCalendar cal, String format)
    {
        if (cal == null)
            return null;
        
        if (format == null)
            format = DATE_FORMAT;
        
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(cal.toGregorianCalendar().getTime());
    }
    
    /**
     * Formatiert die Dezimalzahl als String.
     * Zur Zeit macht die Funktion lediglich ein "toString",
     * @param value der zu formatierende Betrag.
     * @return der formatierte Betrag.
     */
    public static String format(BigDecimal value)
    {
        return value != null ? value.toString() : null;
    }

    /**
     * Ermittelt den maximalen Index aller indizierten Properties. Nicht indizierte Properties
     * werden ignoriert.
     * 
     * @param properties die Properties, mit denen gearbeitet werden soll
     * @return Maximaler Index, oder {@code null}, wenn keine indizierten Properties gefunden wurden
     */
    public static Integer maxIndex(Properties properties)
    {
        Integer max = null;
        for (String key : properties.stringPropertyNames())
        {
            Matcher m = INDEX_PATTERN.matcher(key);
            if (m.matches())
            {
                int index = Integer.parseInt(m.group(1));
                if (max == null || index > max)
                {
                    max = index;
                }
            }
        }
        return max;
    }

    /**
     * Liefert die Summe der Beträge aller Transaktionen. Bei einer Einzeltransaktion wird der
     * Betrag zurückgeliefert. Mehrfachtransaktionen müssen die gleiche Währung verwenden, da
     * eine Summenbildung sonst nicht möglich ist.
     * 
     * @param sepaParams die Properties, mit denen gearbeitet werden soll
     * @param max Maximaler Index, oder {@code null} für Einzeltransaktionen
     * @return Summe aller Beträge
     */
    public static BigDecimal sumBtgValue(Properties sepaParams, Integer max)
    {
        if (max == null)
            return new BigDecimal(sepaParams.getProperty("btg.value"));

        BigDecimal sum = BigDecimal.ZERO;
        String curr = null;

        for (int index = 0; index <= max; index++)
        {
            sum = sum.add(new BigDecimal(sepaParams.getProperty(insertIndex("btg.value", index))));

            // Sicherstellen, dass alle Transaktionen die gleiche Währung verwenden
            String indexCurr = sepaParams.getProperty(insertIndex("btg.curr", index));
            if (curr != null)
            {
                if (!curr.equals(indexCurr)) {
                    throw new InvalidArgumentException("mixed currencies on multiple transactions");
                }
            }
            else
            {
                curr = indexCurr;
            }
        }
        return sum;
    }

    /**
     * Fuegt einen Index in den Property-Key ein. Wurde kein Index angegeben, wird der Key
     * unveraendert zurueckgeliefert.
     * 
     * @param key Key, der mit einem Index ergaenzt werden soll
     * @param index Index oder {@code null}, wenn kein Index gesetzt werden soll
     * @return Key mit Index
     */
    public static String insertIndex(String key, Integer index)
    {
        if (index == null)
            return key;

        int pos = key.indexOf('.');
        if (pos >= 0)
        {
            return key.substring(0, pos) + '[' + index + ']' + key.substring(pos);
        }
        else
        {
            return key + '[' + index + ']';
        }
    }

    /**
     * Liefert ein Value-Objekt mit den Summen des Auftrages.
     * @param properties Auftrags-Properties.
     * @return das Value-Objekt mit der Summe.
     */
    public static Value sumBtgValueObject(Properties properties)
    {
        Integer maxIndex = maxIndex(properties);
        BigDecimal btg = sumBtgValue(properties, maxIndex);
        String curr = properties.getProperty(insertIndex("btg.curr", maxIndex == null ? null : 0));
        return new Value(btg, curr);
    }
    
    /**
     * Liefert den Wert des Properties oder den Default-Wert.
     * Der Default-Wert wird nicht nur bei NULL verwendet sondern auch bei Leerstring.
     * @param props die Properties.
     * @param name der Name des Properties.
     * @param defaultValue der Default-Wert.
     * @return der Wert.
     */
    public static String getProperty(Properties props, String name, String defaultValue)
    {
        String value = props.getProperty(name);
        return value != null && value.length() > 0 ? value : defaultValue;
    }
}


