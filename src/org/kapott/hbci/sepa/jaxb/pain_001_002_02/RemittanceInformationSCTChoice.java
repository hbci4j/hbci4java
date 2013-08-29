
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RemittanceInformationSCTChoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemittanceInformationSCTChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Ustrd" type="{urn:swift:xsd:$pain.001.002.02}Max140Text"/>
 *           &lt;element name="Strd" type="{urn:swift:xsd:$pain.001.002.02}StructuredRemittanceInformationSCT"/>
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
@XmlType(name = "RemittanceInformationSCTChoice", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "ustrd",
    "strd"
})
public class RemittanceInformationSCTChoice {

    @XmlElement(name = "Ustrd", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected String ustrd;
    @XmlElement(name = "Strd", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected StructuredRemittanceInformationSCT strd;

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
     *     {@link StructuredRemittanceInformationSCT }
     *     
     */
    public StructuredRemittanceInformationSCT getStrd() {
        return strd;
    }

    /**
     * Sets the value of the strd property.
     * 
     * @param value
     *     allowed object is
     *     {@link StructuredRemittanceInformationSCT }
     *     
     */
    public void setStrd(StructuredRemittanceInformationSCT value) {
        this.strd = value;
    }

}
