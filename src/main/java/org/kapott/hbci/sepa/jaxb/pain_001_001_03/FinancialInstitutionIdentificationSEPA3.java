
package org.kapott.hbci.sepa.jaxb.pain_001_001_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FinancialInstitutionIdentificationSEPA3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitutionIdentificationSEPA3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="BIC" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}BICIdentifier"/>
 *           &lt;element name="Othr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}OthrIdentification"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionIdentificationSEPA3", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03", propOrder = {
    "bic",
    "othr"
})
public class FinancialInstitutionIdentificationSEPA3 {

    @XmlElement(name = "BIC", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03")
    protected String bic;
    @XmlElement(name = "Othr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03")
    protected OthrIdentification othr;

    /**
     * Gets the value of the bic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIC() {
        return bic;
    }

    /**
     * Sets the value of the bic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIC(String value) {
        this.bic = value;
    }

    /**
     * Gets the value of the othr property.
     * 
     * @return
     *     possible object is
     *     {@link OthrIdentification }
     *     
     */
    public OthrIdentification getOthr() {
        return othr;
    }

    /**
     * Sets the value of the othr property.
     * 
     * @param value
     *     allowed object is
     *     {@link OthrIdentification }
     *     
     */
    public void setOthr(OthrIdentification value) {
        this.othr = value;
    }

}
