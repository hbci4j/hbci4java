
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchAndFinancialInstitutionIdentificationSEPA1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentificationSEPA1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:swift:xsd:$pain.002.002.02}FinancialInstitutionIdentificationSEPA1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentificationSEPA1", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentificationSEPA1 {

    @XmlElement(name = "FinInstnId", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected FinancialInstitutionIdentificationSEPA1 finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public FinancialInstitutionIdentificationSEPA1 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentificationSEPA1 value) {
        this.finInstnId = value;
    }

}
