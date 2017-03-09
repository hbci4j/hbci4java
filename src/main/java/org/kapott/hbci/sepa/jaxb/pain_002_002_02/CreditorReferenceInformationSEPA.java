
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CreditorReferenceInformationSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CreditorReferenceInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrRefTp" type="{urn:swift:xsd:$pain.002.002.02}CreditorReferenceTypeSEPA" minOccurs="0"/>
 *         &lt;element name="CdtrRef" type="{urn:swift:xsd:$pain.002.002.02}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditorReferenceInformationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "cdtrRefTp",
    "cdtrRef"
})
public class CreditorReferenceInformationSEPA {

    @XmlElement(name = "CdtrRefTp", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected CreditorReferenceTypeSEPA cdtrRefTp;
    @XmlElement(name = "CdtrRef", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String cdtrRef;

    /**
     * Ruft den Wert der cdtrRefTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceTypeSEPA }
     *     
     */
    public CreditorReferenceTypeSEPA getCdtrRefTp() {
        return cdtrRefTp;
    }

    /**
     * Legt den Wert der cdtrRefTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceTypeSEPA }
     *     
     */
    public void setCdtrRefTp(CreditorReferenceTypeSEPA value) {
        this.cdtrRefTp = value;
    }

    /**
     * Ruft den Wert der cdtrRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCdtrRef() {
        return cdtrRef;
    }

    /**
     * Legt den Wert der cdtrRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCdtrRef(String value) {
        this.cdtrRef = value;
    }

}
