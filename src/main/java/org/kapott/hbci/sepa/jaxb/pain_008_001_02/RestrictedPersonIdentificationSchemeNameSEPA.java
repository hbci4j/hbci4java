
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RestrictedPersonIdentificationSchemeNameSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RestrictedPersonIdentificationSchemeNameSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Prtry" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}IdentificationSchemeNameSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestrictedPersonIdentificationSchemeNameSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", propOrder = {
    "prtry"
})
public class RestrictedPersonIdentificationSchemeNameSEPA {

    @XmlElement(name = "Prtry", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    @XmlSchemaType(name = "string")
    protected IdentificationSchemeNameSEPA prtry;

    /**
     * Ruft den Wert der prtry-Eigenschaft ab.
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
     * Legt den Wert der prtry-Eigenschaft fest.
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
