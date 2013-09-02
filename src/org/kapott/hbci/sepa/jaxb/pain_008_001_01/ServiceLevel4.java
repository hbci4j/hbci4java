
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceLevel4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceLevel4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:sepade:xsd:pain.008.001.01}ServiceLevel3Code"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevel4", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "cd"
})
public class ServiceLevel4 {

    @XmlElement(name = "Cd", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected ServiceLevel3Code cd;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevel3Code }
     *     
     */
    public ServiceLevel3Code getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevel3Code }
     *     
     */
    public void setCd(ServiceLevel3Code value) {
        this.cd = value;
    }

}
