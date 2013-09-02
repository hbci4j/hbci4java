
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Grouping2Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Grouping2Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GRPD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Grouping2Code", namespace = "urn:sepade:xsd:pain.008.001.01")
@XmlEnum
public enum Grouping2Code {

    GRPD;

    public String value() {
        return name();
    }

    public static Grouping2Code fromValue(String v) {
        return valueOf(v);
    }

}
