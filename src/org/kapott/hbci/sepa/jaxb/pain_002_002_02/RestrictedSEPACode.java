
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RestrictedSEPACode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RestrictedSEPACode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SEPA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RestrictedSEPACode", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum RestrictedSEPACode {

    SEPA;

    public String value() {
        return name();
    }

    public static RestrictedSEPACode fromValue(String v) {
        return valueOf(v);
    }

}
