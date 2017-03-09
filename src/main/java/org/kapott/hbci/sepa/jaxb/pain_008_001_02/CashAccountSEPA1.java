
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CashAccountSEPA1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CashAccountSEPA1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}AccountIdentificationSEPA"/>
 *         &lt;element name="Ccy" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}ActiveOrHistoricCurrencyCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccountSEPA1", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", propOrder = {
    "id",
    "ccy"
})
public class CashAccountSEPA1 {

    @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected AccountIdentificationSEPA id;
    @XmlElement(name = "Ccy", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected String ccy;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link AccountIdentificationSEPA }
     *     
     */
    public AccountIdentificationSEPA getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountIdentificationSEPA }
     *     
     */
    public void setId(AccountIdentificationSEPA value) {
        this.id = value;
    }

    /**
     * Gets the value of the ccy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * Sets the value of the ccy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcy(String value) {
        this.ccy = value;
    }

}
