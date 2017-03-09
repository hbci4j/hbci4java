
package org.kapott.hbci.sepa.jaxb.pain_002_001_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PaymentTypeInformationSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstrPrty" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}Priority2Code" minOccurs="0"/>
 *         &lt;element name="SvcLvl" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}ServiceLevel" minOccurs="0"/>
 *         &lt;element name="LclInstrm" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}LocalInstrumentSEPA" minOccurs="0"/>
 *         &lt;element name="SeqTp" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}SequenceType1Code" minOccurs="0"/>
 *         &lt;element name="CtgyPurp" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}CategoryPurposeSEPA" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformationSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03", propOrder = {
    "instrPrty",
    "svcLvl",
    "lclInstrm",
    "seqTp",
    "ctgyPurp"
})
public class PaymentTypeInformationSEPA {

    @XmlElement(name = "InstrPrty", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    @XmlSchemaType(name = "string")
    protected Priority2Code instrPrty;
    @XmlElement(name = "SvcLvl", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected ServiceLevel svcLvl;
    @XmlElement(name = "LclInstrm", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected LocalInstrumentSEPA lclInstrm;
    @XmlElement(name = "SeqTp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    @XmlSchemaType(name = "string")
    protected SequenceType1Code seqTp;
    @XmlElement(name = "CtgyPurp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected CategoryPurposeSEPA ctgyPurp;

    /**
     * Ruft den Wert der instrPrty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Priority2Code }
     *     
     */
    public Priority2Code getInstrPrty() {
        return instrPrty;
    }

    /**
     * Legt den Wert der instrPrty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Priority2Code }
     *     
     */
    public void setInstrPrty(Priority2Code value) {
        this.instrPrty = value;
    }

    /**
     * Ruft den Wert der svcLvl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevel }
     *     
     */
    public ServiceLevel getSvcLvl() {
        return svcLvl;
    }

    /**
     * Legt den Wert der svcLvl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevel }
     *     
     */
    public void setSvcLvl(ServiceLevel value) {
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
