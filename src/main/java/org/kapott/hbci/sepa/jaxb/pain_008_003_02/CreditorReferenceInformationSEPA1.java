
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditorReferenceInformationSEPA1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditorReferenceInformationSEPA1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}CreditorReferenceTypeSEPA"/>
 *         &lt;element name="Ref" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}Max35Text"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditorReferenceInformationSEPA1", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", propOrder = {
    "tp",
    "ref"
})
public class CreditorReferenceInformationSEPA1 {

    @XmlElement(name = "Tp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected CreditorReferenceTypeSEPA tp;
    @XmlElement(name = "Ref", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected String ref;

    /**
     * Gets the value of the tp property.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceTypeSEPA }
     *     
     */
    public CreditorReferenceTypeSEPA getTp() {
        return tp;
    }

    /**
     * Sets the value of the tp property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceTypeSEPA }
     *     
     */
    public void setTp(CreditorReferenceTypeSEPA value) {
        this.tp = value;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

}
