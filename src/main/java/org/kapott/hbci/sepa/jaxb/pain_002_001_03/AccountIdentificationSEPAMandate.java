
package org.kapott.hbci.sepa.jaxb.pain_002_001_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AccountIdentificationSEPAMandate complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AccountIdentificationSEPAMandate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="IBAN" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}IBAN2007Identifier"/>
 *           &lt;element name="Othr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}GenericAccountIdentificationSEPA"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountIdentificationSEPAMandate", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03", propOrder = {
    "iban",
    "othr"
})
public class AccountIdentificationSEPAMandate {

    @XmlElement(name = "IBAN", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected String iban;
    @XmlElement(name = "Othr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected GenericAccountIdentificationSEPA othr;

    /**
     * Ruft den Wert der iban-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Legt den Wert der iban-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

    /**
     * Ruft den Wert der othr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GenericAccountIdentificationSEPA }
     *     
     */
    public GenericAccountIdentificationSEPA getOthr() {
        return othr;
    }

    /**
     * Legt den Wert der othr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericAccountIdentificationSEPA }
     *     
     */
    public void setOthr(GenericAccountIdentificationSEPA value) {
        this.othr = value;
    }

}
