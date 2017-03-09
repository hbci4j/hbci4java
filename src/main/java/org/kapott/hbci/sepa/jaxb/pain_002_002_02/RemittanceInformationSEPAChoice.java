
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RemittanceInformationSEPAChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RemittanceInformationSEPAChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Ustrd" type="{urn:swift:xsd:$pain.002.002.02}Max140Text"/>
 *           &lt;element name="Strd" type="{urn:swift:xsd:$pain.002.002.02}StructuredRemittanceInformationSEPA"/>
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
@XmlType(name = "RemittanceInformationSEPAChoice", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "ustrd",
    "strd"
})
public class RemittanceInformationSEPAChoice {

    @XmlElement(name = "Ustrd", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String ustrd;
    @XmlElement(name = "Strd", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected StructuredRemittanceInformationSEPA strd;

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
     *     {@link StructuredRemittanceInformationSEPA }
     *     
     */
    public StructuredRemittanceInformationSEPA getStrd() {
        return strd;
    }

    /**
     * Legt den Wert der strd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StructuredRemittanceInformationSEPA }
     *     
     */
    public void setStrd(StructuredRemittanceInformationSEPA value) {
        this.strd = value;
    }

}
