package org.kapott.hbci.GV.parsers;

import java.util.Properties;




/**
 * Abstrakte Basis-Klasse der SEPA PAIN-Parser.
 * @param <T> der konkrete Typ.
 */
public abstract class AbstractSepaParser<T> implements ISEPAParser<T>
{
    /**
     * Speichert den Wert in den Properties.
     * @param props die Properties.
     * @param name das Property.
     * @param value der Wert.
     */
    void put(Properties props, Names name, String value)
    {
        // BUGZILLA 1610 - "java.util.Properties" ist von Hashtable abgeleitet und unterstuetzt keine NULL-Werte
        if (value == null)
            return;
        
        props.setProperty(name.getValue(),value);
    }
}
