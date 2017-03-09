
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceLevelSDDCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceLevelSDDCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SEPA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ServiceLevelSDDCode", namespace = "urn:swift:xsd:$pain.008.002.01")
@XmlEnum
public enum ServiceLevelSDDCode {

    SEPA;

    public String value() {
        return name();
    }

    public static ServiceLevelSDDCode fromValue(String v) {
        return valueOf(v);
    }

}
