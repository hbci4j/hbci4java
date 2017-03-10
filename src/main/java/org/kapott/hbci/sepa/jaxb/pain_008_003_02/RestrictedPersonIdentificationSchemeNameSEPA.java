
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RestrictedPersonIdentificationSchemeNameSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RestrictedPersonIdentificationSchemeNameSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Prtry" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}IdentificationSchemeNameSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestrictedPersonIdentificationSchemeNameSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", propOrder = {
    "prtry"
})
public class RestrictedPersonIdentificationSchemeNameSEPA {

    @XmlElement(name = "Prtry", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected IdentificationSchemeNameSEPA prtry;

    /**
     * Gets the value of the prtry property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificationSchemeNameSEPA }
     *     
     */
    public IdentificationSchemeNameSEPA getPrtry() {
        return prtry;
    }

    /**
     * Sets the value of the prtry property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificationSchemeNameSEPA }
     *     
     */
    public void setPrtry(IdentificationSchemeNameSEPA value) {
        this.prtry = value;
    }

}
