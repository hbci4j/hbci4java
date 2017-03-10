
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PaymentInstructionInformationSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentInstructionInformationSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtInfId" type="{urn:swift:xsd:$pain.001.002.02}RestrictedIdentification2" minOccurs="0"/>
 *         &lt;element name="PmtMtd" type="{urn:swift:xsd:$pain.001.002.02}PaymentMethodSCTCode"/>
 *         &lt;element name="PmtTpInf" type="{urn:swift:xsd:$pain.001.002.02}PaymentTypeInformationSCT1" minOccurs="0"/>
 *         &lt;element name="ReqdExctnDt" type="{urn:swift:xsd:$pain.001.002.02}ISODate"/>
 *         &lt;element name="Dbtr" type="{urn:swift:xsd:$pain.001.002.02}PartyIdentificationSCT2"/>
 *         &lt;element name="DbtrAcct" type="{urn:swift:xsd:$pain.001.002.02}CashAccountSCT1"/>
 *         &lt;element name="DbtrAgt" type="{urn:swift:xsd:$pain.001.002.02}BranchAndFinancialInstitutionIdentificationSCT"/>
 *         &lt;element name="UltmtDbtr" type="{urn:swift:xsd:$pain.001.002.02}PartyIdentificationSCT1" minOccurs="0"/>
 *         &lt;element name="ChrgBr" type="{urn:swift:xsd:$pain.001.002.02}ChargeBearerTypeSCTCode" minOccurs="0"/>
 *         &lt;element name="CdtTrfTxInf" type="{urn:swift:xsd:$pain.001.002.02}CreditTransferTransactionInformationSCT" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstructionInformationSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "pmtInfId",
    "pmtMtd",
    "pmtTpInf",
    "reqdExctnDt",
    "dbtr",
    "dbtrAcct",
    "dbtrAgt",
    "ultmtDbtr",
    "chrgBr",
    "cdtTrfTxInf"
})
public class PaymentInstructionInformationSCT {

    @XmlElement(name = "PmtInfId", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected String pmtInfId;
    @XmlElement(name = "PmtMtd", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected PaymentMethodSCTCode pmtMtd;
    @XmlElement(name = "PmtTpInf", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PaymentTypeInformationSCT1 pmtTpInf;
    @XmlElement(name = "ReqdExctnDt", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected XMLGregorianCalendar reqdExctnDt;
    @XmlElement(name = "Dbtr", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected PartyIdentificationSCT2 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected CashAccountSCT1 dbtrAcct;
    @XmlElement(name = "DbtrAgt", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected BranchAndFinancialInstitutionIdentificationSCT dbtrAgt;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PartyIdentificationSCT1 ultmtDbtr;
    @XmlElement(name = "ChrgBr", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected ChargeBearerTypeSCTCode chrgBr;
    @XmlElement(name = "CdtTrfTxInf", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected List<CreditTransferTransactionInformationSCT> cdtTrfTxInf;

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
     *     {@link PaymentMethodSCTCode }
     *     
     */
    public PaymentMethodSCTCode getPmtMtd() {
        return pmtMtd;
    }

    /**
     * Sets the value of the pmtMtd property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodSCTCode }
     *     
     */
    public void setPmtMtd(PaymentMethodSCTCode value) {
        this.pmtMtd = value;
    }

    /**
     * Gets the value of the pmtTpInf property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypeInformationSCT1 }
     *     
     */
    public PaymentTypeInformationSCT1 getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Sets the value of the pmtTpInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformationSCT1 }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformationSCT1 value) {
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
     *     {@link PartyIdentificationSCT2 }
     *     
     */
    public PartyIdentificationSCT2 getDbtr() {
        return dbtr;
    }

    /**
     * Sets the value of the dbtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSCT2 }
     *     
     */
    public void setDbtr(PartyIdentificationSCT2 value) {
        this.dbtr = value;
    }

    /**
     * Gets the value of the dbtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSCT1 }
     *     
     */
    public CashAccountSCT1 getDbtrAcct() {
        return dbtrAcct;
    }

    /**
     * Sets the value of the dbtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSCT1 }
     *     
     */
    public void setDbtrAcct(CashAccountSCT1 value) {
        this.dbtrAcct = value;
    }

    /**
     * Gets the value of the dbtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSCT }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSCT getDbtrAgt() {
        return dbtrAgt;
    }

    /**
     * Sets the value of the dbtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSCT }
     *     
     */
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentificationSCT value) {
        this.dbtrAgt = value;
    }

    /**
     * Gets the value of the ultmtDbtr property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSCT1 }
     *     
     */
    public PartyIdentificationSCT1 getUltmtDbtr() {
        return ultmtDbtr;
    }

    /**
     * Sets the value of the ultmtDbtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSCT1 }
     *     
     */
    public void setUltmtDbtr(PartyIdentificationSCT1 value) {
        this.ultmtDbtr = value;
    }

    /**
     * Gets the value of the chrgBr property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBearerTypeSCTCode }
     *     
     */
    public ChargeBearerTypeSCTCode getChrgBr() {
        return chrgBr;
    }

    /**
     * Sets the value of the chrgBr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBearerTypeSCTCode }
     *     
     */
    public void setChrgBr(ChargeBearerTypeSCTCode value) {
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
     * {@link CreditTransferTransactionInformationSCT }
     * 
     * 
     */
    public List<CreditTransferTransactionInformationSCT> getCdtTrfTxInf() {
        if (cdtTrfTxInf == null) {
            cdtTrfTxInf = new ArrayList<CreditTransferTransactionInformationSCT>();
        }
        return this.cdtTrfTxInf;
    }

}
