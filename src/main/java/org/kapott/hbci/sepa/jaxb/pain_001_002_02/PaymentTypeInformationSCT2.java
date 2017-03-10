
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentTypeInformationSCT2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformationSCT2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SvcLvl" type="{urn:swift:xsd:$pain.001.002.02}ServiceLevelSCT"/>
 *         &lt;element name="CtgyPurp" type="{urn:swift:xsd:$pain.001.002.02}PaymentCategoryPurpose1Code" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTypeInformationSCT2", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "svcLvl",
    "ctgyPurp"
})
public class PaymentTypeInformationSCT2 {

    @XmlElement(name = "SvcLvl", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected ServiceLevelSCT svcLvl;
    @XmlElement(name = "CtgyPurp", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PaymentCategoryPurpose1Code ctgyPurp;

    /**
     * Gets the value of the svcLvl property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceLevelSCT }
     *     
     */
    public ServiceLevelSCT getSvcLvl() {
        return svcLvl;
    }

    /**
     * Sets the value of the svcLvl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceLevelSCT }
     *     
     */
    public void setSvcLvl(ServiceLevelSCT value) {
        this.svcLvl = value;
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
