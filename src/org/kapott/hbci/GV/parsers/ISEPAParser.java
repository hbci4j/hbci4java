package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Basis-Interface der SEPA-XML Parser.
 */
public interface ISEPAParser
{
    /**
     * Parst SEPA-XML-Daten aus dem Stream und schreib die Ergebnisse in die Liste von Properties-Objekten. 
     * @param xml der Stream mit den XML-Daten.
     * @param target Die Liste mit Properties. Pro Geschaeftsvorfall wird ein Properties-Objekt eingefuegt.
     */
    public void parse(InputStream xml, List<Properties> target);
}
