
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RemittanceInformation3 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RemittanceInformation3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Ustrd" type="{urn:sepade:xsd:pain.001.001.02}Max140Text"/>
 *           &lt;element name="Strd" type="{urn:sepade:xsd:pain.001.001.02}StructuredRemittanceInformation6"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemittanceInformation3", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "ustrd",
    "strd"
})
public class RemittanceInformation3 {

    @XmlElement(name = "Ustrd", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String ustrd;
    @XmlElement(name = "Strd", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected StructuredRemittanceInformation6 strd;

    /**
     * Ruft den Wert der ustrd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUstrd() {
        return ustrd;
    }

    /**
     * Legt den Wert der ustrd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUstrd(String value) {
        this.ustrd = value;
    }

    /**
     * Ruft den Wert der strd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StructuredRemittanceInformation6 }
     *     
     */
    public StructuredRemittanceInformation6 getStrd() {
        return strd;
    }

    /**
     * Legt den Wert der strd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StructuredRemittanceInformation6 }
     *     
     */
    public void setStrd(StructuredRemittanceInformation6 value) {
        this.strd = value;
    }

}
