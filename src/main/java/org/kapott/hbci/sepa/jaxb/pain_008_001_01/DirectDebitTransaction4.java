
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DirectDebitTransaction4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectDebitTransaction4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MndtRltdInf" type="{urn:sepade:xsd:pain.008.001.01}MandateRelatedInformation4"/>
 *         &lt;element name="CdtrSchmeId" type="{urn:sepade:xsd:pain.008.001.01}PartyIdentification11"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransaction4", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "mndtRltdInf",
    "cdtrSchmeId"
})
public class DirectDebitTransaction4 {

    @XmlElement(name = "MndtRltdInf", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected MandateRelatedInformation4 mndtRltdInf;
    @XmlElement(name = "CdtrSchmeId", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PartyIdentification11 cdtrSchmeId;

    /**
     * Gets the value of the mndtRltdInf property.
     * 
     * @return
     *     possible object is
     *     {@link MandateRelatedInformation4 }
     *     
     */
    public MandateRelatedInformation4 getMndtRltdInf() {
        return mndtRltdInf;
    }

    /**
     * Sets the value of the mndtRltdInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link MandateRelatedInformation4 }
     *     
     */
    public void setMndtRltdInf(MandateRelatedInformation4 value) {
        this.mndtRltdInf = value;
    }

    /**
     * Gets the value of the cdtrSchmeId property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification11 }
     *     
     */
    public PartyIdentification11 getCdtrSchmeId() {
        return cdtrSchmeId;
    }

    /**
     * Sets the value of the cdtrSchmeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification11 }
     *     
     */
    public void setCdtrSchmeId(PartyIdentification11 value) {
        this.cdtrSchmeId = value;
    }

}
