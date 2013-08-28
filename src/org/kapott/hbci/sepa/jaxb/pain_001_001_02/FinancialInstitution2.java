
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialInstitution2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitution2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:sepade:xsd:pain.001.001.02}FinancialInstitutionIdentification4"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitution2", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "finInstnId"
})
public class FinancialInstitution2 {

    @XmlElement(name = "FinInstnId", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected FinancialInstitutionIdentification4 finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentification4 }
     *     
     */
    public FinancialInstitutionIdentification4 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentification4 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentification4 value) {
        this.finInstnId = value;
    }

}
