
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für GenericIdentificationSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GenericIdentificationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.008.002.01}RestrictedIdentification3"/>
 *         &lt;element name="IdTp" type="{urn:swift:xsd:$pain.008.002.01}RestrictedSEPACode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericIdentificationSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "id",
    "idTp"
})
public class GenericIdentificationSDD {

    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected String id;
    @XmlElement(name = "IdTp", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    @XmlSchemaType(name = "string")
    protected RestrictedSEPACode idTp;

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der idTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedSEPACode }
     *     
     */
    public RestrictedSEPACode getIdTp() {
        return idTp;
    }

    /**
     * Legt den Wert der idTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedSEPACode }
     *     
     */
    public void setIdTp(RestrictedSEPACode value) {
        this.idTp = value;
    }

}
