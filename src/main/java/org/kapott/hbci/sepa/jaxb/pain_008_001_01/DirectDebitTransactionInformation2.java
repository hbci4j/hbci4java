
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DirectDebitTransactionInformation2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DirectDebitTransactionInformation2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtId" type="{urn:sepade:xsd:pain.008.001.01}PaymentIdentification1"/>
 *         &lt;element name="InstdAmt" type="{urn:sepade:xsd:pain.008.001.01}EuroMax9Amount"/>
 *         &lt;element name="DrctDbtTx" type="{urn:sepade:xsd:pain.008.001.01}DirectDebitTransaction4"/>
 *         &lt;element name="DbtrAgt" type="{urn:sepade:xsd:pain.008.001.01}FinancialInstitution2"/>
 *         &lt;element name="Dbtr" type="{urn:sepade:xsd:pain.008.001.01}PartyIdentification23"/>
 *         &lt;element name="DbtrAcct" type="{urn:sepade:xsd:pain.008.001.01}CashAccount8"/>
 *         &lt;element name="UltmtDbtr" type="{urn:sepade:xsd:pain.008.001.01}PartyIdentification12" minOccurs="0"/>
 *         &lt;element name="RmtInf" type="{urn:sepade:xsd:pain.008.001.01}RemittanceInformation3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionInformation2", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "pmtId",
    "instdAmt",
    "drctDbtTx",
    "dbtrAgt",
    "dbtr",
    "dbtrAcct",
    "ultmtDbtr",
    "rmtInf"
})
public class DirectDebitTransactionInformation2 {

    @XmlElement(name = "PmtId", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PaymentIdentification1 pmtId;
    @XmlElement(name = "InstdAmt", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected EuroMax9Amount instdAmt;
    @XmlElement(name = "DrctDbtTx", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected DirectDebitTransaction4 drctDbtTx;
    @XmlElement(name = "DbtrAgt", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected FinancialInstitution2 dbtrAgt;
    @XmlElement(name = "Dbtr", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PartyIdentification23 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected CashAccount8 dbtrAcct;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected PartyIdentification12 ultmtDbtr;
    @XmlElement(name = "RmtInf", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected RemittanceInformation3 rmtInf;

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
     *     {@link EuroMax9Amount }
     *     
     */
    public EuroMax9Amount getInstdAmt() {
        return instdAmt;
    }

    /**
     * Legt den Wert der instdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EuroMax9Amount }
     *     
     */
    public void setInstdAmt(EuroMax9Amount value) {
        this.instdAmt = value;
    }

    /**
     * Ruft den Wert der drctDbtTx-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DirectDebitTransaction4 }
     *     
     */
    public DirectDebitTransaction4 getDrctDbtTx() {
        return drctDbtTx;
    }

    /**
     * Legt den Wert der drctDbtTx-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DirectDebitTransaction4 }
     *     
     */
    public void setDrctDbtTx(DirectDebitTransaction4 value) {
        this.drctDbtTx = value;
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
     * Ruft den Wert der ultmtDbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification12 }
     *     
     */
    public PartyIdentification12 getUltmtDbtr() {
        return ultmtDbtr;
    }

    /**
     * Legt den Wert der ultmtDbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification12 }
     *     
     */
    public void setUltmtDbtr(PartyIdentification12 value) {
        this.ultmtDbtr = value;
    }

    /**
     * Ruft den Wert der rmtInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceInformation3 }
     *     
     */
    public RemittanceInformation3 getRmtInf() {
        return rmtInf;
    }

    /**
     * Legt den Wert der rmtInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceInformation3 }
     *     
     */
    public void setRmtInf(RemittanceInformation3 value) {
        this.rmtInf = value;
    }

}
