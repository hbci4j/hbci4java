
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PaymentInstructionInformation4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
    protected PaymentMethod5Code pmtMtd;
    @XmlElement(name = "PmtTpInf", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PaymentTypeInformation7 pmtTpInf;
    @XmlElement(name = "ReqdExctnDt", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected XMLGregorianCalendar reqdExctnDt;
    @XmlElement(name = "Dbtr", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PartyIdentification23 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected CashAccount8 dbtrAcct;
    @XmlElement(name = "DbtrAgt", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected FinancialInstitution2 dbtrAgt;
    @XmlElement(name = "ChrgBr", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected ChargeBearerType2Code chrgBr;
    @XmlElement(name = "CdtTrfTxInf", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected List<CreditTransferTransactionInformation2> cdtTrfTxInf;

    /**
     * Gets the value of the pmtInfId property.
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
     * Sets the value of the pmtInfId property.
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
     * Gets the value of the pmtMtd property.
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
     * Sets the value of the pmtMtd property.
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
     * Gets the value of the pmtTpInf property.
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
     * Sets the value of the pmtTpInf property.
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
     * Gets the value of the reqdExctnDt property.
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
     * Sets the value of the reqdExctnDt property.
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
     * Gets the value of the dbtr property.
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
     * Sets the value of the dbtr property.
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
     * Gets the value of the dbtrAcct property.
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
     * Sets the value of the dbtrAcct property.
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
     * Gets the value of the dbtrAgt property.
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
     * Sets the value of the dbtrAgt property.
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
     * Gets the value of the chrgBr property.
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
     * Sets the value of the chrgBr property.
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
