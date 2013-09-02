
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocalInstrumentCodeSDD.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LocalInstrumentCodeSDD">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="B2B"/>
 *     &lt;enumeration value="CORE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LocalInstrumentCodeSDD", namespace = "urn:swift:xsd:$pain.008.002.01")
@XmlEnum
public enum LocalInstrumentCodeSDD {

    @XmlEnumValue("B2B")
    B_2_B("B2B"),
    CORE("CORE");
    private final String value;

    LocalInstrumentCodeSDD(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LocalInstrumentCodeSDD fromValue(String v) {
        for (LocalInstrumentCodeSDD c: LocalInstrumentCodeSDD.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
