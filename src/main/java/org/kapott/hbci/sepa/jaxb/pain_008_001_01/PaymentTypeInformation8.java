
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PaymentTypeInformation8 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformation8">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SvcLvl" type="{urn:sepade:xsd:pain.008.001.01}ServiceLevel4"/>
 *         &lt;element name="SeqTp" type="{urn:sepade:xsd:pain.008.001.01}SequenceType1Code"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformation8", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "svcLvl",
    "seqTp"
})
public class PaymentTypeInformation8 {

    @XmlElement(name = "SvcLvl", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected ServiceLevel4 svcLvl;
    @XmlElement(name = "SeqTp", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    @XmlSchemaType(name = "string")
    protected SequenceType1Code seqTp;

    /**
     * Ruft den Wert der svcLvl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevel4 }
     *     
     */
    public ServiceLevel4 getSvcLvl() {
        return svcLvl;
    }

    /**
     * Legt den Wert der svcLvl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevel4 }
     *     
     */
    public void setSvcLvl(ServiceLevel4 value) {
        this.svcLvl = value;
    }

    /**
     * Ruft den Wert der seqTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SequenceType1Code }
     *     
     */
    public SequenceType1Code getSeqTp() {
        return seqTp;
    }

    /**
     * Legt den Wert der seqTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SequenceType1Code }
     *     
     */
    public void setSeqTp(SequenceType1Code value) {
        this.seqTp = value;
    }

}
