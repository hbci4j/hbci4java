
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchAndFinancialInstitutionIdentificationSEPA2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentificationSEPA2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}FinancialInstitutionIdentificationSEPA2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentificationSEPA2", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentificationSEPA2 {

    @XmlElement(name = "FinInstnId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected FinancialInstitutionIdentificationSEPA2 finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentificationSEPA2 }
     *     
     */
    public FinancialInstitutionIdentificationSEPA2 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentificationSEPA2 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentificationSEPA2 value) {
        this.finInstnId = value;
    }

}
