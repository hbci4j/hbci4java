
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DirectDebitTransactionSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectDebitTransactionSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MndtRltdInf" type="{urn:swift:xsd:$pain.008.002.01}MandateRelatedInformationSDD"/>
 *         &lt;element name="CdtrSchmeId" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD4"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "mndtRltdInf",
    "cdtrSchmeId"
})
public class DirectDebitTransactionSDD {

    @XmlElement(name = "MndtRltdInf", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected MandateRelatedInformationSDD mndtRltdInf;
    @XmlElement(name = "CdtrSchmeId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PartyIdentificationSDD4 cdtrSchmeId;

    /**
     * Gets the value of the mndtRltdInf property.
     * 
     * @return
     *     possible object is
     *     {@link MandateRelatedInformationSDD }
     *     
     */
    public MandateRelatedInformationSDD getMndtRltdInf() {
        return mndtRltdInf;
    }

    /**
     * Sets the value of the mndtRltdInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link MandateRelatedInformationSDD }
     *     
     */
    public void setMndtRltdInf(MandateRelatedInformationSDD value) {
        this.mndtRltdInf = value;
    }

    /**
     * Gets the value of the cdtrSchmeId property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSDD4 }
     *     
     */
    public PartyIdentificationSDD4 getCdtrSchmeId() {
        return cdtrSchmeId;
    }

    /**
     * Sets the value of the cdtrSchmeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD4 }
     *     
     */
    public void setCdtrSchmeId(PartyIdentificationSDD4 value) {
        this.cdtrSchmeId = value;
    }

}
