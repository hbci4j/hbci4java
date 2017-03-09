
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialInstitution3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitution3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:sepade:xsd:pain.008.001.01}FinancialInstitutionIdentification5"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitution3", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "finInstnId"
})
public class FinancialInstitution3 {

    @XmlElement(name = "FinInstnId", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected FinancialInstitutionIdentification5 finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentification5 }
     *     
     */
    public FinancialInstitutionIdentification5 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentification5 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentification5 value) {
        this.finInstnId = value;
    }

}
