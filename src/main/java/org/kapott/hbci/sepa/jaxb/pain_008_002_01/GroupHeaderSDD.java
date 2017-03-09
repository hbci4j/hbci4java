
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für GroupHeaderSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GroupHeaderSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgId" type="{urn:swift:xsd:$pain.008.002.01}RestrictedIdentification1"/>
 *         &lt;element name="CreDtTm" type="{urn:swift:xsd:$pain.008.002.01}ISODateTime"/>
 *         &lt;element name="BtchBookg" type="{urn:swift:xsd:$pain.008.002.01}BatchBookingIndicator" minOccurs="0"/>
 *         &lt;element name="NbOfTxs" type="{urn:swift:xsd:$pain.008.002.01}Max15NumericText"/>
 *         &lt;element name="CtrlSum" type="{urn:swift:xsd:$pain.008.002.01}DecimalNumber" minOccurs="0"/>
 *         &lt;element name="Grpg" type="{urn:swift:xsd:$pain.008.002.01}Grouping1CodeSDD"/>
 *         &lt;element name="InitgPty" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupHeaderSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "msgId",
    "creDtTm",
    "btchBookg",
    "nbOfTxs",
    "ctrlSum",
    "grpg",
    "initgPty"
})
public class GroupHeaderSDD {

    @XmlElement(name = "MsgId", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected String msgId;
    @XmlElement(name = "CreDtTm", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creDtTm;
    @XmlElement(name = "BtchBookg", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected Boolean btchBookg;
    @XmlElement(name = "NbOfTxs", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected String nbOfTxs;
    @XmlElement(name = "CtrlSum", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected BigDecimal ctrlSum;
    @XmlElement(name = "Grpg", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    @XmlSchemaType(name = "string")
    protected Grouping1CodeSDD grpg;
    @XmlElement(name = "InitgPty", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PartyIdentificationSDD1 initgPty;

    /**
     * Ruft den Wert der msgId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Legt den Wert der msgId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgId(String value) {
        this.msgId = value;
    }

    /**
     * Ruft den Wert der creDtTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreDtTm() {
        return creDtTm;
    }

    /**
     * Legt den Wert der creDtTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreDtTm(XMLGregorianCalendar value) {
        this.creDtTm = value;
    }

    /**
     * Ruft den Wert der btchBookg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBtchBookg() {
        return btchBookg;
    }

    /**
     * Legt den Wert der btchBookg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBtchBookg(Boolean value) {
        this.btchBookg = value;
    }

    /**
     * Ruft den Wert der nbOfTxs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNbOfTxs() {
        return nbOfTxs;
    }

    /**
     * Legt den Wert der nbOfTxs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNbOfTxs(String value) {
        this.nbOfTxs = value;
    }

    /**
     * Ruft den Wert der ctrlSum-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCtrlSum() {
        return ctrlSum;
    }

    /**
     * Legt den Wert der ctrlSum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCtrlSum(BigDecimal value) {
        this.ctrlSum = value;
    }

    /**
     * Ruft den Wert der grpg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Grouping1CodeSDD }
     *     
     */
    public Grouping1CodeSDD getGrpg() {
        return grpg;
    }

    /**
     * Legt den Wert der grpg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Grouping1CodeSDD }
     *     
     */
    public void setGrpg(Grouping1CodeSDD value) {
        this.grpg = value;
    }

    /**
     * Ruft den Wert der initgPty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSDD1 }
     *     
     */
    public PartyIdentificationSDD1 getInitgPty() {
        return initgPty;
    }

    /**
     * Legt den Wert der initgPty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD1 }
     *     
     */
    public void setInitgPty(PartyIdentificationSDD1 value) {
        this.initgPty = value;
    }

}
