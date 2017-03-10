
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchAndFinancialInstitutionIdentificationSDD2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentificationSDD2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:swift:xsd:$pain.008.002.01}FinancialInstitutionIdentificationSDD2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentificationSDD2", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentificationSDD2 {

    @XmlElement(name = "FinInstnId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected FinancialInstitutionIdentificationSDD2 finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentificationSDD2 }
     *     
     */
    public FinancialInstitutionIdentificationSDD2 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentificationSDD2 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentificationSDD2 value) {
        this.finInstnId = value;
    }

}
