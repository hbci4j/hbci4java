
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für MandateRelatedInformationSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MandateRelatedInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MndtId" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}Max35Text" minOccurs="0"/>
 *         &lt;element name="DtOfSgntr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}ISODate" minOccurs="0"/>
 *         &lt;element name="AmdmntInd" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}TrueFalseIndicator" minOccurs="0"/>
 *         &lt;element name="AmdmntInfDtls" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}AmendmentInformationDetailsSEPA" minOccurs="0"/>
 *         &lt;element name="ElctrncSgntr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}Max1025Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MandateRelatedInformationSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "mndtId",
    "dtOfSgntr",
    "amdmntInd",
    "amdmntInfDtls",
    "elctrncSgntr"
})
public class MandateRelatedInformationSEPA {

    @XmlElement(name = "MndtId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected String mndtId;
    @XmlElement(name = "DtOfSgntr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dtOfSgntr;
    @XmlElement(name = "AmdmntInd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected Boolean amdmntInd;
    @XmlElement(name = "AmdmntInfDtls", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected AmendmentInformationDetailsSEPA amdmntInfDtls;
    @XmlElement(name = "ElctrncSgntr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected String elctrncSgntr;

    /**
     * Ruft den Wert der mndtId-Eigenschaft ab.
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
     * Legt den Wert der mndtId-Eigenschaft fest.
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
     * Ruft den Wert der dtOfSgntr-Eigenschaft ab.
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
     * Legt den Wert der dtOfSgntr-Eigenschaft fest.
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
     * Ruft den Wert der amdmntInd-Eigenschaft ab.
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
     * Legt den Wert der amdmntInd-Eigenschaft fest.
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
     * Ruft den Wert der amdmntInfDtls-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmendmentInformationDetailsSEPA }
     *     
     */
    public AmendmentInformationDetailsSEPA getAmdmntInfDtls() {
        return amdmntInfDtls;
    }

    /**
     * Legt den Wert der amdmntInfDtls-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmendmentInformationDetailsSEPA }
     *     
     */
    public void setAmdmntInfDtls(AmendmentInformationDetailsSEPA value) {
        this.amdmntInfDtls = value;
    }

    /**
     * Ruft den Wert der elctrncSgntr-Eigenschaft ab.
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
     * Legt den Wert der elctrncSgntr-Eigenschaft fest.
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
