
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentCategoryPurpose1Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PaymentCategoryPurpose1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CORT"/>
 *     &lt;enumeration value="SALA"/>
 *     &lt;enumeration value="TREA"/>
 *     &lt;enumeration value="CASH"/>
 *     &lt;enumeration value="DIVI"/>
 *     &lt;enumeration value="GOVT"/>
 *     &lt;enumeration value="INTE"/>
 *     &lt;enumeration value="LOAN"/>
 *     &lt;enumeration value="PENS"/>
 *     &lt;enumeration value="SECU"/>
 *     &lt;enumeration value="SSBE"/>
 *     &lt;enumeration value="SUPP"/>
 *     &lt;enumeration value="TAXS"/>
 *     &lt;enumeration value="TRAD"/>
 *     &lt;enumeration value="VATX"/>
 *     &lt;enumeration value="HEDG"/>
 *     &lt;enumeration value="INTC"/>
 *     &lt;enumeration value="WHLD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PaymentCategoryPurpose1Code", namespace = "urn:swift:xsd:$pain.008.002.01")
@XmlEnum
public enum PaymentCategoryPurpose1Code {

    CORT,
    SALA,
    TREA,
    CASH,
    DIVI,
    GOVT,
    INTE,
    LOAN,
    PENS,
    SECU,
    SSBE,
    SUPP,
    TAXS,
    TRAD,
    VATX,
    HEDG,
    INTC,
    WHLD;

    public String value() {
        return name();
    }

    public static PaymentCategoryPurpose1Code fromValue(String v) {
        return valueOf(v);
    }

}
