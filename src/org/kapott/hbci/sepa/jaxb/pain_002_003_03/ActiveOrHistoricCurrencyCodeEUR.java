
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActiveOrHistoricCurrencyCodeEUR.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActiveOrHistoricCurrencyCodeEUR">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EUR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActiveOrHistoricCurrencyCodeEUR", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
@XmlEnum
public enum ActiveOrHistoricCurrencyCodeEUR {

    EUR;

    public String value() {
        return name();
    }

    public static ActiveOrHistoricCurrencyCodeEUR fromValue(String v) {
        return valueOf(v);
    }

}
