
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MandateRelatedInformationSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MandateRelatedInformationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MndtId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}RestrictedIdentificationSEPA1"/>
 *         &lt;element name="DtOfSgntr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}ISODate"/>
 *         &lt;element name="AmdmntInd" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}TrueFalseIndicator" minOccurs="0"/>
 *         &lt;element name="AmdmntInfDtls" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}AmendmentInformationDetailsSDD" minOccurs="0"/>
 *         &lt;element name="ElctrncSgntr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}Max1025Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MandateRelatedInformationSDD", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", propOrder = {
    "mndtId",
    "dtOfSgntr",
    "amdmntInd",
    "amdmntInfDtls",
    "elctrncSgntr"
})
public class MandateRelatedInformationSDD {

    @XmlElement(name = "MndtId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected String mndtId;
    @XmlElement(name = "DtOfSgntr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected XMLGregorianCalendar dtOfSgntr;
    @XmlElement(name = "AmdmntInd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected Boolean amdmntInd;
    @XmlElement(name = "AmdmntInfDtls", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected AmendmentInformationDetailsSDD amdmntInfDtls;
    @XmlElement(name = "ElctrncSgntr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected String elctrncSgntr;

    /**
     * Gets the value of the mndtId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMndtId() {
        return mndtId;
    }

    /**
     * Sets the value of the mndtId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMndtId(String value) {
        this.mndtId = value;
    }

    /**
     * Gets the value of the dtOfSgntr property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDtOfSgntr() {
        return dtOfSgntr;
    }

    /**
     * Sets the value of the dtOfSgntr property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDtOfSgntr(XMLGregorianCalendar value) {
        this.dtOfSgntr = value;
    }

    /**
     * Gets the value of the amdmntInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAmdmntInd() {
        return amdmntInd;
    }

    /**
     * Sets the value of the amdmntInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAmdmntInd(Boolean value) {
        this.amdmntInd = value;
    }

    /**
     * Gets the value of the amdmntInfDtls property.
     * 
     * @return
     *     possible object is
     *     {@link AmendmentInformationDetailsSDD }
     *     
     */
    public AmendmentInformationDetailsSDD getAmdmntInfDtls() {
        return amdmntInfDtls;
    }

    /**
     * Sets the value of the amdmntInfDtls property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmendmentInformationDetailsSDD }
     *     
     */
    public void setAmdmntInfDtls(AmendmentInformationDetailsSDD value) {
        this.amdmntInfDtls = value;
    }

    /**
     * Gets the value of the elctrncSgntr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElctrncSgntr() {
        return elctrncSgntr;
    }

    /**
     * Sets the value of the elctrncSgntr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElctrncSgntr(String value) {
        this.elctrncSgntr = value;
    }

}
