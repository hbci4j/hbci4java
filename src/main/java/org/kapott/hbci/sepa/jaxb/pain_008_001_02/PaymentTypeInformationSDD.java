
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentTypeInformationSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SvcLvl" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}ServiceLevel"/>
 *         &lt;element name="LclInstrm" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}LocalInstrumentSEPA"/>
 *         &lt;element name="SeqTp" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}SequenceType1Code"/>
 *         &lt;element name="CtgyPurp" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}CategoryPurposeSEPA" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformationSDD", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", propOrder = {
    "svcLvl",
    "lclInstrm",
    "seqTp",
    "ctgyPurp"
})
public class PaymentTypeInformationSDD {

    @XmlElement(name = "SvcLvl", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected ServiceLevel svcLvl;
    @XmlElement(name = "LclInstrm", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected LocalInstrumentSEPA lclInstrm;
    @XmlElement(name = "SeqTp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected SequenceType1Code seqTp;
    @XmlElement(name = "CtgyPurp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected CategoryPurposeSEPA ctgyPurp;

    /**
     * Gets the value of the svcLvl property.
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
     * Sets the value of the svcLvl property.
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
     * Gets the value of the lclInstrm property.
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
     * Sets the value of the lclInstrm property.
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
     * Gets the value of the seqTp property.
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
     * Sets the value of the seqTp property.
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
     * Gets the value of the ctgyPurp property.
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
     * Sets the value of the ctgyPurp property.
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
