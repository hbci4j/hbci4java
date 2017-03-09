
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CreditTransferTransactionInformationSCT complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CreditTransferTransactionInformationSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtId" type="{urn:swift:xsd:$pain.001.002.02}PaymentIdentification1"/>
 *         &lt;element name="PmtTpInf" type="{urn:swift:xsd:$pain.001.002.02}PaymentTypeInformationSCT2" minOccurs="0"/>
 *         &lt;element name="Amt" type="{urn:swift:xsd:$pain.001.002.02}AmountTypeSCT"/>
 *         &lt;element name="ChrgBr" type="{urn:swift:xsd:$pain.001.002.02}ChargeBearerTypeSCTCode" minOccurs="0"/>
 *         &lt;element name="UltmtDbtr" type="{urn:swift:xsd:$pain.001.002.02}PartyIdentificationSCT1" minOccurs="0"/>
 *         &lt;element name="CdtrAgt" type="{urn:swift:xsd:$pain.001.002.02}BranchAndFinancialInstitutionIdentificationSCT"/>
 *         &lt;element name="Cdtr" type="{urn:swift:xsd:$pain.001.002.02}PartyIdentificationSCT2"/>
 *         &lt;element name="CdtrAcct" type="{urn:swift:xsd:$pain.001.002.02}CashAccountSCT2"/>
 *         &lt;element name="UltmtCdtr" type="{urn:swift:xsd:$pain.001.002.02}PartyIdentificationSCT1" minOccurs="0"/>
 *         &lt;element name="Purp" type="{urn:swift:xsd:$pain.001.002.02}PurposeSCT" minOccurs="0"/>
 *         &lt;element name="RmtInf" type="{urn:swift:xsd:$pain.001.002.02}RemittanceInformationSCTChoice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditTransferTransactionInformationSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
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

    @XmlElement(name = "PmtId", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected PaymentIdentification1 pmtId;
    @XmlElement(name = "PmtTpInf", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PaymentTypeInformationSCT2 pmtTpInf;
    @XmlElement(name = "Amt", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected AmountTypeSCT amt;
    @XmlElement(name = "ChrgBr", namespace = "urn:swift:xsd:$pain.001.002.02")
    @XmlSchemaType(name = "string")
    protected ChargeBearerTypeSCTCode chrgBr;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PartyIdentificationSCT1 ultmtDbtr;
    @XmlElement(name = "CdtrAgt", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected BranchAndFinancialInstitutionIdentificationSCT cdtrAgt;
    @XmlElement(name = "Cdtr", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected PartyIdentificationSCT2 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected CashAccountSCT2 cdtrAcct;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PartyIdentificationSCT1 ultmtCdtr;
    @XmlElement(name = "Purp", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PurposeSCT purp;
    @XmlElement(name = "RmtInf", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected RemittanceInformationSCTChoice rmtInf;

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
     * Ruft den Wert der pmtTpInf-Eigenschaft ab.
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
     * Legt den Wert der pmtTpInf-Eigenschaft fest.
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
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountTypeSCT }
     *     
     */
    public AmountTypeSCT getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountTypeSCT }
     *     
     */
    public void setAmt(AmountTypeSCT value) {
        this.amt = value;
    }

    /**
     * Ruft den Wert der chrgBr-Eigenschaft ab.
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
     * Legt den Wert der chrgBr-Eigenschaft fest.
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
     * Ruft den Wert der ultmtDbtr-Eigenschaft ab.
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
     * Legt den Wert der ultmtDbtr-Eigenschaft fest.
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
     * Ruft den Wert der cdtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSCT }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSCT getCdtrAgt() {
        return cdtrAgt;
    }

    /**
     * Legt den Wert der cdtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSCT }
     *     
     */
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentificationSCT value) {
        this.cdtrAgt = value;
    }

    /**
     * Ruft den Wert der cdtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSCT2 }
     *     
     */
    public PartyIdentificationSCT2 getCdtr() {
        return cdtr;
    }

    /**
     * Legt den Wert der cdtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSCT2 }
     *     
     */
    public void setCdtr(PartyIdentificationSCT2 value) {
        this.cdtr = value;
    }

    /**
     * Ruft den Wert der cdtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSCT2 }
     *     
     */
    public CashAccountSCT2 getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Legt den Wert der cdtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSCT2 }
     *     
     */
    public void setCdtrAcct(CashAccountSCT2 value) {
        this.cdtrAcct = value;
    }

    /**
     * Ruft den Wert der ultmtCdtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSCT1 }
     *     
     */
    public PartyIdentificationSCT1 getUltmtCdtr() {
        return ultmtCdtr;
    }

    /**
     * Legt den Wert der ultmtCdtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSCT1 }
     *     
     */
    public void setUltmtCdtr(PartyIdentificationSCT1 value) {
        this.ultmtCdtr = value;
    }

    /**
     * Ruft den Wert der purp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PurposeSCT }
     *     
     */
    public PurposeSCT getPurp() {
        return purp;
    }

    /**
     * Legt den Wert der purp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PurposeSCT }
     *     
     */
    public void setPurp(PurposeSCT value) {
        this.purp = value;
    }

    /**
     * Ruft den Wert der rmtInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceInformationSCTChoice }
     *     
     */
    public RemittanceInformationSCTChoice getRmtInf() {
        return rmtInf;
    }

    /**
     * Legt den Wert der rmtInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceInformationSCTChoice }
     *     
     */
    public void setRmtInf(RemittanceInformationSCTChoice value) {
        this.rmtInf = value;
    }

}
