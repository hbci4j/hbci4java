
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AmountTypeSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AmountTypeSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstdAmt" type="{urn:swift:xsd:$pain.001.002.02}CurrencyAndAmountSCT"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountTypeSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "instdAmt"
})
public class AmountTypeSCT {

    @XmlElement(name = "InstdAmt", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected CurrencyAndAmountSCT instdAmt;

    /**
     * Gets the value of the instdAmt property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmountSCT }
     *     
     */
    public CurrencyAndAmountSCT getInstdAmt() {
        return instdAmt;
    }

    /**
     * Sets the value of the instdAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmountSCT }
     *     
     */
    public void setInstdAmt(CurrencyAndAmountSCT value) {
        this.instdAmt = value;
    }

}
