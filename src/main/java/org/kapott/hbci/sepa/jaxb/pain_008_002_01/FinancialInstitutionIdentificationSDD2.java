
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FinancialInstitutionIdentificationSDD2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitutionIdentificationSDD2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrtryId" type="{urn:swift:xsd:$pain.008.002.01}RestrictedIdentificationSDD"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentificationSDD2", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "prtryId"
})
public class FinancialInstitutionIdentificationSDD2 {

    @XmlElement(name = "PrtryId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected RestrictedIdentificationSDD prtryId;

    /**
     * Ruft den Wert der prtryId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedIdentificationSDD }
     *     
     */
    public RestrictedIdentificationSDD getPrtryId() {
        return prtryId;
    }

    /**
     * Legt den Wert der prtryId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedIdentificationSDD }
     *     
     */
    public void setPrtryId(RestrictedIdentificationSDD value) {
        this.prtryId = value;
    }

}
