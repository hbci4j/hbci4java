
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

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
 *         &lt;element name="PmtInfId" type="{urn:swift:xsd:$pain.008.002.01}RestrictedIdentification1" minOccurs="0"/>
 *         &lt;element name="PmtMtd" type="{urn:swift:xsd:$pain.008.002.01}PaymentMethod2Code"/>
 *         &lt;element name="PmtTpInf" type="{urn:swift:xsd:$pain.008.002.01}PaymentTypeInformationSDD"/>
 *         &lt;element name="ReqdColltnDt" type="{urn:swift:xsd:$pain.008.002.01}ISODate"/>
 *         &lt;element name="Cdtr" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD2"/>
 *         &lt;element name="CdtrAcct" type="{urn:swift:xsd:$pain.008.002.01}CashAccountSDD1"/>
 *         &lt;element name="CdtrAgt" type="{urn:swift:xsd:$pain.008.002.01}BranchAndFinancialInstitutionIdentificationSDD1"/>
 *         &lt;element name="UltmtCdtr" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD1" minOccurs="0"/>
 *         &lt;element name="ChrgBr" type="{urn:swift:xsd:$pain.008.002.01}ChargeBearerTypeSDDCode" minOccurs="0"/>
 *         &lt;element name="DrctDbtTxInf" type="{urn:swift:xsd:$pain.008.002.01}DirectDebitTransactionInformationSDD" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstructionInformationSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "pmtInfId",
    "pmtMtd",
    "pmtTpInf",
    "reqdColltnDt",
    "cdtr",
    "cdtrAcct",
    "cdtrAgt",
    "ultmtCdtr",
    "chrgBr",
    "drctDbtTxInf"
})
public class PaymentInstructionInformationSDD {

    @XmlElement(name = "PmtInfId", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected String pmtInfId;
    @XmlElement(name = "PmtMtd", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PaymentMethod2Code pmtMtd;
    @XmlElement(name = "PmtTpInf", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PaymentTypeInformationSDD pmtTpInf;
    @XmlElement(name = "ReqdColltnDt", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected XMLGregorianCalendar reqdColltnDt;
    @XmlElement(name = "Cdtr", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PartyIdentificationSDD2 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected CashAccountSDD1 cdtrAcct;
    @XmlElement(name = "CdtrAgt", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected BranchAndFinancialInstitutionIdentificationSDD1 cdtrAgt;
    @XmlElement(name = "UltmtCdtr", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PartyIdentificationSDD1 ultmtCdtr;
    @XmlElement(name = "ChrgBr", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected ChargeBearerTypeSDDCode chrgBr;
    @XmlElement(name = "DrctDbtTxInf", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
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
     *     {@link PartyIdentificationSDD2 }
     *     
     */
    public PartyIdentificationSDD2 getCdtr() {
        return cdtr;
    }

    /**
     * Sets the value of the cdtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD2 }
     *     
     */
    public void setCdtr(PartyIdentificationSDD2 value) {
        this.cdtr = value;
    }

    /**
     * Gets the value of the cdtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSDD1 }
     *     
     */
    public CashAccountSDD1 getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Sets the value of the cdtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSDD1 }
     *     
     */
    public void setCdtrAcct(CashAccountSDD1 value) {
        this.cdtrAcct = value;
    }

    /**
     * Gets the value of the cdtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSDD1 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSDD1 getCdtrAgt() {
        return cdtrAgt;
    }

    /**
     * Sets the value of the cdtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSDD1 }
     *     
     */
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentificationSDD1 value) {
        this.cdtrAgt = value;
    }

    /**
     * Gets the value of the ultmtCdtr property.
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
     * Sets the value of the ultmtCdtr property.
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
     * Gets the value of the chrgBr property.
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
     * Sets the value of the chrgBr property.
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
