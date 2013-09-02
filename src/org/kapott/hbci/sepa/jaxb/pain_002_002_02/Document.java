
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Document complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pain.002.001.02" type="{urn:swift:xsd:$pain.002.002.02}pain.002.001.02"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "pain00200102"
})
public class Document {

    @XmlElement(name = "pain.002.001.02", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected Pain00200102 pain00200102;

    /**
     * Gets the value of the pain00200102 property.
     * 
     * @return
     *     possible object is
     *     {@link Pain00200102 }
     *     
     */
    public Pain00200102 getPain00200102() {
        return pain00200102;
    }

    /**
     * Sets the value of the pain00200102 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pain00200102 }
     *     
     */
    public void setPain00200102(Pain00200102 value) {
        this.pain00200102 = value;
    }

}
