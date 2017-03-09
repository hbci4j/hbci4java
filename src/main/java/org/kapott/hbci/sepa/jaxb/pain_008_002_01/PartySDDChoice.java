
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PartySDDChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PartySDDChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="OrgId" type="{urn:swift:xsd:$pain.008.002.01}OrganisationIdentificationSDDChoice"/>
 *           &lt;element name="PrvtId" type="{urn:swift:xsd:$pain.008.002.01}PersonIdentificationSDD1"/>
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
@XmlType(name = "PartySDDChoice", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "orgId",
    "prvtId"
})
public class PartySDDChoice {

    @XmlElement(name = "OrgId", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected OrganisationIdentificationSDDChoice orgId;
    @XmlElement(name = "PrvtId", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PersonIdentificationSDD1 prvtId;

    /**
     * Ruft den Wert der orgId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationIdentificationSDDChoice }
     *     
     */
    public OrganisationIdentificationSDDChoice getOrgId() {
        return orgId;
    }

    /**
     * Legt den Wert der orgId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationIdentificationSDDChoice }
     *     
     */
    public void setOrgId(OrganisationIdentificationSDDChoice value) {
        this.orgId = value;
    }

    /**
     * Ruft den Wert der prvtId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PersonIdentificationSDD1 }
     *     
     */
    public PersonIdentificationSDD1 getPrvtId() {
        return prvtId;
    }

    /**
     * Legt den Wert der prvtId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonIdentificationSDD1 }
     *     
     */
    public void setPrvtId(PersonIdentificationSDD1 value) {
        this.prvtId = value;
    }

}
