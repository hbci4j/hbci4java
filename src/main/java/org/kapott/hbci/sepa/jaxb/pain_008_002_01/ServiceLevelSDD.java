
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceLevelSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceLevelSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:swift:xsd:$pain.008.002.01}ServiceLevelSDDCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevelSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "cd"
})
public class ServiceLevelSDD {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected ServiceLevelSDDCode cd;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelSDDCode }
     *     
     */
    public ServiceLevelSDDCode getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelSDDCode }
     *     
     */
    public void setCd(ServiceLevelSDDCode value) {
        this.cd = value;
    }

}
