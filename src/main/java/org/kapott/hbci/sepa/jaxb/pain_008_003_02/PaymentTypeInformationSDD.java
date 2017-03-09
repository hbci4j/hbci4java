
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PaymentTypeInformationSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SvcLvl" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}ServiceLevelSEPA"/>
 *         &lt;element name="LclInstrm" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}LocalInstrumentSEPA"/>
 *         &lt;element name="SeqTp" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}SequenceType1Code"/>
 *         &lt;element name="CtgyPurp" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}CategoryPurposeSEPA" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformationSDD", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", propOrder = {
    "svcLvl",
    "lclInstrm",
    "seqTp",
    "ctgyPurp"
})
public class PaymentTypeInformationSDD {

    @XmlElement(name = "SvcLvl", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected ServiceLevelSEPA svcLvl;
    @XmlElement(name = "LclInstrm", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected LocalInstrumentSEPA lclInstrm;
    @XmlElement(name = "SeqTp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    @XmlSchemaType(name = "string")
    protected SequenceType1Code seqTp;
    @XmlElement(name = "CtgyPurp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
    protected CategoryPurposeSEPA ctgyPurp;

    /**
     * Ruft den Wert der svcLvl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelSEPA }
     *     
     */
    public ServiceLevelSEPA getSvcLvl() {
        return svcLvl;
    }

    /**
     * Legt den Wert der svcLvl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelSEPA }
     *     
     */
    public void setSvcLvl(ServiceLevelSEPA value) {
        this.svcLvl = value;
    }

    /**
     * Ruft den Wert der lclInstrm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LocalInstrumentSEPA }
     *     
     */
    public LocalInstrumentSEPA getLclInstrm() {
        return lclInstrm;
    }

    /**
     * Legt den Wert der lclInstrm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalInstrumentSEPA }
     *     
     */
    public void setLclInstrm(LocalInstrumentSEPA value) {
        this.lclInstrm = value;
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

    /**
     * Ruft den Wert der ctgyPurp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CategoryPurposeSEPA }
     *     
     */
    public CategoryPurposeSEPA getCtgyPurp() {
        return ctgyPurp;
    }

    /**
     * Legt den Wert der ctgyPurp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryPurposeSEPA }
     *     
     */
    public void setCtgyPurp(CategoryPurposeSEPA value) {
        this.ctgyPurp = value;
    }

}
