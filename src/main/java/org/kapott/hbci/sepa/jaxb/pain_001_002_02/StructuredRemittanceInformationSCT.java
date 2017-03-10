
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StructuredRemittanceInformationSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StructuredRemittanceInformationSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrRefInf" type="{urn:swift:xsd:$pain.001.002.02}CreditorReferenceInformationSCT" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StructuredRemittanceInformationSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "cdtrRefInf"
})
public class StructuredRemittanceInformationSCT {

    @XmlElement(name = "CdtrRefInf", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected CreditorReferenceInformationSCT cdtrRefInf;

    /**
     * Gets the value of the cdtrRefInf property.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceInformationSCT }
     *     
     */
    public CreditorReferenceInformationSCT getCdtrRefInf() {
        return cdtrRefInf;
    }

    /**
     * Sets the value of the cdtrRefInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceInformationSCT }
     *     
     */
    public void setCdtrRefInf(CreditorReferenceInformationSCT value) {
        this.cdtrRefInf = value;
    }

}
