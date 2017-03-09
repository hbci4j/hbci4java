
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PostalAddress1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PostalAddress1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AdrTp" type="{urn:sepade:xsd:pain.001.001.02}AddressType2Code" minOccurs="0"/>
 *         &lt;element name="AdrLine" type="{urn:sepade:xsd:pain.001.001.02}Max70Text" maxOccurs="5" minOccurs="0"/>
 *         &lt;element name="StrtNm" type="{urn:sepade:xsd:pain.001.001.02}Max70Text" minOccurs="0"/>
 *         &lt;element name="BldgNb" type="{urn:sepade:xsd:pain.001.001.02}Max16Text" minOccurs="0"/>
 *         &lt;element name="PstCd" type="{urn:sepade:xsd:pain.001.001.02}Max16Text" minOccurs="0"/>
 *         &lt;element name="TwnNm" type="{urn:sepade:xsd:pain.001.001.02}Max35Text" minOccurs="0"/>
 *         &lt;element name="CtrySubDvsn" type="{urn:sepade:xsd:pain.001.001.02}Max35Text" minOccurs="0"/>
 *         &lt;element name="Ctry" type="{urn:sepade:xsd:pain.001.001.02}CountryCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddress1", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "adrTp",
    "adrLine",
    "strtNm",
    "bldgNb",
    "pstCd",
    "twnNm",
    "ctrySubDvsn",
    "ctry"
})
public class PostalAddress1 {

    @XmlElement(name = "AdrTp", namespace = "urn:sepade:xsd:pain.001.001.02")
    @XmlSchemaType(name = "string")
    protected AddressType2Code adrTp;
    @XmlElement(name = "AdrLine", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected List<String> adrLine;
    @XmlElement(name = "StrtNm", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String strtNm;
    @XmlElement(name = "BldgNb", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String bldgNb;
    @XmlElement(name = "PstCd", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String pstCd;
    @XmlElement(name = "TwnNm", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String twnNm;
    @XmlElement(name = "CtrySubDvsn", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String ctrySubDvsn;
    @XmlElement(name = "Ctry", namespace = "urn:sepade:xsd:pain.001.001.02", required = true)
    protected String ctry;

    /**
     * Ruft den Wert der adrTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AddressType2Code }
     *     
     */
    public AddressType2Code getAdrTp() {
        return adrTp;
    }

    /**
     * Legt den Wert der adrTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType2Code }
     *     
     */
    public void setAdrTp(AddressType2Code value) {
        this.adrTp = value;
    }

    /**
     * Gets the value of the adrLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adrLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdrLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAdrLine() {
        if (adrLine == null) {
            adrLine = new ArrayList<String>();
        }
        return this.adrLine;
    }

    /**
     * Ruft den Wert der strtNm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrtNm() {
        return strtNm;
    }

    /**
     * Legt den Wert der strtNm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrtNm(String value) {
        this.strtNm = value;
    }

    /**
     * Ruft den Wert der bldgNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBldgNb() {
        return bldgNb;
    }

    /**
     * Legt den Wert der bldgNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBldgNb(String value) {
        this.bldgNb = value;
    }

    /**
     * Ruft den Wert der pstCd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPstCd() {
        return pstCd;
    }

    /**
     * Legt den Wert der pstCd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPstCd(String value) {
        this.pstCd = value;
    }

    /**
     * Ruft den Wert der twnNm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwnNm() {
        return twnNm;
    }

    /**
     * Legt den Wert der twnNm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwnNm(String value) {
        this.twnNm = value;
    }

    /**
     * Ruft den Wert der ctrySubDvsn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtrySubDvsn() {
        return ctrySubDvsn;
    }

    /**
     * Legt den Wert der ctrySubDvsn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtrySubDvsn(String value) {
        this.ctrySubDvsn = value;
    }

    /**
     * Ruft den Wert der ctry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtry() {
        return ctry;
    }

    /**
     * Legt den Wert der ctry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtry(String value) {
        this.ctry = value;
    }

}
