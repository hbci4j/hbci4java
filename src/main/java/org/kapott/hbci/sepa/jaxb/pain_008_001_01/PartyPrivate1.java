
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyPrivate1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyPrivate1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrvtId" type="{urn:sepade:xsd:pain.008.001.01}PersonIdentification4"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyPrivate1", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "prvtId"
})
public class PartyPrivate1 {

    @XmlElement(name = "PrvtId", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PersonIdentification4 prvtId;

    /**
     * Gets the value of the prvtId property.
     * 
     * @return
     *     possible object is
     *     {@link PersonIdentification4 }
     *     
     */
    public PersonIdentification4 getPrvtId() {
        return prvtId;
    }

    /**
     * Sets the value of the prvtId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonIdentification4 }
     *     
     */
    public void setPrvtId(PersonIdentification4 value) {
        this.prvtId = value;
    }

}
