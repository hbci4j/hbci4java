
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentificationSDD4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentificationSDD4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.008.002.01}PartySDD"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentificationSDD4", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "id"
})
public class PartyIdentificationSDD4 {

    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PartySDD id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link PartySDD }
     *     
     */
    public PartySDD getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartySDD }
     *     
     */
    public void setId(PartySDD value) {
        this.id = value;
    }

}
