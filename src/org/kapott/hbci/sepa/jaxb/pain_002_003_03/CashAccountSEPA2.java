
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CashAccountSEPA2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CashAccountSEPA2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}AccountIdentificationSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccountSEPA2", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "id"
})
public class CashAccountSEPA2 {

    @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", required = true)
    protected AccountIdentificationSEPA id;

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

}
