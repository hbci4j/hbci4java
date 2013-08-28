
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for StructuredRemittanceInformation6 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the rfrdDocInf property.
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
     * Sets the value of the rfrdDocInf property.
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
     * Gets the value of the rfrdDocRltdDt property.
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
     * Sets the value of the rfrdDocRltdDt property.
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
     * Gets the value of the cdtrRefInf property.
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
     * Sets the value of the cdtrRefInf property.
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
     * Gets the value of the invcr property.
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
     * Sets the value of the invcr property.
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
     * Gets the value of the invcee property.
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
     * Sets the value of the invcee property.
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
     * Gets the value of the addtlRmtInf property.
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
     * Sets the value of the addtlRmtInf property.
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
