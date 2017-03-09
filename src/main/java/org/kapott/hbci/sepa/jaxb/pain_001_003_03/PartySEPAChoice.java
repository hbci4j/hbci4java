
package org.kapott.hbci.sepa.jaxb.pain_001_003_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartySEPAChoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartySEPAChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="OrgId" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.003.03}OrganisationIdentificationSEPAChoice"/>
 *           &lt;element name="PrvtId" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.003.03}PersonIdentificationSEPA1Choice"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartySEPAChoice", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.003.03", propOrder = {
    "orgId",
    "prvtId"
})
public class PartySEPAChoice {

    @XmlElement(name = "OrgId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.003.03")
    protected OrganisationIdentificationSEPAChoice orgId;
    @XmlElement(name = "PrvtId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.003.03")
    protected PersonIdentificationSEPA1Choice prvtId;

    /**
     * Gets the value of the orgId property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationIdentificationSEPAChoice }
     *     
     */
    public OrganisationIdentificationSEPAChoice getOrgId() {
        return orgId;
    }

    /**
     * Sets the value of the orgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationIdentificationSEPAChoice }
     *     
     */
    public void setOrgId(OrganisationIdentificationSEPAChoice value) {
        this.orgId = value;
    }

    /**
     * Gets the value of the prvtId property.
     * 
     * @return
     *     possible object is
     *     {@link PersonIdentificationSEPA1Choice }
     *     
     */
    public PersonIdentificationSEPA1Choice getPrvtId() {
        return prvtId;
    }

    /**
     * Sets the value of the prvtId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonIdentificationSEPA1Choice }
     *     
     */
    public void setPrvtId(PersonIdentificationSEPA1Choice value) {
        this.prvtId = value;
    }

}
