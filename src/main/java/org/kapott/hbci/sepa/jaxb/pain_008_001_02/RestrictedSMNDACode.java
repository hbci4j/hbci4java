
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RestrictedSMNDACode.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="RestrictedSMNDACode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SMNDA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RestrictedSMNDACode", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
@XmlEnum
public enum RestrictedSMNDACode {

    SMNDA;

    public String value() {
        return name();
    }

    public static RestrictedSMNDACode fromValue(String v) {
        return valueOf(v);
    }

}
