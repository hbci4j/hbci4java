
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OriginalPaymentInformationSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OriginalPaymentInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgnlPmtInfId" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}RestrictedIdentificationSEPA1"/>
 *         &lt;element name="OrgnlNbOfTxs" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}Max15NumericText" minOccurs="0"/>
 *         &lt;element name="OrgnlCtrlSum" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}DecimalNumber" minOccurs="0"/>
 *         &lt;element name="PmtInfSts" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}TransactionGroupStatusCodeSEPA" minOccurs="0"/>
 *         &lt;element name="StsRsnInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}StatusReasonInformationSEPA" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TxInfAndSts" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}PaymentTransactionInformationSEPA" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OriginalPaymentInformationSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "orgnlPmtInfId",
    "orgnlNbOfTxs",
    "orgnlCtrlSum",
    "pmtInfSts",
    "stsRsnInf",
    "txInfAndSts"
})
public class OriginalPaymentInformationSEPA {

    @XmlElement(name = "OrgnlPmtInfId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", required = true)
    protected String orgnlPmtInfId;
    @XmlElement(name = "OrgnlNbOfTxs", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected String orgnlNbOfTxs;
    @XmlElement(name = "OrgnlCtrlSum", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected BigDecimal orgnlCtrlSum;
    @XmlElement(name = "PmtInfSts", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected TransactionGroupStatusCodeSEPA pmtInfSts;
    @XmlElement(name = "StsRsnInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected List<StatusReasonInformationSEPA> stsRsnInf;
    @XmlElement(name = "TxInfAndSts", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected List<PaymentTransactionInformationSEPA> txInfAndSts;

    /**
     * Gets the value of the orgnlPmtInfId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlPmtInfId() {
        return orgnlPmtInfId;
    }

    /**
     * Sets the value of the orgnlPmtInfId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlPmtInfId(String value) {
        this.orgnlPmtInfId = value;
    }

    /**
     * Gets the value of the orgnlNbOfTxs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlNbOfTxs() {
        return orgnlNbOfTxs;
    }

    /**
     * Sets the value of the orgnlNbOfTxs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlNbOfTxs(String value) {
        this.orgnlNbOfTxs = value;
    }

    /**
     * Gets the value of the orgnlCtrlSum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOrgnlCtrlSum() {
        return orgnlCtrlSum;
    }

    /**
     * Sets the value of the orgnlCtrlSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOrgnlCtrlSum(BigDecimal value) {
        this.orgnlCtrlSum = value;
    }

    /**
     * Gets the value of the pmtInfSts property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionGroupStatusCodeSEPA }
     *     
     */
    public TransactionGroupStatusCodeSEPA getPmtInfSts() {
        return pmtInfSts;
    }

    /**
     * Sets the value of the pmtInfSts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionGroupStatusCodeSEPA }
     *     
     */
    public void setPmtInfSts(TransactionGroupStatusCodeSEPA value) {
        this.pmtInfSts = value;
    }

    /**
     * Gets the value of the stsRsnInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stsRsnInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStsRsnInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StatusReasonInformationSEPA }
     * 
     * 
     */
    public List<StatusReasonInformationSEPA> getStsRsnInf() {
        if (stsRsnInf == null) {
            stsRsnInf = new ArrayList<StatusReasonInformationSEPA>();
        }
        return this.stsRsnInf;
    }

    /**
     * Gets the value of the txInfAndSts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the txInfAndSts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTxInfAndSts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentTransactionInformationSEPA }
     * 
     * 
     */
    public List<PaymentTransactionInformationSEPA> getTxInfAndSts() {
        if (txInfAndSts == null) {
            txInfAndSts = new ArrayList<PaymentTransactionInformationSEPA>();
        }
        return this.txInfAndSts;
    }

}
