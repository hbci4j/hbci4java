
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RemittanceInformationSDDChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RemittanceInformationSDDChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Ustrd" type="{urn:swift:xsd:$pain.008.002.01}Max140Text"/>
 *           &lt;element name="Strd" type="{urn:swift:xsd:$pain.008.002.01}StructuredRemittanceInformationSDD"/>
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
@XmlType(name = "RemittanceInformationSDDChoice", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "ustrd",
    "strd"
})
public class RemittanceInformationSDDChoice {

    @XmlElement(name = "Ustrd", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected String ustrd;
    @XmlElement(name = "Strd", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected StructuredRemittanceInformationSDD strd;

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
     *     {@link StructuredRemittanceInformationSDD }
     *     
     */
    public StructuredRemittanceInformationSDD getStrd() {
        return strd;
    }

    /**
     * Legt den Wert der strd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StructuredRemittanceInformationSDD }
     *     
     */
    public void setStrd(StructuredRemittanceInformationSDD value) {
        this.strd = value;
    }

}
