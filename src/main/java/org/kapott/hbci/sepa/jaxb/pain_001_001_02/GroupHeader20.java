
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for GroupHeader20 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GroupHeader20">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgId" type="{urn:sepade:xsd:pain.001.001.02}Max35Text"/>
 *         &lt;element name="CreDtTm" type="{urn:sepade:xsd:pain.001.001.02}ISODateTime"/>
 *         &lt;element name="NbOfTxs" type="{urn:sepade:xsd:pain.001.001.02}Max15NumericText"/>
 *         &lt;element name="CtrlSum" type="{urn:sepade:xsd:pain.001.001.02}DecimalNumber" minOccurs="0"/>
 *         &lt;element name="Grpg" type="{urn:sepade:xsd:pain.001.001.02}Grouping2Code"/>
 *         &lt;element name="InitgPty" type="{urn:sepade:xsd:pain.001.001.02}PartyIdentification20"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupHeader20", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "msgId",
    "creDtTm",
    "nbOfTxs",
    "ctrlSum",
    "grpg",
    "initgPty"
})
public class GroupHeader20 {

    @XmlElement(name = "MsgId", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected String msgId;
    @XmlElement(name = "CreDtTm", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected XMLGregorianCalendar creDtTm;
    @XmlElement(name = "NbOfTxs", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected String nbOfTxs;
    @XmlElement(name = "CtrlSum", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected BigDecimal ctrlSum;
    @XmlElement(name = "Grpg", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected Grouping2Code grpg;
    @XmlElement(name = "InitgPty", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected PartyIdentification20 initgPty;

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
     *     {@link Grouping2Code }
     *     
     */
    public Grouping2Code getGrpg() {
        return grpg;
    }

    /**
     * Sets the value of the grpg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Grouping2Code }
     *     
     */
    public void setGrpg(Grouping2Code value) {
        this.grpg = value;
    }

    /**
     * Gets the value of the initgPty property.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification20 }
     *     
     */
    public PartyIdentification20 getInitgPty() {
        return initgPty;
    }

    /**
     * Sets the value of the initgPty property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification20 }
     *     
     */
    public void setInitgPty(PartyIdentification20 value) {
        this.initgPty = value;
    }

}
