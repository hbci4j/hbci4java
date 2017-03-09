
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OthrIdentification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OthrIdentification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.003.03}OthrIdentificationCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OthrIdentification", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.003.03", propOrder = {
    "id"
})
public class OthrIdentification {

    @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.003.03", required = true)
    protected OthrIdentificationCode id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link OthrIdentificationCode }
     *     
     */
    public OthrIdentificationCode getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link OthrIdentificationCode }
     *     
     */
    public void setId(OthrIdentificationCode value) {
        this.id = value;
    }

}
