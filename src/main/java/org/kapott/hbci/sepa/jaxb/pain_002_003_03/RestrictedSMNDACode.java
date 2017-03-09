
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RestrictedSMNDACode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
@XmlType(name = "RestrictedSMNDACode", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
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
