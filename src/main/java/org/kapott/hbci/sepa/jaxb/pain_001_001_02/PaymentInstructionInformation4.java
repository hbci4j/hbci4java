
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
 * <p>Java-Klasse für PaymentInstructionInformation4 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentInstructionInformation4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtInfId" type="{urn:sepade:xsd:pain.001.001.02}Max35Text" minOccurs="0"/>
 *         &lt;element name="PmtMtd" type="{urn:sepade:xsd:pain.001.001.02}PaymentMethod5Code"/>
 *         &lt;element name="PmtTpInf" type="{urn:sepade:xsd:pain.001.001.02}PaymentTypeInformation7"/>
 *         &lt;element name="ReqdExctnDt" type="{urn:sepade:xsd:pain.001.001.02}ISODate"/>
 *         &lt;element name="Dbtr" type="{urn:sepade:xsd:pain.001.001.02}PartyIdentification23"/>
 *         &lt;element name="DbtrAcct" type="{urn:sepade:xsd:pain.001.001.02}CashAccount8"/>
 *         &lt;element name="DbtrAgt" type="{urn:sepade:xsd:pain.001.001.02}FinancialInstitution2"/>
 *         &lt;element name="ChrgBr" type="{urn:sepade:xsd:pain.001.001.02}ChargeBearerType2Code"/>
 *         &lt;element name="CdtTrfTxInf" type="{urn:sepade:xsd:pain.001.001.02}CreditTransferTransactionInformation2" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstructionInformation4", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "pmtInfId",
    "pmtMtd",
    "pmtTpInf",
    "reqdExctnDt",
    "dbtr",
    "dbtrAcct",
    "dbtrAgt",
    "chrgBr",
    "cdtTrfTxInf"
})
public class PaymentInstructionInformation4 {

    @XmlElement(name = "PmtInfId", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String pmtInfId;
    @XmlElement(name = "PmtMtd", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    @XmlSchemaType(name = "string")
    protected PaymentMethod5Code pmtMtd;
    @XmlElement(name = "PmtTpInf", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PaymentTypeInformation7 pmtTpInf;
    @XmlElement(name = "ReqdExctnDt", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reqdExctnDt;
    @XmlElement(name = "Dbtr", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PartyIdentification23 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected CashAccount8 dbtrAcct;
    @XmlElement(name = "DbtrAgt", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected FinancialInstitution2 dbtrAgt;
    @XmlElement(name = "ChrgBr", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    @XmlSchemaType(name = "string")
    protected ChargeBearerType2Code chrgBr;
    @XmlElement(name = "CdtTrfTxInf", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected List<CreditTransferTransactionInformation2> cdtTrfTxInf;

    /**
     * Ruft den Wert der pmtInfId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPmtInfId() {
        return pmtInfId;
    }

    /**
     * Legt den Wert der pmtInfId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPmtInfId(String value) {
        this.pmtInfId = value;
    }

    /**
     * Ruft den Wert der pmtMtd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethod5Code }
     *     
     */
    public PaymentMethod5Code getPmtMtd() {
        return pmtMtd;
    }

    /**
     * Legt den Wert der pmtMtd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethod5Code }
     *     
     */
    public void setPmtMtd(PaymentMethod5Code value) {
        this.pmtMtd = value;
    }

    /**
     * Ruft den Wert der pmtTpInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypeInformation7 }
     *     
     */
    public PaymentTypeInformation7 getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Legt den Wert der pmtTpInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformation7 }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformation7 value) {
        this.pmtTpInf = value;
    }

    /**
     * Ruft den Wert der reqdExctnDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReqdExctnDt() {
        return reqdExctnDt;
    }

    /**
     * Legt den Wert der reqdExctnDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReqdExctnDt(XMLGregorianCalendar value) {
        this.reqdExctnDt = value;
    }

    /**
     * Ruft den Wert der dbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification23 }
     *     
     */
    public PartyIdentification23 getDbtr() {
        return dbtr;
    }

    /**
     * Legt den Wert der dbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification23 }
     *     
     */
    public void setDbtr(PartyIdentification23 value) {
        this.dbtr = value;
    }

    /**
     * Ruft den Wert der dbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount8 }
     *     
     */
    public CashAccount8 getDbtrAcct() {
        return dbtrAcct;
    }

    /**
     * Legt den Wert der dbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount8 }
     *     
     */
    public void setDbtrAcct(CashAccount8 value) {
        this.dbtrAcct = value;
    }

    /**
     * Ruft den Wert der dbtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitution2 }
     *     
     */
    public FinancialInstitution2 getDbtrAgt() {
        return dbtrAgt;
    }

    /**
     * Legt den Wert der dbtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitution2 }
     *     
     */
    public void setDbtrAgt(FinancialInstitution2 value) {
        this.dbtrAgt = value;
    }

    /**
     * Ruft den Wert der chrgBr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBearerType2Code }
     *     
     */
    public ChargeBearerType2Code getChrgBr() {
        return chrgBr;
    }

    /**
     * Legt den Wert der chrgBr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBearerType2Code }
     *     
     */
    public void setChrgBr(ChargeBearerType2Code value) {
        this.chrgBr = value;
    }

    /**
     * Gets the value of the cdtTrfTxInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cdtTrfTxInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCdtTrfTxInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CreditTransferTransactionInformation2 }
     * 
     * 
     */
    public List<CreditTransferTransactionInformation2> getCdtTrfTxInf() {
        if (cdtTrfTxInf == null) {
            cdtTrfTxInf = new ArrayList<CreditTransferTransactionInformation2>();
        }
        return this.cdtTrfTxInf;
    }

}
