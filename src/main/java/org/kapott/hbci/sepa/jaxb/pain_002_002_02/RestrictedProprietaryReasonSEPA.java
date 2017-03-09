
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RestrictedProprietaryReasonSEPA.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RestrictedProprietaryReasonSEPA">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RR01"/>
 *     &lt;enumeration value="SL01"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RestrictedProprietaryReasonSEPA", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum RestrictedProprietaryReasonSEPA {

    @XmlEnumValue("RR01")
    RR_01("RR01"),
    @XmlEnumValue("SL01")
    SL_01("SL01");
    private final String value;

    RestrictedProprietaryReasonSEPA(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RestrictedProprietaryReasonSEPA fromValue(String v) {
        for (RestrictedProprietaryReasonSEPA c: RestrictedProprietaryReasonSEPA.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
