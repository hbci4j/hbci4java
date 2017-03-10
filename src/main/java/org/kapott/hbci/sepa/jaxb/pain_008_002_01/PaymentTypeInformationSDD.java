
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

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
 *         &lt;element name="SvcLvl" type="{urn:swift:xsd:$pain.008.002.01}ServiceLevelSDD"/>
 *         &lt;element name="LclInstrm" type="{urn:swift:xsd:$pain.008.002.01}LocalInstrumentSDD"/>
 *         &lt;element name="SeqTp" type="{urn:swift:xsd:$pain.008.002.01}SequenceType1Code"/>
 *         &lt;element name="CtgyPurp" type="{urn:swift:xsd:$pain.008.002.01}PaymentCategoryPurpose1Code" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformationSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "svcLvl",
    "lclInstrm",
    "seqTp",
    "ctgyPurp"
})
public class PaymentTypeInformationSDD {

    @XmlElement(name = "SvcLvl", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected ServiceLevelSDD svcLvl;
    @XmlElement(name = "LclInstrm", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected LocalInstrumentSDD lclInstrm;
    @XmlElement(name = "SeqTp", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected SequenceType1Code seqTp;
    @XmlElement(name = "CtgyPurp", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PaymentCategoryPurpose1Code ctgyPurp;

    /**
     * Gets the value of the svcLvl property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelSDD }
     *     
     */
    public ServiceLevelSDD getSvcLvl() {
        return svcLvl;
    }

    /**
     * Sets the value of the svcLvl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelSDD }
     *     
     */
    public void setSvcLvl(ServiceLevelSDD value) {
        this.svcLvl = value;
    }

    /**
     * Gets the value of the lclInstrm property.
     * 
     * @return
     *     possible object is
     *     {@link LocalInstrumentSDD }
     *     
     */
    public LocalInstrumentSDD getLclInstrm() {
        return lclInstrm;
    }

    /**
     * Sets the value of the lclInstrm property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalInstrumentSDD }
     *     
     */
    public void setLclInstrm(LocalInstrumentSDD value) {
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
