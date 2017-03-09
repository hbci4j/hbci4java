
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReferredDocumentInformation1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReferredDocumentInformation1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RfrdDocTp" type="{urn:sepade:xsd:pain.008.001.01}ReferredDocumentType1" minOccurs="0"/>
 *         &lt;element name="RfrdDocNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferredDocumentInformation1", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "rfrdDocTp",
    "rfrdDocNb"
})
public class ReferredDocumentInformation1 {

    @XmlElement(name = "RfrdDocTp", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected ReferredDocumentType1 rfrdDocTp;
    @XmlElement(name = "RfrdDocNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String rfrdDocNb;

    /**
     * Ruft den Wert der rfrdDocTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferredDocumentType1 }
     *     
     */
    public ReferredDocumentType1 getRfrdDocTp() {
        return rfrdDocTp;
    }

    /**
     * Legt den Wert der rfrdDocTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredDocumentType1 }
     *     
     */
    public void setRfrdDocTp(ReferredDocumentType1 value) {
        this.rfrdDocTp = value;
    }

    /**
     * Ruft den Wert der rfrdDocNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfrdDocNb() {
        return rfrdDocNb;
    }

    /**
     * Legt den Wert der rfrdDocNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfrdDocNb(String value) {
        this.rfrdDocNb = value;
    }

}
