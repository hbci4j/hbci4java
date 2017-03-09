
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentType3Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentType3Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RADM"/>
 *     &lt;enumeration value="RPIN"/>
 *     &lt;enumeration value="FXDR"/>
 *     &lt;enumeration value="DISP"/>
 *     &lt;enumeration value="PUOR"/>
 *     &lt;enumeration value="SCOR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentType3Code", namespace = "urn:sepade:xsd:pain.008.001.01")
@XmlEnum
public enum DocumentType3Code {

    RADM,
    RPIN,
    FXDR,
    DISP,
    PUOR,
    SCOR;

    public String value() {
        return name();
    }

    public static DocumentType3Code fromValue(String v) {
        return valueOf(v);
    }

}
