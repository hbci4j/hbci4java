
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentTransactionInformationSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentTransactionInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StsId" type="{urn:swift:xsd:$pain.002.002.02}RestrictedIdentification1" minOccurs="0"/>
 *         &lt;element name="OrgnlPmtInfId" type="{urn:swift:xsd:$pain.002.002.02}RestrictedIdentification1" minOccurs="0"/>
 *         &lt;element name="OrgnlInstrId" type="{urn:swift:xsd:$pain.002.002.02}RestrictedIdentification1" minOccurs="0"/>
 *         &lt;element name="OrgnlEndToEndId" type="{urn:swift:xsd:$pain.002.002.02}RestrictedIdentification2" minOccurs="0"/>
 *         &lt;element name="TxSts" type="{urn:swift:xsd:$pain.002.002.02}TransactionIndividualStatus1CodeSEPA" minOccurs="0"/>
 *         &lt;element name="StsRsnInf" type="{urn:swift:xsd:$pain.002.002.02}StatusReasonInformationSEPA" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OrgnlTxRef" type="{urn:swift:xsd:$pain.002.002.02}OriginalTransactionReferenceSEPA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTransactionInformationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "stsId",
    "orgnlPmtInfId",
    "orgnlInstrId",
    "orgnlEndToEndId",
    "txSts",
    "stsRsnInf",
    "orgnlTxRef"
})
public class PaymentTransactionInformationSEPA {

    @XmlElement(name = "StsId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String stsId;
    @XmlElement(name = "OrgnlPmtInfId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String orgnlPmtInfId;
    @XmlElement(name = "OrgnlInstrId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String orgnlInstrId;
    @XmlElement(name = "OrgnlEndToEndId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String orgnlEndToEndId;
    @XmlElement(name = "TxSts", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected TransactionIndividualStatus1CodeSEPA txSts;
    @XmlElement(name = "StsRsnInf", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected List<StatusReasonInformationSEPA> stsRsnInf;
    @XmlElement(name = "OrgnlTxRef", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected OriginalTransactionReferenceSEPA orgnlTxRef;

    /**
     * Gets the value of the stsId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStsId() {
        return stsId;
    }

    /**
     * Sets the value of the stsId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStsId(String value) {
        this.stsId = value;
    }

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
     * Gets the value of the orgnlInstrId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlInstrId() {
        return orgnlInstrId;
    }

    /**
     * Sets the value of the orgnlInstrId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlInstrId(String value) {
        this.orgnlInstrId = value;
    }

    /**
     * Gets the value of the orgnlEndToEndId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlEndToEndId() {
        return orgnlEndToEndId;
    }

    /**
     * Sets the value of the orgnlEndToEndId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlEndToEndId(String value) {
        this.orgnlEndToEndId = value;
    }

    /**
     * Gets the value of the txSts property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionIndividualStatus1CodeSEPA }
     *     
     */
    public TransactionIndividualStatus1CodeSEPA getTxSts() {
        return txSts;
    }

    /**
     * Sets the value of the txSts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionIndividualStatus1CodeSEPA }
     *     
     */
    public void setTxSts(TransactionIndividualStatus1CodeSEPA value) {
        this.txSts = value;
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
     * Gets the value of the orgnlTxRef property.
     * 
     * @return
     *     possible object is
     *     {@link OriginalTransactionReferenceSEPA }
     *     
     */
    public OriginalTransactionReferenceSEPA getOrgnlTxRef() {
        return orgnlTxRef;
    }

    /**
     * Sets the value of the orgnlTxRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginalTransactionReferenceSEPA }
     *     
     */
    public void setOrgnlTxRef(OriginalTransactionReferenceSEPA value) {
        this.orgnlTxRef = value;
    }

}
