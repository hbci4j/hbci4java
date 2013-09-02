
package org.kapott.hbci.sepa.jaxb.pain_008_002_02;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PaymentInstructionInformationSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentInstructionInformationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtInfId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}RestrictedIdentificationSEPA1"/>
 *         &lt;element name="PmtMtd" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PaymentMethod2Code"/>
 *         &lt;element name="BtchBookg" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}BatchBookingIndicator" minOccurs="0"/>
 *         &lt;element name="NbOfTxs" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}Max15NumericText" minOccurs="0"/>
 *         &lt;element name="CtrlSum" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}DecimalNumber" minOccurs="0"/>
 *         &lt;element name="PmtTpInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PaymentTypeInformationSDD"/>
 *         &lt;element name="ReqdColltnDt" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}ISODate"/>
 *         &lt;element name="Cdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PartyIdentificationSEPA5"/>
 *         &lt;element name="CdtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}CashAccountSEPA1"/>
 *         &lt;element name="CdtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}BranchAndFinancialInstitutionIdentificationSEPA1"/>
 *         &lt;element name="UltmtCdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="ChrgBr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}ChargeBearerTypeSEPACode" minOccurs="0"/>
 *         &lt;element name="CdtrSchmeId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PartyIdentificationSEPA3" minOccurs="0"/>
 *         &lt;element name="DrctDbtTxInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}DirectDebitTransactionInformationSDD" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstructionInformationSDD", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", propOrder = {
    "pmtInfId",
    "pmtMtd",
    "btchBookg",
    "nbOfTxs",
    "ctrlSum",
    "pmtTpInf",
    "reqdColltnDt",
    "cdtr",
    "cdtrAcct",
    "cdtrAgt",
    "ultmtCdtr",
    "chrgBr",
    "cdtrSchmeId",
    "drctDbtTxInf"
})
public class PaymentInstructionInformationSDD {

    @XmlElement(name = "PmtInfId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected String pmtInfId;
    @XmlElement(name = "PmtMtd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected PaymentMethod2Code pmtMtd;
    @XmlElement(name = "BtchBookg", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected Boolean btchBookg;
    @XmlElement(name = "NbOfTxs", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected String nbOfTxs;
    @XmlElement(name = "CtrlSum", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected BigDecimal ctrlSum;
    @XmlElement(name = "PmtTpInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected PaymentTypeInformationSDD pmtTpInf;
    @XmlElement(name = "ReqdColltnDt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected XMLGregorianCalendar reqdColltnDt;
    @XmlElement(name = "Cdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected PartyIdentificationSEPA5 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected CashAccountSEPA1 cdtrAcct;
    @XmlElement(name = "CdtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected BranchAndFinancialInstitutionIdentificationSEPA1 cdtrAgt;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected PartyIdentificationSEPA1 ultmtCdtr;
    @XmlElement(name = "ChrgBr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected ChargeBearerTypeSEPACode chrgBr;
    @XmlElement(name = "CdtrSchmeId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected PartyIdentificationSEPA3 cdtrSchmeId;
    @XmlElement(name = "DrctDbtTxInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected List<DirectDebitTransactionInformationSDD> drctDbtTxInf;

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
     *     {@link PaymentMethod2Code }
     *     
     */
    public PaymentMethod2Code getPmtMtd() {
        return pmtMtd;
    }

    /**
     * Sets the value of the pmtMtd property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethod2Code }
     *     
     */
    public void setPmtMtd(PaymentMethod2Code value) {
        this.pmtMtd = value;
    }

    /**
     * Gets the value of the btchBookg property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBtchBookg() {
        return btchBookg;
    }

    /**
     * Sets the value of the btchBookg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBtchBookg(Boolean value) {
        this.btchBookg = value;
    }

    /**
     * Gets the value of the nbOfTxs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNbOfTxs() {
        return nbOfTxs;
    }

    /**
     * Sets the value of the nbOfTxs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNbOfTxs(String value) {
        this.nbOfTxs = value;
    }

    /**
     * Gets the value of the ctrlSum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCtrlSum() {
        return ctrlSum;
    }

    /**
     * Sets the value of the ctrlSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCtrlSum(BigDecimal value) {
        this.ctrlSum = value;
    }

    /**
     * Gets the value of the pmtTpInf property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypeInformationSDD }
     *     
     */
    public PaymentTypeInformationSDD getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Sets the value of the pmtTpInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformationSDD }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformationSDD value) {
        this.pmtTpInf = value;
    }

    /**
     * Gets the value of the reqdColltnDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReqdColltnDt() {
        return reqdColltnDt;
    }

    /**
     * Sets the value of the reqdColltnDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReqdColltnDt(XMLGregorianCalendar value) {
        this.reqdColltnDt = value;
    }

    /**
     * Gets the value of the cdtr property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA5 }
     *     
     */
    public PartyIdentificationSEPA5 getCdtr() {
        return cdtr;
    }

    /**
     * Sets the value of the cdtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA5 }
     *     
     */
    public void setCdtr(PartyIdentificationSEPA5 value) {
        this.cdtr = value;
    }

    /**
     * Gets the value of the cdtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSEPA1 }
     *     
     */
    public CashAccountSEPA1 getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Sets the value of the cdtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSEPA1 }
     *     
     */
    public void setCdtrAcct(CashAccountSEPA1 value) {
        this.cdtrAcct = value;
    }

    /**
     * Gets the value of the cdtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSEPA1 getCdtrAgt() {
        return cdtrAgt;
    }

    /**
     * Sets the value of the cdtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentificationSEPA1 value) {
        this.cdtrAgt = value;
    }

    /**
     * Gets the value of the ultmtCdtr property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA1 }
     *     
     */
    public PartyIdentificationSEPA1 getUltmtCdtr() {
        return ultmtCdtr;
    }

    /**
     * Sets the value of the ultmtCdtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA1 }
     *     
     */
    public void setUltmtCdtr(PartyIdentificationSEPA1 value) {
        this.ultmtCdtr = value;
    }

    /**
     * Gets the value of the chrgBr property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBearerTypeSEPACode }
     *     
     */
    public ChargeBearerTypeSEPACode getChrgBr() {
        return chrgBr;
    }

    /**
     * Sets the value of the chrgBr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBearerTypeSEPACode }
     *     
     */
    public void setChrgBr(ChargeBearerTypeSEPACode value) {
        this.chrgBr = value;
    }

    /**
     * Gets the value of the cdtrSchmeId property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA3 }
     *     
     */
    public PartyIdentificationSEPA3 getCdtrSchmeId() {
        return cdtrSchmeId;
    }

    /**
     * Sets the value of the cdtrSchmeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA3 }
     *     
     */
    public void setCdtrSchmeId(PartyIdentificationSEPA3 value) {
        this.cdtrSchmeId = value;
    }

    /**
     * Gets the value of the drctDbtTxInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the drctDbtTxInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDrctDbtTxInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DirectDebitTransactionInformationSDD }
     * 
     * 
     */
    public List<DirectDebitTransactionInformationSDD> getDrctDbtTxInf() {
        if (drctDbtTxInf == null) {
            drctDbtTxInf = new ArrayList<DirectDebitTransactionInformationSDD>();
        }
        return this.drctDbtTxInf;
    }

}
