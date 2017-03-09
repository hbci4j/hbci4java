
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DocumentType3CodeSDD.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
