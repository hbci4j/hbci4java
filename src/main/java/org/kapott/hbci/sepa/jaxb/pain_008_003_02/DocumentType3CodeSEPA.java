
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DocumentType3CodeSEPA.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
@XmlType(name = "DocumentType3CodeSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
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
