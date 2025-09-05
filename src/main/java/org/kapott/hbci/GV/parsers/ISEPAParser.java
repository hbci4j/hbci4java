package org.kapott.hbci.GV.parsers;

import java.io.InputStream;

/**
 * Basis-Interface der SEPA-XML Parser.
 * @param <T> Die konkrete Struktur, in die die Daten geparst werden.
 */
public interface ISEPAParser<T>
{
    /**
     * Enums fuer die verwendeten Schluessel-Namen in den Properties.
     */
    @SuppressWarnings("javadoc")
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
        BATCHBOOK("batchbook"),
        
        VOP_STATUS("vop.status"),
        
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
     * @param target das Zielobjekt, in das die Daten gelesen werden.
     */
    public void parse(InputStream xml, T target);
}
