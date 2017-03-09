
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

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
 *           &lt;element name="OrgId" type="{urn:swift:xsd:$pain.002.002.02}OrganisationIdentificationSEPAChoice"/>
 *           &lt;element name="PrvtId" type="{urn:swift:xsd:$pain.002.002.02}PersonIdentification3"/>
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
@XmlType(name = "PartySEPAChoice", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "orgId",
    "prvtId"
})
public class PartySEPAChoice {

    @XmlElement(name = "OrgId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected OrganisationIdentificationSEPAChoice orgId;
    @XmlElement(name = "PrvtId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected PersonIdentification3 prvtId;

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
     *     {@link PersonIdentification3 }
     *     
     */
    public PersonIdentification3 getPrvtId() {
        return prvtId;
    }

    /**
     * Sets the value of the prvtId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonIdentification3 }
     *     
     */
    public void setPrvtId(PersonIdentification3 value) {
        this.prvtId = value;
    }

}
