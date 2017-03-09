
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialInstitutionIdentification5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitutionIdentification5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrtryId" type="{urn:sepade:xsd:pain.008.001.01}RestrictedIdentification1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentification5", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "prtryId"
})
public class FinancialInstitutionIdentification5 {

    @XmlElement(name = "PrtryId", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected RestrictedIdentification1 prtryId;

    /**
     * Gets the value of the prtryId property.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedIdentification1 }
     *     
     */
    public RestrictedIdentification1 getPrtryId() {
        return prtryId;
    }

    /**
     * Sets the value of the prtryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedIdentification1 }
     *     
     */
    public void setPrtryId(RestrictedIdentification1 value) {
        this.prtryId = value;
    }

}
