
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ChargeBearerTypeSDDCode.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ChargeBearerTypeSDDCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SLEV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChargeBearerTypeSDDCode", namespace = "urn:swift:xsd:$pain.008.002.01")
@XmlEnum
public enum ChargeBearerTypeSDDCode {

    SLEV;

    public String value() {
        return name();
    }

    public static ChargeBearerTypeSDDCode fromValue(String v) {
        return valueOf(v);
    }

}
