
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PaymentTypeInformationSCT1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentTypeInformationSCT1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstrPrty" type="{urn:swift:xsd:$pain.001.002.02}Priority2Code" minOccurs="0"/>
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
@XmlType(name = "PaymentTypeInformationSCT1", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "instrPrty",
    "svcLvl",
    "ctgyPurp"
})
public class PaymentTypeInformationSCT1 {

    @XmlElement(name = "InstrPrty", namespace = "urn:swift:xsd:$pain.001.002.02")
    @XmlSchemaType(name = "string")
    protected Priority2Code instrPrty;
    @XmlElement(name = "SvcLvl", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected ServiceLevelSCT svcLvl;
    @XmlElement(name = "CtgyPurp", namespace = "urn:swift:xsd:$pain.001.002.02")
    @XmlSchemaType(name = "string")
    protected PaymentCategoryPurpose1Code ctgyPurp;

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
     *     {@link ServiceLevelSCT }
     *     
     */
    public ServiceLevelSCT getSvcLvl() {
        return svcLvl;
    }

    /**
     * Legt den Wert der svcLvl-Eigenschaft fest.
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
     * Ruft den Wert der ctgyPurp-Eigenschaft ab.
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
     * Legt den Wert der ctgyPurp-Eigenschaft fest.
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
