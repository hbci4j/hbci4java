
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentType3CodeSEPA.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentType3CodeSEPA">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SCOR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentType3CodeSEPA", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum DocumentType3CodeSEPA {

    SCOR;

    public String value() {
        return name();
    }

    public static DocumentType3CodeSEPA fromValue(String v) {
        return valueOf(v);
    }

}
