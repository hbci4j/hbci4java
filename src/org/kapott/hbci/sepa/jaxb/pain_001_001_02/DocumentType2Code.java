
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentType2Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentType2Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MSIN"/>
 *     &lt;enumeration value="CNFA"/>
 *     &lt;enumeration value="DNFA"/>
 *     &lt;enumeration value="CINV"/>
 *     &lt;enumeration value="CREN"/>
 *     &lt;enumeration value="DEBN"/>
 *     &lt;enumeration value="HIRI"/>
 *     &lt;enumeration value="SBIN"/>
 *     &lt;enumeration value="CMCN"/>
 *     &lt;enumeration value="SOAC"/>
 *     &lt;enumeration value="DISP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentType2Code", namespace = "urn:sepade:xsd:pain.001.001.02")
@XmlEnum
public enum DocumentType2Code {

    MSIN,
    CNFA,
    DNFA,
    CINV,
    CREN,
    DEBN,
    HIRI,
    SBIN,
    CMCN,
    SOAC,
    DISP;

    public String value() {
        return name();
    }

    public static DocumentType2Code fromValue(String v) {
        return valueOf(v);
    }

}
