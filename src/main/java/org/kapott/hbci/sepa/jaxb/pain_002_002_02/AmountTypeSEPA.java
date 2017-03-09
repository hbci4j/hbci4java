
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AmountTypeSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AmountTypeSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstdAmt" type="{urn:swift:xsd:$pain.002.002.02}CurrencyAndAmountSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountTypeSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "instdAmt"
})
public class AmountTypeSEPA {

    @XmlElement(name = "InstdAmt", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected CurrencyAndAmountSEPA instdAmt;

    /**
     * Ruft den Wert der instdAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmountSEPA }
     *     
     */
    public CurrencyAndAmountSEPA getInstdAmt() {
        return instdAmt;
    }

    /**
     * Legt den Wert der instdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmountSEPA }
     *     
     */
    public void setInstdAmt(CurrencyAndAmountSEPA value) {
        this.instdAmt = value;
    }

}
