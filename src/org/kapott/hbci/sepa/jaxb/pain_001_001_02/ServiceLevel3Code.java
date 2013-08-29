
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceLevel3Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceLevel3Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SEPA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ServiceLevel3Code", namespace = "urn:sepade:xsd:pain.001.001.02")
@XmlEnum
public enum ServiceLevel3Code {

    SEPA;

    public String value() {
        return name();
    }

    public static ServiceLevel3Code fromValue(String v) {
        return valueOf(v);
    }

}
