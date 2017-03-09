
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PersonIdentificationSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PersonIdentificationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OthrId" type="{urn:swift:xsd:$pain.002.002.02}GenericIdentificationSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonIdentificationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "othrId"
})
public class PersonIdentificationSEPA {

    @XmlElement(name = "OthrId", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected GenericIdentificationSEPA othrId;

    /**
     * Ruft den Wert der othrId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GenericIdentificationSEPA }
     *     
     */
    public GenericIdentificationSEPA getOthrId() {
        return othrId;
    }

    /**
     * Legt den Wert der othrId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericIdentificationSEPA }
     *     
     */
    public void setOthrId(GenericIdentificationSEPA value) {
        this.othrId = value;
    }

}
