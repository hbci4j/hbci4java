
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BranchAndFinancialInstitutionIdentificationSDD1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
     * Ruft den Wert der finInstnId-Eigenschaft ab.
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
     * Legt den Wert der finInstnId-Eigenschaft fest.
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
