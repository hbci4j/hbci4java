
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceLevelSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceLevelSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:swift:xsd:$pain.001.002.02}ServiceLevelSCTCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevelSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "cd"
})
public class ServiceLevelSCT {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected ServiceLevelSCTCode cd;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelSCTCode }
     *     
     */
    public ServiceLevelSCTCode getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelSCTCode }
     *     
     */
    public void setCd(ServiceLevelSCTCode value) {
        this.cd = value;
    }

}
