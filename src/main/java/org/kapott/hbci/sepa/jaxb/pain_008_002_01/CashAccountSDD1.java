
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CashAccountSDD1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CashAccountSDD1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.008.002.01}AccountIdentificationSDD"/>
 *         &lt;element name="Ccy" type="{urn:swift:xsd:$pain.008.002.01}CurrencyCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccountSDD1", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "id",
    "ccy"
})
public class CashAccountSDD1 {

    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected AccountIdentificationSDD id;
    @XmlElement(name = "Ccy", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected String ccy;

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AccountIdentificationSDD }
     *     
     */
    public AccountIdentificationSDD getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountIdentificationSDD }
     *     
     */
    public void setId(AccountIdentificationSDD value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der ccy-Eigenschaft ab.
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
     * Legt den Wert der ccy-Eigenschaft fest.
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
