
package org.kapott.hbci.sepa.jaxb.pain_001_002_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditTransferTransactionInformationSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditTransferTransactionInformationSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtId" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PaymentIdentificationSEPA"/>
 *         &lt;element name="PmtTpInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PaymentTypeInformationSCT2" minOccurs="0"/>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}AmountTypeSEPA"/>
 *         &lt;element name="ChrgBr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}ChargeBearerTypeSEPACode" minOccurs="0"/>
 *         &lt;element name="UltmtDbtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="CdtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}BranchAndFinancialInstitutionIdentificationSEPA1"/>
 *         &lt;element name="Cdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PartyIdentificationSEPA2"/>
 *         &lt;element name="CdtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}CashAccountSEPA2"/>
 *         &lt;element name="UltmtCdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="Purp" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}PurposeSEPA" minOccurs="0"/>
 *         &lt;element name="RmtInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.002.03}RemittanceInformationSEPA1Choice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditTransferTransactionInformationSCT", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", propOrder = {
    "pmtId",
    "pmtTpInf",
    "amt",
    "chrgBr",
    "ultmtDbtr",
    "cdtrAgt",
    "cdtr",
    "cdtrAcct",
    "ultmtCdtr",
    "purp",
    "rmtInf"
})
public class CreditTransferTransactionInformationSCT {

    @XmlElement(name = "PmtId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected PaymentIdentificationSEPA pmtId;
    @XmlElement(name = "PmtTpInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected PaymentTypeInformationSCT2 pmtTpInf;
    @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected AmountTypeSEPA amt;
    @XmlElement(name = "ChrgBr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected ChargeBearerTypeSEPACode chrgBr;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected PartyIdentificationSEPA1 ultmtDbtr;
    @XmlElement(name = "CdtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected BranchAndFinancialInstitutionIdentificationSEPA1 cdtrAgt;
    @XmlElement(name = "Cdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected PartyIdentificationSEPA2 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03", required = true)
    protected CashAccountSEPA2 cdtrAcct;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected PartyIdentificationSEPA1 ultmtCdtr;
    @XmlElement(name = "Purp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected PurposeSEPA purp;
    @XmlElement(name = "RmtInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03")
    protected RemittanceInformationSEPA1Choice rmtInf;

    /**
     * Gets the value of the pmtId property.
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
     * Sets the value of the pmtId property.
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
     * Gets the value of the pmtTpInf property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypeInformationSCT2 }
     *     
     */
    public PaymentTypeInformationSCT2 getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Sets the value of the pmtTpInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformationSCT2 }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformationSCT2 value) {
        this.pmtTpInf = value;
    }

    /**
     * Gets the value of the amt property.
     * 
     * @return
     *     possible object is
     *     {@link AmountTypeSEPA }
     *     
     */
    public AmountTypeSEPA getAmt() {
        return amt;
    }

    /**
     * Sets the value of the amt property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountTypeSEPA }
     *     
     */
    public void setAmt(AmountTypeSEPA value) {
        this.amt = value;
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
     * Gets the value of the ultmtDbtr property.
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
     * Sets the value of the ultmtDbtr property.
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
     * Gets the value of the cdtr property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA2 }
     *     
     */
    public PartyIdentificationSEPA2 getCdtr() {
        return cdtr;
    }

    /**
     * Sets the value of the cdtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA2 }
     *     
     */
    public void setCdtr(PartyIdentificationSEPA2 value) {
        this.cdtr = value;
    }

    /**
     * Gets the value of the cdtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSEPA2 }
     *     
     */
    public CashAccountSEPA2 getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Sets the value of the cdtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSEPA2 }
     *     
     */
    public void setCdtrAcct(CashAccountSEPA2 value) {
        this.cdtrAcct = value;
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
     * Gets the value of the purp property.
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
     * Sets the value of the purp property.
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
     * Gets the value of the rmtInf property.
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
     * Sets the value of the rmtInf property.
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
