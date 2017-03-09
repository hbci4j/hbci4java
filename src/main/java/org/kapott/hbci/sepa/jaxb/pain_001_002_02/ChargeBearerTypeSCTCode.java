
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ChargeBearerTypeSCTCode.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ChargeBearerTypeSCTCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SLEV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChargeBearerTypeSCTCode", namespace = "urn:swift:xsd:$pain.001.002.02")
@XmlEnum
public enum ChargeBearerTypeSCTCode {

    SLEV;

    public String value() {
        return name();
    }

    public static ChargeBearerTypeSCTCode fromValue(String v) {
        return valueOf(v);
    }

}
