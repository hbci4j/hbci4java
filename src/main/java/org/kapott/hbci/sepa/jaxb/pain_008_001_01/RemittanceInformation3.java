
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RemittanceInformation3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemittanceInformation3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Ustrd" type="{urn:sepade:xsd:pain.008.001.01}Max140Text"/>
 *           &lt;element name="Strd" type="{urn:sepade:xsd:pain.008.001.01}StructuredRemittanceInformation6"/>
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
@XmlType(name = "RemittanceInformation3", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "ustrd",
    "strd"
})
public class RemittanceInformation3 {

    @XmlElement(name = "Ustrd", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String ustrd;
    @XmlElement(name = "Strd", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected StructuredRemittanceInformation6 strd;

    /**
     * Gets the value of the ustrd property.
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
     * Sets the value of the ustrd property.
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
     * Gets the value of the strd property.
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
     * Sets the value of the strd property.
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
