
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartySEPA2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartySEPA2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrvtId" type="{urn:swift:xsd:$pain.002.002.02}PersonIdentificationSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartySEPA2", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "prvtId"
})
public class PartySEPA2 {

    @XmlElement(name = "PrvtId", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected PersonIdentificationSEPA prvtId;

    /**
     * Gets the value of the prvtId property.
     * 
     * @return
     *     possible object is
     *     {@link PersonIdentificationSEPA }
     *     
     */
    public PersonIdentificationSEPA getPrvtId() {
        return prvtId;
    }

    /**
     * Sets the value of the prvtId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonIdentificationSEPA }
     *     
     */
    public void setPrvtId(PersonIdentificationSEPA value) {
        this.prvtId = value;
    }

}
