
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

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
 *         &lt;element name="PmtId" type="{urn:swift:xsd:$pain.008.002.01}PaymentIdentification1"/>
 *         &lt;element name="InstdAmt" type="{urn:swift:xsd:$pain.008.002.01}CurrencyAndAmountSDD"/>
 *         &lt;element name="ChrgBr" type="{urn:swift:xsd:$pain.008.002.01}ChargeBearerTypeSDDCode" minOccurs="0"/>
 *         &lt;element name="DrctDbtTx" type="{urn:swift:xsd:$pain.008.002.01}DirectDebitTransactionSDD"/>
 *         &lt;element name="UltmtCdtr" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD1" minOccurs="0"/>
 *         &lt;element name="DbtrAgt" type="{urn:swift:xsd:$pain.008.002.01}BranchAndFinancialInstitutionIdentificationSDD1"/>
 *         &lt;element name="Dbtr" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD3"/>
 *         &lt;element name="DbtrAcct" type="{urn:swift:xsd:$pain.008.002.01}CashAccountSDD2"/>
 *         &lt;element name="UltmtDbtr" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD1" minOccurs="0"/>
 *         &lt;element name="Purp" type="{urn:swift:xsd:$pain.008.002.01}PurposeSDD" minOccurs="0"/>
 *         &lt;element name="RmtInf" type="{urn:swift:xsd:$pain.008.002.01}RemittanceInformationSDDChoice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionInformationSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
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

    @XmlElement(name = "PmtId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PaymentIdentification1 pmtId;
    @XmlElement(name = "InstdAmt", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected CurrencyAndAmountSDD instdAmt;
    @XmlElement(name = "ChrgBr", namespace = "urn:swift:xsd:$pain.008.002.01")
    @XmlSchemaType(name = "string")
    protected ChargeBearerTypeSDDCode chrgBr;
    @XmlElement(name = "DrctDbtTx", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected DirectDebitTransactionSDD drctDbtTx;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PartyIdentificationSDD1 ultmtCdtr;
    @XmlElement(name = "DbtrAgt", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected BranchAndFinancialInstitutionIdentificationSDD1 dbtrAgt;
    @XmlElement(name = "Dbtr", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PartyIdentificationSDD3 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected CashAccountSDD2 dbtrAcct;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PartyIdentificationSDD1 ultmtDbtr;
    @XmlElement(name = "Purp", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PurposeSDD purp;
    @XmlElement(name = "RmtInf", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected RemittanceInformationSDDChoice rmtInf;

    /**
     * Ruft den Wert der pmtId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentIdentification1 }
     *     
     */
    public PaymentIdentification1 getPmtId() {
        return pmtId;
    }

    /**
     * Legt den Wert der pmtId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentIdentification1 }
     *     
     */
    public void setPmtId(PaymentIdentification1 value) {
        this.pmtId = value;
    }

    /**
     * Ruft den Wert der instdAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmountSDD }
     *     
     */
    public CurrencyAndAmountSDD getInstdAmt() {
        return instdAmt;
    }

    /**
     * Legt den Wert der instdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmountSDD }
     *     
     */
    public void setInstdAmt(CurrencyAndAmountSDD value) {
        this.instdAmt = value;
    }

    /**
     * Ruft den Wert der chrgBr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBearerTypeSDDCode }
     *     
     */
    public ChargeBearerTypeSDDCode getChrgBr() {
        return chrgBr;
    }

    /**
     * Legt den Wert der chrgBr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBearerTypeSDDCode }
     *     
     */
    public void setChrgBr(ChargeBearerTypeSDDCode value) {
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
     *     {@link PartyIdentificationSDD1 }
     *     
     */
    public PartyIdentificationSDD1 getUltmtCdtr() {
        return ultmtCdtr;
    }

    /**
     * Legt den Wert der ultmtCdtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD1 }
     *     
     */
    public void setUltmtCdtr(PartyIdentificationSDD1 value) {
        this.ultmtCdtr = value;
    }

    /**
     * Ruft den Wert der dbtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSDD1 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSDD1 getDbtrAgt() {
        return dbtrAgt;
    }

    /**
     * Legt den Wert der dbtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSDD1 }
     *     
     */
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentificationSDD1 value) {
        this.dbtrAgt = value;
    }

    /**
     * Ruft den Wert der dbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSDD3 }
     *     
     */
    public PartyIdentificationSDD3 getDbtr() {
        return dbtr;
    }

    /**
     * Legt den Wert der dbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD3 }
     *     
     */
    public void setDbtr(PartyIdentificationSDD3 value) {
        this.dbtr = value;
    }

    /**
     * Ruft den Wert der dbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSDD2 }
     *     
     */
    public CashAccountSDD2 getDbtrAcct() {
        return dbtrAcct;
    }

    /**
     * Legt den Wert der dbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSDD2 }
     *     
     */
    public void setDbtrAcct(CashAccountSDD2 value) {
        this.dbtrAcct = value;
    }

    /**
     * Ruft den Wert der ultmtDbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSDD1 }
     *     
     */
    public PartyIdentificationSDD1 getUltmtDbtr() {
        return ultmtDbtr;
    }

    /**
     * Legt den Wert der ultmtDbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD1 }
     *     
     */
    public void setUltmtDbtr(PartyIdentificationSDD1 value) {
        this.ultmtDbtr = value;
    }

    /**
     * Ruft den Wert der purp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PurposeSDD }
     *     
     */
    public PurposeSDD getPurp() {
        return purp;
    }

    /**
     * Legt den Wert der purp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PurposeSDD }
     *     
     */
    public void setPurp(PurposeSDD value) {
        this.purp = value;
    }

    /**
     * Ruft den Wert der rmtInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceInformationSDDChoice }
     *     
     */
    public RemittanceInformationSDDChoice getRmtInf() {
        return rmtInf;
    }

    /**
     * Legt den Wert der rmtInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceInformationSDDChoice }
     *     
     */
    public void setRmtInf(RemittanceInformationSDDChoice value) {
        this.rmtInf = value;
    }

}
