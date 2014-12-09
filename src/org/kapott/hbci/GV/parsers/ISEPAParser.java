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
     * Enums fuer die verwendeten Schluessel-Namen in den Properties.
     */
    public static enum Names
    {
        SRC_NAME("src.name"),
        SRC_IBAN("src.iban"),
        SRC_BIC("src.bic"),
        
        DST_NAME("dst.name"),
        DST_IBAN("dst.iban"),
        DST_BIC("dst.bic"),
        
        VALUE("value"),
        CURR("curr"),

        USAGE("usage"),
        DATE("date"),
        ENDTOENDID("endtoendid"),
        PMTINFID("pmtinfid"),
        PURPOSECODE("purposecode"),
        
        LAST_TYPE("type"), // CORE,COR1,B2B
        CREDITORID("creditorid"),
        MANDATEID("mandateid"),
        MANDDATEOFSIG("manddateofsig"),
        SEQUENCETYPE("sequencetype"),
        TARGETDATE("targetdate"),
        
        ;
        
        private String value = null;
        
        /**
         * ct.
         * @param value der Schluessel-Name.
         */
        private Names(String value)
        {
            this.value = value;
        }
        
        /**
         * Liefert den Schluessel-Namen.
         * @return der Schluessel-Name.
         */
        public String getValue()
        {
            return this.value;
        }
    }

    /**
     * Parst SEPA-XML-Daten aus dem Stream und schreib die Ergebnisse in die Liste von Properties-Objekten. 
     * @param xml der Stream mit den XML-Daten.
     * @param target Die Liste mit Properties. Pro Geschaeftsvorfall wird ein Properties-Objekt eingefuegt.
     */
    public void parse(InputStream xml, List<Properties> target);
}
