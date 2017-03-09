
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ServiceLevelSCTCode.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceLevelSCTCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SEPA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ServiceLevelSCTCode", namespace = "urn:swift:xsd:$pain.001.002.02")
@XmlEnum
public enum ServiceLevelSCTCode {

    SEPA;

    public String value() {
        return name();
    }

    public static ServiceLevelSCTCode fromValue(String v) {
        return valueOf(v);
    }

}
