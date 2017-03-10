
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditorReferenceInformationSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditorReferenceInformationSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrRefTp" type="{urn:swift:xsd:$pain.001.002.02}CreditorReferenceTypeSCT"/>
 *         &lt;element name="CdtrRef" type="{urn:swift:xsd:$pain.001.002.02}Max35Text"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditorReferenceInformationSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "cdtrRefTp",
    "cdtrRef"
})
public class CreditorReferenceInformationSCT {

    @XmlElement(name = "CdtrRefTp", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected CreditorReferenceTypeSCT cdtrRefTp;
    @XmlElement(name = "CdtrRef", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected String cdtrRef;

    /**
     * Gets the value of the cdtrRefTp property.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceTypeSCT }
     *     
     */
    public CreditorReferenceTypeSCT getCdtrRefTp() {
        return cdtrRefTp;
    }

    /**
     * Sets the value of the cdtrRefTp property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceTypeSCT }
     *     
     */
    public void setCdtrRefTp(CreditorReferenceTypeSCT value) {
        this.cdtrRefTp = value;
    }

    /**
     * Gets the value of the cdtrRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCdtrRef() {
        return cdtrRef;
    }

    /**
     * Sets the value of the cdtrRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCdtrRef(String value) {
        this.cdtrRef = value;
    }

}
