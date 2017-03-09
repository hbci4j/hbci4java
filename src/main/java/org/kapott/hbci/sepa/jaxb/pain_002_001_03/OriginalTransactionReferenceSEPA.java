
package org.kapott.hbci.sepa.jaxb.pain_002_001_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for OriginalTransactionReferenceSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OriginalTransactionReferenceSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}AmountTypeSEPA" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="ReqdColltnDt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}ISODate" minOccurs="0"/>
 *           &lt;element name="ReqdExctnDt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}ISODate" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="CdtrSchmeId" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PartyIdentificationSEPA3" minOccurs="0"/>
 *         &lt;element name="PmtTpInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PaymentTypeInformationSEPA" minOccurs="0"/>
 *         &lt;element name="PmtMtd" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PaymentMethodSEPACode" minOccurs="0"/>
 *         &lt;element name="MndtRltdInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}MandateRelatedInformationSEPA" minOccurs="0"/>
 *         &lt;element name="RmtInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}RemittanceInformationSEPA2Choice" minOccurs="0"/>
 *         &lt;element name="UltmtDbtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PartyIdentificationSEPA1" minOccurs="0"/>
 *         &lt;element name="Dbtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PartyIdentificationSEPA2" minOccurs="0"/>
 *         &lt;element name="DbtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}CashAccountSEPA1" minOccurs="0"/>
 *         &lt;element name="DbtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}BranchAndFinancialInstitutionIdentificationSEPA3" minOccurs="0"/>
 *         &lt;element name="CdtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}BranchAndFinancialInstitutionIdentificationSEPA3" minOccurs="0"/>
 *         &lt;element name="Cdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PartyIdentificationSEPA2" minOccurs="0"/>
 *         &lt;element name="CdtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}CashAccountSEPA1" minOccurs="0"/>
 *         &lt;element name="UltmtCdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}PartyIdentificationSEPA1" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OriginalTransactionReferenceSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03", propOrder = {
    "amt",
    "reqdColltnDt",
    "reqdExctnDt",
    "cdtrSchmeId",
    "pmtTpInf",
    "pmtMtd",
    "mndtRltdInf",
    "rmtInf",
    "ultmtDbtr",
    "dbtr",
    "dbtrAcct",
    "dbtrAgt",
    "cdtrAgt",
    "cdtr",
    "cdtrAcct",
    "ultmtCdtr"
})
public class OriginalTransactionReferenceSEPA {

    @XmlElement(name = "Amt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected AmountTypeSEPA amt;
    @XmlElement(name = "ReqdColltnDt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected XMLGregorianCalendar reqdColltnDt;
    @XmlElement(name = "ReqdExctnDt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected XMLGregorianCalendar reqdExctnDt;
    @XmlElement(name = "CdtrSchmeId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PartyIdentificationSEPA3 cdtrSchmeId;
    @XmlElement(name = "PmtTpInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PaymentTypeInformationSEPA pmtTpInf;
    @XmlElement(name = "PmtMtd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PaymentMethodSEPACode pmtMtd;
    @XmlElement(name = "MndtRltdInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected MandateRelatedInformationSEPA mndtRltdInf;
    @XmlElement(name = "RmtInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected RemittanceInformationSEPA2Choice rmtInf;
    @XmlElement(name = "UltmtDbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PartyIdentificationSEPA1 ultmtDbtr;
    @XmlElement(name = "Dbtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PartyIdentificationSEPA2 dbtr;
    @XmlElement(name = "DbtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected CashAccountSEPA1 dbtrAcct;
    @XmlElement(name = "DbtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected BranchAndFinancialInstitutionIdentificationSEPA3 dbtrAgt;
    @XmlElement(name = "CdtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected BranchAndFinancialInstitutionIdentificationSEPA3 cdtrAgt;
    @XmlElement(name = "Cdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PartyIdentificationSEPA2 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected CashAccountSEPA1 cdtrAcct;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03")
    protected PartyIdentificationSEPA1 ultmtCdtr;

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
     * Gets the value of the pmtTpInf property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypeInformationSEPA }
     *     
     */
    public PaymentTypeInformationSEPA getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Sets the value of the pmtTpInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformationSEPA }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformationSEPA value) {
        this.pmtTpInf = value;
    }

    /**
     * Gets the value of the pmtMtd property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodSEPACode }
     *     
     */
    public PaymentMethodSEPACode getPmtMtd() {
        return pmtMtd;
    }

    /**
     * Sets the value of the pmtMtd property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodSEPACode }
     *     
     */
    public void setPmtMtd(PaymentMethodSEPACode value) {
        this.pmtMtd = value;
    }

    /**
     * Gets the value of the mndtRltdInf property.
     * 
     * @return
     *     possible object is
     *     {@link MandateRelatedInformationSEPA }
     *     
     */
    public MandateRelatedInformationSEPA getMndtRltdInf() {
        return mndtRltdInf;
    }

    /**
     * Sets the value of the mndtRltdInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link MandateRelatedInformationSEPA }
     *     
     */
    public void setMndtRltdInf(MandateRelatedInformationSEPA value) {
        this.mndtRltdInf = value;
    }

    /**
     * Gets the value of the rmtInf property.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceInformationSEPA2Choice }
     *     
     */
    public RemittanceInformationSEPA2Choice getRmtInf() {
        return rmtInf;
    }

    /**
     * Sets the value of the rmtInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceInformationSEPA2Choice }
     *     
     */
    public void setRmtInf(RemittanceInformationSEPA2Choice value) {
        this.rmtInf = value;
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
     * Gets the value of the dbtr property.
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
     * Sets the value of the dbtr property.
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
     * Gets the value of the dbtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSEPA1 }
     *     
     */
    public CashAccountSEPA1 getDbtrAcct() {
        return dbtrAcct;
    }

    /**
     * Sets the value of the dbtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSEPA1 }
     *     
     */
    public void setDbtrAcct(CashAccountSEPA1 value) {
        this.dbtrAcct = value;
    }

    /**
     * Gets the value of the dbtrAgt property.
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
     * Sets the value of the dbtrAgt property.
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
     * Gets the value of the cdtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA3 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSEPA3 getCdtrAgt() {
        return cdtrAgt;
    }

    /**
     * Sets the value of the cdtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA3 }
     *     
     */
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentificationSEPA3 value) {
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

}
