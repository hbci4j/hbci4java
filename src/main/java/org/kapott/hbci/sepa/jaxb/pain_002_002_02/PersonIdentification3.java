
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PersonIdentification3 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PersonIdentification3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="DrvrsLicNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="CstmrNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="SclSctyNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="AlnRegnNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="PsptNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="TaxIdNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="IdntyCardNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="MplyrIdNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="DtAndPlcOfBirth" type="{urn:swift:xsd:$pain.002.002.02}DateAndPlaceOfBirth"/>
 *           &lt;element name="OthrId" type="{urn:swift:xsd:$pain.002.002.02}GenericIdentification4"/>
 *         &lt;/choice>
 *         &lt;element name="Issr" type="{urn:swift:xsd:$pain.002.002.02}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonIdentification3", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "drvrsLicNb",
    "cstmrNb",
    "sclSctyNb",
    "alnRegnNb",
    "psptNb",
    "taxIdNb",
    "idntyCardNb",
    "mplyrIdNb",
    "dtAndPlcOfBirth",
    "othrId",
    "issr"
})
public class PersonIdentification3 {

    @XmlElement(name = "DrvrsLicNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String drvrsLicNb;
    @XmlElement(name = "CstmrNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String cstmrNb;
    @XmlElement(name = "SclSctyNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String sclSctyNb;
    @XmlElement(name = "AlnRegnNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String alnRegnNb;
    @XmlElement(name = "PsptNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String psptNb;
    @XmlElement(name = "TaxIdNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String taxIdNb;
    @XmlElement(name = "IdntyCardNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String idntyCardNb;
    @XmlElement(name = "MplyrIdNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String mplyrIdNb;
    @XmlElement(name = "DtAndPlcOfBirth", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected DateAndPlaceOfBirth dtAndPlcOfBirth;
    @XmlElement(name = "OthrId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected GenericIdentification4 othrId;
    @XmlElement(name = "Issr", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String issr;

    /**
     * Ruft den Wert der drvrsLicNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrvrsLicNb() {
        return drvrsLicNb;
    }

    /**
     * Legt den Wert der drvrsLicNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrvrsLicNb(String value) {
        this.drvrsLicNb = value;
    }

    /**
     * Ruft den Wert der cstmrNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCstmrNb() {
        return cstmrNb;
    }

    /**
     * Legt den Wert der cstmrNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCstmrNb(String value) {
        this.cstmrNb = value;
    }

    /**
     * Ruft den Wert der sclSctyNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSclSctyNb() {
        return sclSctyNb;
    }

    /**
     * Legt den Wert der sclSctyNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSclSctyNb(String value) {
        this.sclSctyNb = value;
    }

    /**
     * Ruft den Wert der alnRegnNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlnRegnNb() {
        return alnRegnNb;
    }

    /**
     * Legt den Wert der alnRegnNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlnRegnNb(String value) {
        this.alnRegnNb = value;
    }

    /**
     * Ruft den Wert der psptNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPsptNb() {
        return psptNb;
    }

    /**
     * Legt den Wert der psptNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPsptNb(String value) {
        this.psptNb = value;
    }

    /**
     * Ruft den Wert der taxIdNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxIdNb() {
        return taxIdNb;
    }

    /**
     * Legt den Wert der taxIdNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxIdNb(String value) {
        this.taxIdNb = value;
    }

    /**
     * Ruft den Wert der idntyCardNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdntyCardNb() {
        return idntyCardNb;
    }

    /**
     * Legt den Wert der idntyCardNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdntyCardNb(String value) {
        this.idntyCardNb = value;
    }

    /**
     * Ruft den Wert der mplyrIdNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMplyrIdNb() {
        return mplyrIdNb;
    }

    /**
     * Legt den Wert der mplyrIdNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMplyrIdNb(String value) {
        this.mplyrIdNb = value;
    }

    /**
     * Ruft den Wert der dtAndPlcOfBirth-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateAndPlaceOfBirth }
     *     
     */
    public DateAndPlaceOfBirth getDtAndPlcOfBirth() {
        return dtAndPlcOfBirth;
    }

    /**
     * Legt den Wert der dtAndPlcOfBirth-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateAndPlaceOfBirth }
     *     
     */
    public void setDtAndPlcOfBirth(DateAndPlaceOfBirth value) {
        this.dtAndPlcOfBirth = value;
    }

    /**
     * Ruft den Wert der othrId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GenericIdentification4 }
     *     
     */
    public GenericIdentification4 getOthrId() {
        return othrId;
    }

    /**
     * Legt den Wert der othrId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericIdentification4 }
     *     
     */
    public void setOthrId(GenericIdentification4 value) {
        this.othrId = value;
    }

    /**
     * Ruft den Wert der issr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssr() {
        return issr;
    }

    /**
     * Legt den Wert der issr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssr(String value) {
        this.issr = value;
    }

}
