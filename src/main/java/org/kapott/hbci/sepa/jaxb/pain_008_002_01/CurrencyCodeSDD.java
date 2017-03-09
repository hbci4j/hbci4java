
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CurrencyCodeSDD.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="CurrencyCodeSDD">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EUR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CurrencyCodeSDD", namespace = "urn:swift:xsd:$pain.008.002.01")
@XmlEnum
public enum CurrencyCodeSDD {

    EUR;

    public String value() {
        return name();
    }

    public static CurrencyCodeSDD fromValue(String v) {
        return valueOf(v);
    }

}
