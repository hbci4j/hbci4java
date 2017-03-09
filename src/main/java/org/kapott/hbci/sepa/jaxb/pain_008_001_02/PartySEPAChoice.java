
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PartySEPAChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PartySEPAChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="OrgId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}OrganisationIdentificationSEPAChoice"/>
 *           &lt;element name="PrvtId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}PersonIdentificationSEPA1Choice"/>
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
@XmlType(name = "PartySEPAChoice", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", propOrder = {
    "orgId",
    "prvtId"
})
public class PartySEPAChoice {

    @XmlElement(name = "OrgId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected OrganisationIdentificationSEPAChoice orgId;
    @XmlElement(name = "PrvtId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected PersonIdentificationSEPA1Choice prvtId;

    /**
     * Ruft den Wert der orgId-Eigenschaft ab.
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
     * Legt den Wert der orgId-Eigenschaft fest.
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
     * Ruft den Wert der prvtId-Eigenschaft ab.
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
     * Legt den Wert der prvtId-Eigenschaft fest.
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
