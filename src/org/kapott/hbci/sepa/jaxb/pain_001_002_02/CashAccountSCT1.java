
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CashAccountSCT1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CashAccountSCT1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.001.002.02}AccountIdentificationSCT"/>
 *         &lt;element name="Ccy" type="{urn:swift:xsd:$pain.001.002.02}CurrencyCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccountSCT1", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "id",
    "ccy"
})
public class CashAccountSCT1 {

    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected AccountIdentificationSCT id;
    @XmlElement(name = "Ccy", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected String ccy;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link AccountIdentificationSCT }
     *     
     */
    public AccountIdentificationSCT getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountIdentificationSCT }
     *     
     */
    public void setId(AccountIdentificationSCT value) {
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
