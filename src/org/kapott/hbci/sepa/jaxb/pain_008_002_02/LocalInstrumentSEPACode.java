
package org.kapott.hbci.sepa.jaxb.pain_008_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocalInstrumentSEPACode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LocalInstrumentSEPACode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CORE"/>
 *     &lt;enumeration value="B2B"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LocalInstrumentSEPACode", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
@XmlEnum
public enum LocalInstrumentSEPACode {

    CORE("CORE"),
    @XmlEnumValue("B2B")
    B_2_B("B2B");
    private final String value;

    LocalInstrumentSEPACode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LocalInstrumentSEPACode fromValue(String v) {
        for (LocalInstrumentSEPACode c: LocalInstrumentSEPACode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
