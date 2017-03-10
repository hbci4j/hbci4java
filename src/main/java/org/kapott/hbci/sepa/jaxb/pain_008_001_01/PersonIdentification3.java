
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonIdentification3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonIdentification3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="DrvrsLicNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="CstmrNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="SclSctyNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="AlnRegnNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="PsptNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="TaxIdNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="IdntyCardNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="MplyrIdNb" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *           &lt;element name="DtAndPlcOfBirth" type="{urn:sepade:xsd:pain.008.001.01}DateAndPlaceOfBirth"/>
 *           &lt;element name="OthrId" type="{urn:sepade:xsd:pain.008.001.01}GenericIdentification4"/>
 *         &lt;/choice>
 *         &lt;element name="Issr" type="{urn:sepade:xsd:pain.008.001.01}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonIdentification3", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
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

    @XmlElement(name = "DrvrsLicNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String drvrsLicNb;
    @XmlElement(name = "CstmrNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String cstmrNb;
    @XmlElement(name = "SclSctyNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String sclSctyNb;
    @XmlElement(name = "AlnRegnNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String alnRegnNb;
    @XmlElement(name = "PsptNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String psptNb;
    @XmlElement(name = "TaxIdNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String taxIdNb;
    @XmlElement(name = "IdntyCardNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String idntyCardNb;
    @XmlElement(name = "MplyrIdNb", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String mplyrIdNb;
    @XmlElement(name = "DtAndPlcOfBirth", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected DateAndPlaceOfBirth dtAndPlcOfBirth;
    @XmlElement(name = "OthrId", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected GenericIdentification4 othrId;
    @XmlElement(name = "Issr", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String issr;

    /**
     * Gets the value of the drvrsLicNb property.
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
     * Sets the value of the drvrsLicNb property.
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
     * Gets the value of the cstmrNb property.
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
     * Sets the value of the cstmrNb property.
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
     * Gets the value of the sclSctyNb property.
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
     * Sets the value of the sclSctyNb property.
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
     * Gets the value of the alnRegnNb property.
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
     * Sets the value of the alnRegnNb property.
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
     * Gets the value of the psptNb property.
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
     * Sets the value of the psptNb property.
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
     * Gets the value of the taxIdNb property.
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
     * Sets the value of the taxIdNb property.
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
     * Gets the value of the idntyCardNb property.
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
     * Sets the value of the idntyCardNb property.
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
     * Gets the value of the mplyrIdNb property.
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
     * Sets the value of the mplyrIdNb property.
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
     * Gets the value of the dtAndPlcOfBirth property.
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
     * Sets the value of the dtAndPlcOfBirth property.
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
     * Gets the value of the othrId property.
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
     * Sets the value of the othrId property.
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
     * Gets the value of the issr property.
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
     * Sets the value of the issr property.
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
