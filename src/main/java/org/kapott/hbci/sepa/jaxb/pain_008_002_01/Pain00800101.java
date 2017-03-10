
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="GrpHdr" type="{urn:swift:xsd:$pain.008.002.01}GroupHeaderSDD"/>
 *         &lt;element name="PmtInf" type="{urn:swift:xsd:$pain.008.002.01}PaymentInstructionInformationSDD" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pain.008.001.01", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "grpHdr",
    "pmtInf"
})
public class Pain00800101 {

    @XmlElement(name = "GrpHdr", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected GroupHeaderSDD grpHdr;
    @XmlElement(name = "PmtInf", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected List<PaymentInstructionInformationSDD> pmtInf;

    /**
     * Gets the value of the grpHdr property.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeaderSDD }
     *     
     */
    public GroupHeaderSDD getGrpHdr() {
        return grpHdr;
    }

    /**
     * Sets the value of the grpHdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeaderSDD }
     *     
     */
    public void setGrpHdr(GroupHeaderSDD value) {
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
     * {@link PaymentInstructionInformationSDD }
     * 
     * 
     */
    public List<PaymentInstructionInformationSDD> getPmtInf() {
        if (pmtInf == null) {
            pmtInf = new ArrayList<PaymentInstructionInformationSDD>();
        }
        return this.pmtInf;
    }

}
