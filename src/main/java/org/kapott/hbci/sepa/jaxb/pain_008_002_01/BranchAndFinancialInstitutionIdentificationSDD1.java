
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchAndFinancialInstitutionIdentificationSDD1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentificationSDD1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:swift:xsd:$pain.008.002.01}FinancialInstitutionIdentificationSDD1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentificationSDD1", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentificationSDD1 {

    @XmlElement(name = "FinInstnId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected FinancialInstitutionIdentificationSDD1 finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentificationSDD1 }
     *     
     */
    public FinancialInstitutionIdentificationSDD1 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentificationSDD1 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentificationSDD1 value) {
        this.finInstnId = value;
    }

}
