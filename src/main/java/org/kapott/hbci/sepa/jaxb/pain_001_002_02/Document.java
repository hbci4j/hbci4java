
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Document complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pain.001.001.02" type="{urn:swift:xsd:$pain.001.002.02}pain.001.001.02"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "pain00100102"
})
public class Document {

    @XmlElement(name = "pain.001.001.02", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected Pain00100102 pain00100102;

    /**
     * Ruft den Wert der pain00100102-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Pain00100102 }
     *     
     */
    public Pain00100102 getPain00100102() {
        return pain00100102;
    }

    /**
     * Legt den Wert der pain00100102-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Pain00100102 }
     *     
     */
    public void setPain00100102(Pain00100102 value) {
        this.pain00100102 = value;
    }

}
