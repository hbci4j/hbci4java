
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für StructuredRemittanceInformation6 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="StructuredRemittanceInformation6">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RfrdDocInf" type="{urn:sepade:xsd:pain.001.001.02}ReferredDocumentInformation1" minOccurs="0"/>
 *         &lt;element name="RfrdDocRltdDt" type="{urn:sepade:xsd:pain.001.001.02}ISODate" minOccurs="0"/>
 *         &lt;element name="RfrdDocAmt" type="{urn:sepade:xsd:pain.001.001.02}ReferredDocumentAmount1Choice" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CdtrRefInf" type="{urn:sepade:xsd:pain.001.001.02}CreditorReferenceInformation1" minOccurs="0"/>
 *         &lt;element name="Invcr" type="{urn:sepade:xsd:pain.001.001.02}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="Invcee" type="{urn:sepade:xsd:pain.001.001.02}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="AddtlRmtInf" type="{urn:sepade:xsd:pain.001.001.02}Max140Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StructuredRemittanceInformation6", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "rfrdDocInf",
    "rfrdDocRltdDt",
    "rfrdDocAmt",
    "cdtrRefInf",
    "invcr",
    "invcee",
    "addtlRmtInf"
})
public class StructuredRemittanceInformation6 {

    @XmlElement(name = "RfrdDocInf", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected ReferredDocumentInformation1 rfrdDocInf;
    @XmlElement(name = "RfrdDocRltdDt", namespace = "urn:sepade:xsd:pain.001.001.02")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar rfrdDocRltdDt;
    @XmlElement(name = "RfrdDocAmt", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected List<ReferredDocumentAmount1Choice> rfrdDocAmt;
    @XmlElement(name = "CdtrRefInf", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected CreditorReferenceInformation1 cdtrRefInf;
    @XmlElement(name = "Invcr", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected PartyIdentification8 invcr;
    @XmlElement(name = "Invcee", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected PartyIdentification8 invcee;
    @XmlElement(name = "AddtlRmtInf", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String addtlRmtInf;

    /**
     * Ruft den Wert der rfrdDocInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferredDocumentInformation1 }
     *     
     */
    public ReferredDocumentInformation1 getRfrdDocInf() {
        return rfrdDocInf;
    }

    /**
     * Legt den Wert der rfrdDocInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredDocumentInformation1 }
     *     
     */
    public void setRfrdDocInf(ReferredDocumentInformation1 value) {
        this.rfrdDocInf = value;
    }

    /**
     * Ruft den Wert der rfrdDocRltdDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRfrdDocRltdDt() {
        return rfrdDocRltdDt;
    }

    /**
     * Legt den Wert der rfrdDocRltdDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRfrdDocRltdDt(XMLGregorianCalendar value) {
        this.rfrdDocRltdDt = value;
    }

    /**
     * Gets the value of the rfrdDocAmt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rfrdDocAmt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRfrdDocAmt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferredDocumentAmount1Choice }
     * 
     * 
     */
    public List<ReferredDocumentAmount1Choice> getRfrdDocAmt() {
        if (rfrdDocAmt == null) {
            rfrdDocAmt = new ArrayList<ReferredDocumentAmount1Choice>();
        }
        return this.rfrdDocAmt;
    }

    /**
     * Ruft den Wert der cdtrRefInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreditorReferenceInformation1 }
     *     
     */
    public CreditorReferenceInformation1 getCdtrRefInf() {
        return cdtrRefInf;
    }

    /**
     * Legt den Wert der cdtrRefInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditorReferenceInformation1 }
     *     
     */
    public void setCdtrRefInf(CreditorReferenceInformation1 value) {
        this.cdtrRefInf = value;
    }

    /**
     * Ruft den Wert der invcr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getInvcr() {
        return invcr;
    }

    /**
     * Legt den Wert der invcr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setInvcr(PartyIdentification8 value) {
        this.invcr = value;
    }

    /**
     * Ruft den Wert der invcee-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getInvcee() {
        return invcee;
    }

    /**
     * Legt den Wert der invcee-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setInvcee(PartyIdentification8 value) {
        this.invcee = value;
    }

    /**
     * Ruft den Wert der addtlRmtInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddtlRmtInf() {
        return addtlRmtInf;
    }

    /**
     * Legt den Wert der addtlRmtInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddtlRmtInf(String value) {
        this.addtlRmtInf = value;
    }

}
