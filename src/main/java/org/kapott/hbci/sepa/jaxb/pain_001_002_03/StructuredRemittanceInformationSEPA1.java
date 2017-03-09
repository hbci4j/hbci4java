
package org.kapott.hbci.sepa.jaxb.pain_001_002_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StructuredRemittanceInformationSEPA1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StructuredRemittanceInformationSEPA1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrRefInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}CreditorReferenceInformationSEPA1" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StructuredRemittanceInformationSEPA1", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", propOrder = {
    "cdtrRefInf"
})
public class StructuredRemittanceInformationSEPA1 {

    @XmlElement(name = "CdtrRefInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected CreditorReferenceInformationSEPA1 cdtrRefInf;

    /**
     * Gets the value of the cdtrRefInf property.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceInformationSEPA1 }
     *     
     */
    public CreditorReferenceInformationSEPA1 getCdtrRefInf() {
        return cdtrRefInf;
    }

    /**
     * Sets the value of the cdtrRefInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceInformationSEPA1 }
     *     
     */
    public void setCdtrRefInf(CreditorReferenceInformationSEPA1 value) {
        this.cdtrRefInf = value;
    }

}
