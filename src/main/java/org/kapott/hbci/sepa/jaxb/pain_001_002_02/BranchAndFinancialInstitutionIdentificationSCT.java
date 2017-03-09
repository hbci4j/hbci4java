
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BranchAndFinancialInstitutionIdentificationSCT complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentificationSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:swift:xsd:$pain.001.002.02}FinancialInstitutionIdentificationSCT"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentificationSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentificationSCT {

    @XmlElement(name = "FinInstnId", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected FinancialInstitutionIdentificationSCT finInstnId;

    /**
     * Ruft den Wert der finInstnId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentificationSCT }
     *     
     */
    public FinancialInstitutionIdentificationSCT getFinInstnId() {
        return finInstnId;
    }

    /**
     * Legt den Wert der finInstnId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentificationSCT }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentificationSCT value) {
        this.finInstnId = value;
    }

}
