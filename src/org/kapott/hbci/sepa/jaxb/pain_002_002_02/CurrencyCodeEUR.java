
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CurrencyCodeEUR.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CurrencyCodeEUR">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EUR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CurrencyCodeEUR", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum CurrencyCodeEUR {

    EUR;

    public String value() {
        return name();
    }

    public static CurrencyCodeEUR fromValue(String v) {
        return valueOf(v);
    }

}
