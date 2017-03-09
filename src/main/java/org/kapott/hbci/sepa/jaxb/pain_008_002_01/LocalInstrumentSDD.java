
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocalInstrumentSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocalInstrumentSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd">
 *           &lt;simpleType>
 *             &lt;restriction base="{urn:swift:xsd:$pain.008.002.01}LocalInstrumentCodeSDD">
 *               &lt;enumeration value="CORE"/>
 *               &lt;enumeration value="B2B"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalInstrumentSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "cd"
})
public class LocalInstrumentSDD {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected LocalInstrumentCodeSDD cd;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link LocalInstrumentCodeSDD }
     *     
     */
    public LocalInstrumentCodeSDD getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalInstrumentCodeSDD }
     *     
     */
    public void setCd(LocalInstrumentCodeSDD value) {
        this.cd = value;
    }

}
