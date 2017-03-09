
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Grouping1CodeSCT.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Grouping1CodeSCT">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MIXD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Grouping1CodeSCT", namespace = "urn:swift:xsd:$pain.001.002.02")
@XmlEnum
public enum Grouping1CodeSCT {

    MIXD;

    public String value() {
        return name();
    }

    public static Grouping1CodeSCT fromValue(String v) {
        return valueOf(v);
    }

}
