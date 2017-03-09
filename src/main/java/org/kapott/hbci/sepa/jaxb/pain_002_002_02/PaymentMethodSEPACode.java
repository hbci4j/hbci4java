
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentMethodSEPACode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PaymentMethodSEPACode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DD"/>
 *     &lt;enumeration value="TRF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PaymentMethodSEPACode", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum PaymentMethodSEPACode {

    DD,
    TRF;

    public String value() {
        return name();
    }

    public static PaymentMethodSEPACode fromValue(String v) {
        return valueOf(v);
    }

}
