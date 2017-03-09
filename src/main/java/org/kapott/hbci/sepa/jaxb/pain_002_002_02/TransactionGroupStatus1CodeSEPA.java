
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionGroupStatus1CodeSEPA.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionGroupStatus1CodeSEPA">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RJCT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionGroupStatus1CodeSEPA", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum TransactionGroupStatus1CodeSEPA {

    RJCT;

    public String value() {
        return name();
    }

    public static TransactionGroupStatus1CodeSEPA fromValue(String v) {
        return valueOf(v);
    }

}
