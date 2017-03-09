
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialInstitutionIdentificationSEPA2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitutionIdentificationSEPA2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrtryId" type="{urn:swift:xsd:$pain.002.002.02}RestrictedIdentificationSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentificationSEPA2", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "prtryId"
})
public class FinancialInstitutionIdentificationSEPA2 {

    @XmlElement(name = "PrtryId", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected RestrictedIdentificationSEPA prtryId;

    /**
     * Gets the value of the prtryId property.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedIdentificationSEPA }
     *     
     */
    public RestrictedIdentificationSEPA getPrtryId() {
        return prtryId;
    }

    /**
     * Sets the value of the prtryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedIdentificationSEPA }
     *     
     */
    public void setPrtryId(RestrictedIdentificationSEPA value) {
        this.prtryId = value;
    }

}
