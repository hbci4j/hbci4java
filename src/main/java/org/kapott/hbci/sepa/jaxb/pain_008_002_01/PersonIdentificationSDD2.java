
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonIdentificationSDD2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonIdentificationSDD2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OthrId" type="{urn:swift:xsd:$pain.008.002.01}GenericIdentificationSDD"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonIdentificationSDD2", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "othrId"
})
public class PersonIdentificationSDD2 {

    @XmlElement(name = "OthrId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected GenericIdentificationSDD othrId;

    /**
     * Gets the value of the othrId property.
     * 
     * @return
     *     possible object is
     *     {@link GenericIdentificationSDD }
     *     
     */
    public GenericIdentificationSDD getOthrId() {
        return othrId;
    }

    /**
     * Sets the value of the othrId property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericIdentificationSDD }
     *     
     */
    public void setOthrId(GenericIdentificationSDD value) {
        this.othrId = value;
    }

}
