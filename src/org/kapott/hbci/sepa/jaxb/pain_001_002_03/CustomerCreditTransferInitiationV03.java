
package org.kapott.hbci.sepa.jaxb.pain_001_002_03;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerCreditTransferInitiationV03 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerCreditTransferInitiationV03">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}GroupHeaderSCT"/>
 *         &lt;element name="PmtInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PaymentInstructionInformationSCT" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerCreditTransferInitiationV03", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", propOrder = {
    "grpHdr",
    "pmtInf"
})
public class CustomerCreditTransferInitiationV03 {

    @XmlElement(name = "GrpHdr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected GroupHeaderSCT grpHdr;
    @XmlElement(name = "PmtInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected List<PaymentInstructionInformationSCT> pmtInf;

    /**
     * Gets the value of the grpHdr property.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeaderSCT }
     *     
     */
    public GroupHeaderSCT getGrpHdr() {
        return grpHdr;
    }

    /**
     * Sets the value of the grpHdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeaderSCT }
     *     
     */
    public void setGrpHdr(GroupHeaderSCT value) {
        this.grpHdr = value;
    }

    /**
     * Gets the value of the pmtInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pmtInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPmtInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentInstructionInformationSCT }
     * 
     * 
     */
    public List<PaymentInstructionInformationSCT> getPmtInf() {
        if (pmtInf == null) {
            pmtInf = new ArrayList<PaymentInstructionInformationSCT>();
        }
        return this.pmtInf;
    }

}
