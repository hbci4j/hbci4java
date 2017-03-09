
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PartySDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PartySDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrvtId" type="{urn:swift:xsd:$pain.008.002.01}PersonIdentificationSDD2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartySDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "prvtId"
})
public class PartySDD {

    @XmlElement(name = "PrvtId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PersonIdentificationSDD2 prvtId;

    /**
     * Ruft den Wert der prvtId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PersonIdentificationSDD2 }
     *     
     */
    public PersonIdentificationSDD2 getPrvtId() {
        return prvtId;
    }

    /**
     * Legt den Wert der prvtId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonIdentificationSDD2 }
     *     
     */
    public void setPrvtId(PersonIdentificationSDD2 value) {
        this.prvtId = value;
    }

}
