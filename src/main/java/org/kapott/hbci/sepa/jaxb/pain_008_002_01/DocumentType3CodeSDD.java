
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentType3CodeSDD.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentType3CodeSDD">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SCOR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentType3CodeSDD", namespace = "urn:swift:xsd:$pain.008.002.01")
@XmlEnum
public enum DocumentType3CodeSDD {

    SCOR;

    public String value() {
        return name();
    }

    public static DocumentType3CodeSDD fromValue(String v) {
        return valueOf(v);
    }

}
