
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialInstitutionIdentificationSDD2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitutionIdentificationSDD2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrtryId" type="{urn:swift:xsd:$pain.008.002.01}RestrictedIdentificationSDD"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentificationSDD2", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "prtryId"
})
public class FinancialInstitutionIdentificationSDD2 {

    @XmlElement(name = "PrtryId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected RestrictedIdentificationSDD prtryId;

    /**
     * Gets the value of the prtryId property.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedIdentificationSDD }
     *     
     */
    public RestrictedIdentificationSDD getPrtryId() {
        return prtryId;
    }

    /**
     * Sets the value of the prtryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedIdentificationSDD }
     *     
     */
    public void setPrtryId(RestrictedIdentificationSDD value) {
        this.prtryId = value;
    }

}
