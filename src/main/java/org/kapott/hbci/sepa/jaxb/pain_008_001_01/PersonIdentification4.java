
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonIdentification4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonIdentification4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OthrId" type="{urn:sepade:xsd:pain.008.001.01}RestrictedIdentification2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonIdentification4", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "othrId"
})
public class PersonIdentification4 {

    @XmlElement(name = "OthrId", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected RestrictedIdentification2 othrId;

    /**
     * Gets the value of the othrId property.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedIdentification2 }
     *     
     */
    public RestrictedIdentification2 getOthrId() {
        return othrId;
    }

    /**
     * Sets the value of the othrId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedIdentification2 }
     *     
     */
    public void setOthrId(RestrictedIdentification2 value) {
        this.othrId = value;
    }

}
