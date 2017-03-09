
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für pain.008.001.01 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="pain.008.001.01">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:sepade:xsd:pain.008.001.01}GroupHeader20"/>
 *         &lt;element name="PmtInf" type="{urn:sepade:xsd:pain.008.001.01}PaymentInstructionInformation5"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pain.008.001.01", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "grpHdr",
    "pmtInf"
})
public class Pain00800101 {

    @XmlElement(name = "GrpHdr", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected GroupHeader20 grpHdr;
    @XmlElement(name = "PmtInf", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PaymentInstructionInformation5 pmtInf;

    /**
     * Ruft den Wert der grpHdr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeader20 }
     *     
     */
    public GroupHeader20 getGrpHdr() {
        return grpHdr;
    }

    /**
     * Legt den Wert der grpHdr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeader20 }
     *     
     */
    public void setGrpHdr(GroupHeader20 value) {
        this.grpHdr = value;
    }

    /**
     * Ruft den Wert der pmtInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentInstructionInformation5 }
     *     
     */
    public PaymentInstructionInformation5 getPmtInf() {
        return pmtInf;
    }

    /**
     * Legt den Wert der pmtInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentInstructionInformation5 }
     *     
     */
    public void setPmtInf(PaymentInstructionInformation5 value) {
        this.pmtInf = value;
    }

}
