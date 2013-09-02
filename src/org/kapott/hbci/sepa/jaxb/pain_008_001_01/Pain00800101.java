
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pain.008.001.01 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pain.008.001.01">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:sepade:xsd:pain.008.001.01}GroupHeader20"/>
 *         &lt;element name="PmtInf" type="{urn:sepade:xsd:pain.008.001.01}PaymentInstructionInformation5"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pain.008.001.01", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "grpHdr",
    "pmtInf"
})
public class Pain00800101 {

    @XmlElement(name = "GrpHdr", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected GroupHeader20 grpHdr;
    @XmlElement(name = "PmtInf", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PaymentInstructionInformation5 pmtInf;

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
     *     {@link PaymentInstructionInformation5 }
     *     
     */
    public PaymentInstructionInformation5 getPmtInf() {
        return pmtInf;
    }

    /**
     * Sets the value of the pmtInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentInstructionInformation5 }
     *     
     */
    public void setPmtInf(PaymentInstructionInformation5 value) {
        this.pmtInf = value;
    }

}
