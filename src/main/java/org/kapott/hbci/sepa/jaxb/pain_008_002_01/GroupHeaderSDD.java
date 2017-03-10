
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for GroupHeaderSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
    protected XMLGregorianCalendar creDtTm;
    @XmlElement(name = "BtchBookg", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected Boolean btchBookg;
    @XmlElement(name = "NbOfTxs", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected String nbOfTxs;
    @XmlElement(name = "CtrlSum", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected BigDecimal ctrlSum;
    @XmlElement(name = "Grpg", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected Grouping1CodeSDD grpg;
    @XmlElement(name = "InitgPty", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected PartyIdentificationSDD1 initgPty;

    /**
     * Gets the value of the msgId property.
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
     * Sets the value of the msgId property.
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
     * Gets the value of the creDtTm property.
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
     * Sets the value of the creDtTm property.
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
     * Gets the value of the btchBookg property.
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
     * Sets the value of the btchBookg property.
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
     * Gets the value of the nbOfTxs property.
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
     * Sets the value of the nbOfTxs property.
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
     * Gets the value of the ctrlSum property.
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
     * Sets the value of the ctrlSum property.
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
     * Gets the value of the grpg property.
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
     * Sets the value of the grpg property.
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
     * Gets the value of the initgPty property.
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
     * Sets the value of the initgPty property.
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
