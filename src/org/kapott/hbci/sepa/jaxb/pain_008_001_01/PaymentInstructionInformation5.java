
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PaymentInstructionInformation5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentInstructionInformation5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtInfId" type="{urn:sepade:xsd:pain.008.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="PmtMtd" type="{urn:sepade:xsd:pain.008.001.01}PaymentMethod2Code"/>
 *         &lt;element name="PmtTpInf" type="{urn:sepade:xsd:pain.008.001.01}PaymentTypeInformation8"/>
 *         &lt;element name="ReqdColltnDt" type="{urn:sepade:xsd:pain.008.001.01}ISODate"/>
 *         &lt;element name="Cdtr" type="{urn:sepade:xsd:pain.008.001.01}PartyIdentification22"/>
 *         &lt;element name="CdtrAcct" type="{urn:sepade:xsd:pain.008.001.01}CashAccount8"/>
 *         &lt;element name="CdtrAgt" type="{urn:sepade:xsd:pain.008.001.01}FinancialInstitution2"/>
 *         &lt;element name="ChrgBr" type="{urn:sepade:xsd:pain.008.001.01}ChargeBearerType2Code"/>
 *         &lt;element name="DrctDbtTxInf" type="{urn:sepade:xsd:pain.008.001.01}DirectDebitTransactionInformation2" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentInstructionInformation5", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "pmtInfId",
    "pmtMtd",
    "pmtTpInf",
    "reqdColltnDt",
    "cdtr",
    "cdtrAcct",
    "cdtrAgt",
    "chrgBr",
    "drctDbtTxInf"
})
public class PaymentInstructionInformation5 {

    @XmlElement(name = "PmtInfId", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String pmtInfId;
    @XmlElement(name = "PmtMtd", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PaymentMethod2Code pmtMtd;
    @XmlElement(name = "PmtTpInf", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PaymentTypeInformation8 pmtTpInf;
    @XmlElement(name = "ReqdColltnDt", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected XMLGregorianCalendar reqdColltnDt;
    @XmlElement(name = "Cdtr", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected PartyIdentification22 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected CashAccount8 cdtrAcct;
    @XmlElement(name = "CdtrAgt", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected FinancialInstitution2 cdtrAgt;
    @XmlElement(name = "ChrgBr", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected ChargeBearerType2Code chrgBr;
    @XmlElement(name = "DrctDbtTxInf", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected List<DirectDebitTransactionInformation2> drctDbtTxInf;

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
     *     {@link PaymentTypeInformation8 }
     *     
     */
    public PaymentTypeInformation8 getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Sets the value of the pmtTpInf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformation8 }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformation8 value) {
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
     *     {@link PartyIdentification22 }
     *     
     */
    public PartyIdentification22 getCdtr() {
        return cdtr;
    }

    /**
     * Sets the value of the cdtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification22 }
     *     
     */
    public void setCdtr(PartyIdentification22 value) {
        this.cdtr = value;
    }

    /**
     * Gets the value of the cdtrAcct property.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount8 }
     *     
     */
    public CashAccount8 getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Sets the value of the cdtrAcct property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount8 }
     *     
     */
    public void setCdtrAcct(CashAccount8 value) {
        this.cdtrAcct = value;
    }

    /**
     * Gets the value of the cdtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitution2 }
     *     
     */
    public FinancialInstitution2 getCdtrAgt() {
        return cdtrAgt;
    }

    /**
     * Sets the value of the cdtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitution2 }
     *     
     */
    public void setCdtrAgt(FinancialInstitution2 value) {
        this.cdtrAgt = value;
    }

    /**
     * Gets the value of the chrgBr property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBearerType2Code }
     *     
     */
    public ChargeBearerType2Code getChrgBr() {
        return chrgBr;
    }

    /**
     * Sets the value of the chrgBr property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBearerType2Code }
     *     
     */
    public void setChrgBr(ChargeBearerType2Code value) {
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
     * {@link DirectDebitTransactionInformation2 }
     * 
     * 
     */
    public List<DirectDebitTransactionInformation2> getDrctDbtTxInf() {
        if (drctDbtTxInf == null) {
            drctDbtTxInf = new ArrayList<DirectDebitTransactionInformation2>();
        }
        return this.drctDbtTxInf;
    }

}
