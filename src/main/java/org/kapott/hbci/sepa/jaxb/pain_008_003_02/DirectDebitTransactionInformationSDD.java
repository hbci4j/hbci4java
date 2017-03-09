
package org.kapott.hbci.sepa.jaxb.pain_008_003_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DirectDebitTransactionInformationSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DirectDebitTransactionInformationSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}PaymentIdentificationSEPA"/>
 *         &lt;element name="InstdAmt" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}ActiveOrHistoricCurrencyAndAmountSEPA"/>
 *         &lt;element name="ChrgBr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}ChargeBearerTypeSEPACode" minOccurs="0"/>
 *         &lt;element name="DrctDbtTx" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}DirectDebitTransactionSDD"/>
 *         &lt;element name="UltmtCdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="DbtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}BranchAndFinancialInstitutionIdentificationSEPA3"/>
 *         &lt;element name="Dbtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}PartyIdentificationSEPA2"/>
 *         &lt;element name="DbtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}CashAccountSEPA2"/>
 *         &lt;element name="UltmtDbtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="Purp" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}PurposeSEPA" minOccurs="0"/>
 *         &lt;element name="RmtInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.003.02}RemittanceInformationSEPA1Choice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionInformationSDD", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", propOrder = {
    "pmtId",
    "instdAmt",
    "chrgBr",
    "drctDbtTx",
    "ultmtCdtr",
    "dbtrAgt",
    "dbtr",
    "dbtrAcct",
    "ultmtDbtr",
    "purp",
    "rmtInf"
})
public class DirectDebitTransactionInformationSDD {

    @XmlElement(name = "PmtId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected PaymentIdentificationSEPA pmtId;
    @XmlElement(name = "InstdAmt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected ActiveOrHistoricCurrencyAndAmountSEPA instdAmt;
    @XmlElement(name = "ChrgBr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
    @XmlSchemaType(name = "string")
    protected ChargeBearerTypeSEPACode chrgBr;
    @XmlElement(name = "DrctDbtTx", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected DirectDebitTransactionSDD drctDbtTx;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
    protected PartyIdentificationSEPA1 ultmtCdtr;
    @XmlElement(name = "DbtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected BranchAndFinancialInstitutionIdentificationSEPA3 dbtrAgt;
    @XmlElement(name = "Dbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected PartyIdentificationSEPA2 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02", required = true)
    protected CashAccountSEPA2 dbtrAcct;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
    protected PartyIdentificationSEPA1 ultmtDbtr;
    @XmlElement(name = "Purp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
    protected PurposeSEPA purp;
    @XmlElement(name = "RmtInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.003.02")
    protected RemittanceInformationSEPA1Choice rmtInf;

    /**
     * Ruft den Wert der pmtId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentIdentificationSEPA }
     *     
     */
    public PaymentIdentificationSEPA getPmtId() {
        return pmtId;
    }

    /**
     * Legt den Wert der pmtId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentIdentificationSEPA }
     *     
     */
    public void setPmtId(PaymentIdentificationSEPA value) {
        this.pmtId = value;
    }

    /**
     * Ruft den Wert der instdAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ActiveOrHistoricCurrencyAndAmountSEPA }
     *     
     */
    public ActiveOrHistoricCurrencyAndAmountSEPA getInstdAmt() {
        return instdAmt;
    }

    /**
     * Legt den Wert der instdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveOrHistoricCurrencyAndAmountSEPA }
     *     
     */
    public void setInstdAmt(ActiveOrHistoricCurrencyAndAmountSEPA value) {
        this.instdAmt = value;
    }

    /**
     * Ruft den Wert der chrgBr-Eigenschaft ab.
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
     * Legt den Wert der chrgBr-Eigenschaft fest.
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
     * Ruft den Wert der drctDbtTx-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DirectDebitTransactionSDD }
     *     
     */
    public DirectDebitTransactionSDD getDrctDbtTx() {
        return drctDbtTx;
    }

    /**
     * Legt den Wert der drctDbtTx-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DirectDebitTransactionSDD }
     *     
     */
    public void setDrctDbtTx(DirectDebitTransactionSDD value) {
        this.drctDbtTx = value;
    }

    /**
     * Ruft den Wert der ultmtCdtr-Eigenschaft ab.
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
     * Legt den Wert der ultmtCdtr-Eigenschaft fest.
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
     * Ruft den Wert der dbtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA3 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSEPA3 getDbtrAgt() {
        return dbtrAgt;
    }

    /**
     * Legt den Wert der dbtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA3 }
     *     
     */
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentificationSEPA3 value) {
        this.dbtrAgt = value;
    }

    /**
     * Ruft den Wert der dbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA2 }
     *     
     */
    public PartyIdentificationSEPA2 getDbtr() {
        return dbtr;
    }

    /**
     * Legt den Wert der dbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA2 }
     *     
     */
    public void setDbtr(PartyIdentificationSEPA2 value) {
        this.dbtr = value;
    }

    /**
     * Ruft den Wert der dbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSEPA2 }
     *     
     */
    public CashAccountSEPA2 getDbtrAcct() {
        return dbtrAcct;
    }

    /**
     * Legt den Wert der dbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSEPA2 }
     *     
     */
    public void setDbtrAcct(CashAccountSEPA2 value) {
        this.dbtrAcct = value;
    }

    /**
     * Ruft den Wert der ultmtDbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA1 }
     *     
     */
    public PartyIdentificationSEPA1 getUltmtDbtr() {
        return ultmtDbtr;
    }

    /**
     * Legt den Wert der ultmtDbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA1 }
     *     
     */
    public void setUltmtDbtr(PartyIdentificationSEPA1 value) {
        this.ultmtDbtr = value;
    }

    /**
     * Ruft den Wert der purp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PurposeSEPA }
     *     
     */
    public PurposeSEPA getPurp() {
        return purp;
    }

    /**
     * Legt den Wert der purp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PurposeSEPA }
     *     
     */
    public void setPurp(PurposeSEPA value) {
        this.purp = value;
    }

    /**
     * Ruft den Wert der rmtInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceInformationSEPA1Choice }
     *     
     */
    public RemittanceInformationSEPA1Choice getRmtInf() {
        return rmtInf;
    }

    /**
     * Legt den Wert der rmtInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceInformationSEPA1Choice }
     *     
     */
    public void setRmtInf(RemittanceInformationSEPA1Choice value) {
        this.rmtInf = value;
    }

}
