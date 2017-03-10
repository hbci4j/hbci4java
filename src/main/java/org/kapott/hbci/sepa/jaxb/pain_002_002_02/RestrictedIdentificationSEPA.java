
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RestrictedIdentificationSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RestrictedIdentificationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.002.002.02}RestrictedSMNDACode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestrictedIdentificationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "id"
})
public class RestrictedIdentificationSEPA {

    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected RestrictedSMNDACode id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedSMNDACode }
     *     
     */
    public RestrictedSMNDACode getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedSMNDACode }
     *     
     */
    public void setId(RestrictedSMNDACode value) {
        this.id = value;
    }

}
