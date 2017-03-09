
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für StructuredRemittanceInformationSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="StructuredRemittanceInformationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrRefInf" type="{urn:swift:xsd:$pain.008.002.01}CreditorReferenceInformationSDD" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StructuredRemittanceInformationSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "cdtrRefInf"
})
public class StructuredRemittanceInformationSDD {

    @XmlElement(name = "CdtrRefInf", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected CreditorReferenceInformationSDD cdtrRefInf;

    /**
     * Ruft den Wert der cdtrRefInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceInformationSDD }
     *     
     */
    public CreditorReferenceInformationSDD getCdtrRefInf() {
        return cdtrRefInf;
    }

    /**
     * Legt den Wert der cdtrRefInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceInformationSDD }
     *     
     */
    public void setCdtrRefInf(CreditorReferenceInformationSDD value) {
        this.cdtrRefInf = value;
    }

}
