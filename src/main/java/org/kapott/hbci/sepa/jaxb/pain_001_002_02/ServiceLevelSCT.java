
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ServiceLevelSCT complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ServiceLevelSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:swift:xsd:$pain.001.002.02}ServiceLevelSCTCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceLevelSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "cd"
})
public class ServiceLevelSCT {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    @XmlSchemaType(name = "string")
    protected ServiceLevelSCTCode cd;

    /**
     * Ruft den Wert der cd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelSCTCode }
     *     
     */
    public ServiceLevelSCTCode getCd() {
        return cd;
    }

    /**
     * Legt den Wert der cd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelSCTCode }
     *     
     */
    public void setCd(ServiceLevelSCTCode value) {
        this.cd = value;
    }

}
