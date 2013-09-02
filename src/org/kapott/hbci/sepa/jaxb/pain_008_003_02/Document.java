
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CstmrDrctDbtInitn" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}CustomerDirectDebitInitiationV02"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cstmrDrctDbtInitn"
})
@XmlRootElement(name = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
public class Document {

    @XmlElement(name = "CstmrDrctDbtInitn", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected CustomerDirectDebitInitiationV02 cstmrDrctDbtInitn;

    /**
     * Gets the value of the cstmrDrctDbtInitn property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerDirectDebitInitiationV02 }
     *     
     */
    public CustomerDirectDebitInitiationV02 getCstmrDrctDbtInitn() {
        return cstmrDrctDbtInitn;
    }

    /**
     * Sets the value of the cstmrDrctDbtInitn property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerDirectDebitInitiationV02 }
     *     
     */
    public void setCstmrDrctDbtInitn(CustomerDirectDebitInitiationV02 value) {
        this.cstmrDrctDbtInitn = value;
    }

}
