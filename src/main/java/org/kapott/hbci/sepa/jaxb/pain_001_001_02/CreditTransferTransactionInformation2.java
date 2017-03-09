
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CreditTransferTransactionInformation2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CreditTransferTransactionInformation2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PmtId" type="{urn:sepade:xsd:pain.001.001.02}PaymentIdentification1"/>
 *         &lt;element name="Amt" type="{urn:sepade:xsd:pain.001.001.02}AmountType3"/>
 *         &lt;element name="CdtrAgt" type="{urn:sepade:xsd:pain.001.001.02}FinancialInstitution2"/>
 *         &lt;element name="Cdtr" type="{urn:sepade:xsd:pain.001.001.02}PartyIdentification21"/>
 *         &lt;element name="CdtrAcct" type="{urn:sepade:xsd:pain.001.001.02}CashAccount8"/>
 *         &lt;element name="RmtInf" type="{urn:sepade:xsd:pain.001.001.02}RemittanceInformation3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditTransferTransactionInformation2", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "pmtId",
    "amt",
    "cdtrAgt",
    "cdtr",
    "cdtrAcct",
    "rmtInf"
})
public class CreditTransferTransactionInformation2 {

    @XmlElement(name = "PmtId", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PaymentIdentification1 pmtId;
    @XmlElement(name = "Amt", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected AmountType3 amt;
    @XmlElement(name = "CdtrAgt", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected FinancialInstitution2 cdtrAgt;
    @XmlElement(name = "Cdtr", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PartyIdentification21 cdtr;
    @XmlElement(name = "CdtrAcct", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected CashAccount8 cdtrAcct;
    @XmlElement(name = "RmtInf", namespace = "urn:sepade:xsd:pain.001.001.02")
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
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType3 }
     *     
     */
    public AmountType3 getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType3 }
     *     
     */
    public void setAmt(AmountType3 value) {
        this.amt = value;
    }

    /**
     * Ruft den Wert der cdtrAgt-Eigenschaft ab.
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
     * Legt den Wert der cdtrAgt-Eigenschaft fest.
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
     * Ruft den Wert der cdtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification21 }
     *     
     */
    public PartyIdentification21 getCdtr() {
        return cdtr;
    }

    /**
     * Legt den Wert der cdtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification21 }
     *     
     */
    public void setCdtr(PartyIdentification21 value) {
        this.cdtr = value;
    }

    /**
     * Ruft den Wert der cdtrAcct-Eigenschaft ab.
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
     * Legt den Wert der cdtrAcct-Eigenschaft fest.
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
