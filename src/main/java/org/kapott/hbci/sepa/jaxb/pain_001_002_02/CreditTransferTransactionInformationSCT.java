
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

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
     * Gets the value of the pmtId property.
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
     * Sets the value of the pmtId property.
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
     *     {@link AmountTypeSCT }
     *     
     */
    public AmountTypeSCT getAmt() {
        return amt;
    }

    /**
     * Sets the value of the amt property.
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
     * Gets the value of the cdtrAgt property.
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
     * Sets the value of the cdtrAgt property.
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
     * Gets the value of the cdtr property.
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
     * Sets the value of the cdtr property.
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
     * Gets the value of the cdtrAcct property.
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
     * Sets the value of the cdtrAcct property.
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
     * Gets the value of the ultmtCdtr property.
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
     * Sets the value of the ultmtCdtr property.
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
     * Gets the value of the purp property.
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
     * Sets the value of the purp property.
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
     * Gets the value of the rmtInf property.
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
     * Sets the value of the rmtInf property.
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
