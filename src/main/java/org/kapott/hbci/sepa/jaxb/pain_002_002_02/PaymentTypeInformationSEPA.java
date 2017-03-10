
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentTypeInformationSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstrPrty" type="{urn:swift:xsd:$pain.002.002.02}Priority2Code" minOccurs="0"/>
 *         &lt;element name="SvcLvl" type="{urn:swift:xsd:$pain.002.002.02}ServiceLevelSEPA" minOccurs="0"/>
 *         &lt;element name="LclInstrm" type="{urn:swift:xsd:$pain.002.002.02}LocalInstrumentSEPA" minOccurs="0"/>
 *         &lt;element name="SeqTp" type="{urn:swift:xsd:$pain.002.002.02}SequenceType1Code" minOccurs="0"/>
 *         &lt;element name="CtgyPurp" type="{urn:swift:xsd:$pain.002.002.02}PaymentCategoryPurpose1Code" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "instrPrty",
    "svcLvl",
    "lclInstrm",
    "seqTp",
    "ctgyPurp"
})
public class PaymentTypeInformationSEPA {

    @XmlElement(name = "InstrPrty", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected Priority2Code instrPrty;
    @XmlElement(name = "SvcLvl", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected ServiceLevelSEPA svcLvl;
    @XmlElement(name = "LclInstrm", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected LocalInstrumentSEPA lclInstrm;
    @XmlElement(name = "SeqTp", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected SequenceType1Code seqTp;
    @XmlElement(name = "CtgyPurp", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected PaymentCategoryPurpose1Code ctgyPurp;

    /**
     * Gets the value of the instrPrty property.
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
     * Sets the value of the instrPrty property.
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
     * Gets the value of the svcLvl property.
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
     * Sets the value of the svcLvl property.
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
     *     {@link PaymentCategoryPurpose1Code }
     *     
     */
    public PaymentCategoryPurpose1Code getCtgyPurp() {
        return ctgyPurp;
    }

    /**
     * Sets the value of the ctgyPurp property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCategoryPurpose1Code }
     *     
     */
    public void setCtgyPurp(PaymentCategoryPurpose1Code value) {
        this.ctgyPurp = value;
    }

}
