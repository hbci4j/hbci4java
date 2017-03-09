
package org.kapott.hbci.sepa.jaxb.pain_001_001_03;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ChargeBearerTypeSEPACode.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="ChargeBearerTypeSEPACode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SLEV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChargeBearerTypeSEPACode", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03")
@XmlEnum
public enum ChargeBearerTypeSEPACode {

    SLEV;

    public String value() {
        return name();
    }

    public static ChargeBearerTypeSEPACode fromValue(String v) {
        return valueOf(v);
    }

}
