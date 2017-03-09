
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pain.001.001.02 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pain.001.001.02">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:sepade:xsd:pain.001.001.02}GroupHeader20"/>
 *         &lt;element name="PmtInf" type="{urn:sepade:xsd:pain.001.001.02}PaymentInstructionInformation4"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pain.001.001.02", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "grpHdr",
    "pmtInf"
})
public class Pain00100102 {

    @XmlElement(name = "GrpHdr", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected GroupHeader20 grpHdr;
    @XmlElement(name = "PmtInf", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PaymentInstructionInformation4 pmtInf;

    /**
     * Gets the value of the grpHdr property.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeader20 }
     *     
     */
    public GroupHeader20 getGrpHdr() {
        return grpHdr;
    }

    /**
     * Sets the value of the grpHdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeader20 }
     *     
     */
    public void setGrpHdr(GroupHeader20 value) {
        this.grpHdr = value;
    }

    /**
     * Gets the value of the pmtInf property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentInstructionInformation4 }
     *     
     */
    public PaymentInstructionInformation4 getPmtInf() {
        return pmtInf;
    }

    /**
     * Sets the value of the pmtInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentInstructionInformation4 }
     *     
     */
    public void setPmtInf(PaymentInstructionInformation4 value) {
        this.pmtInf = value;
    }

}
