
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

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
 *         &lt;element name="pain.008.001.01" type="{urn:swift:xsd:$pain.008.002.01}pain.008.001.01"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "pain00800101"
})
public class Document {

    @XmlElement(name = "pain.008.001.01", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected Pain00800101 pain00800101;

    /**
     * Gets the value of the pain00800101 property.
     * 
     * @return
     *     possible object is
     *     {@link Pain00800101 }
     *     
     */
    public Pain00800101 getPain00800101() {
        return pain00800101;
    }

    /**
     * Sets the value of the pain00800101 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pain00800101 }
     *     
     */
    public void setPain00800101(Pain00800101 value) {
        this.pain00800101 = value;
    }

}
