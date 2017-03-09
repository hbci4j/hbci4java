
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusReasonInformationSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusReasonInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StsOrgtr" type="{urn:swift:xsd:$pain.002.002.02}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="StsRsn" type="{urn:swift:xsd:$pain.002.002.02}StatusReason1Choice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusReasonInformationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "stsOrgtr",
    "stsRsn"
})
public class StatusReasonInformationSEPA {

    @XmlElement(name = "StsOrgtr", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected PartyIdentificationSEPA1 stsOrgtr;
    @XmlElement(name = "StsRsn", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected StatusReason1Choice stsRsn;

    /**
     * Gets the value of the stsOrgtr property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA1 }
     *     
     */
    public PartyIdentificationSEPA1 getStsOrgtr() {
        return stsOrgtr;
    }

    /**
     * Sets the value of the stsOrgtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA1 }
     *     
     */
    public void setStsOrgtr(PartyIdentificationSEPA1 value) {
        this.stsOrgtr = value;
    }

    /**
     * Gets the value of the stsRsn property.
     * 
     * @return
     *     possible object is
     *     {@link StatusReason1Choice }
     *     
     */
    public StatusReason1Choice getStsRsn() {
        return stsRsn;
    }

    /**
     * Sets the value of the stsRsn property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusReason1Choice }
     *     
     */
    public void setStsRsn(StatusReason1Choice value) {
        this.stsRsn = value;
    }

}
