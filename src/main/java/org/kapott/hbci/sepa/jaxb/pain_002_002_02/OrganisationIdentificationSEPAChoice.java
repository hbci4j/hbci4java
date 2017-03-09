
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für OrganisationIdentificationSEPAChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrganisationIdentificationSEPAChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="BIC" type="{urn:swift:xsd:$pain.002.002.02}BICIdentifier"/>
 *           &lt;element name="IBEI" type="{urn:swift:xsd:$pain.002.002.02}IBEIIdentifier"/>
 *           &lt;element name="BEI" type="{urn:swift:xsd:$pain.002.002.02}BEIIdentifier"/>
 *           &lt;element name="EANGLN" type="{urn:swift:xsd:$pain.002.002.02}EANGLNIdentifier"/>
 *           &lt;element name="USCHU" type="{urn:swift:xsd:$pain.002.002.02}CHIPSUniversalIdentifier"/>
 *           &lt;element name="DUNS" type="{urn:swift:xsd:$pain.002.002.02}DunsIdentifier"/>
 *           &lt;element name="BkPtyId" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="TaxIdNb" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *           &lt;element name="PrtryId" type="{urn:swift:xsd:$pain.002.002.02}GenericIdentification3"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationIdentificationSEPAChoice", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "bic",
    "ibei",
    "bei",
    "eangln",
    "uschu",
    "duns",
    "bkPtyId",
    "taxIdNb",
    "prtryId"
})
public class OrganisationIdentificationSEPAChoice {

    @XmlElement(name = "BIC", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String bic;
    @XmlElement(name = "IBEI", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String ibei;
    @XmlElement(name = "BEI", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String bei;
    @XmlElement(name = "EANGLN", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String eangln;
    @XmlElement(name = "USCHU", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String uschu;
    @XmlElement(name = "DUNS", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String duns;
    @XmlElement(name = "BkPtyId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String bkPtyId;
    @XmlElement(name = "TaxIdNb", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String taxIdNb;
    @XmlElement(name = "PrtryId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected GenericIdentification3 prtryId;

    /**
     * Ruft den Wert der bic-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIC() {
        return bic;
    }

    /**
     * Legt den Wert der bic-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIC(String value) {
        this.bic = value;
    }

    /**
     * Ruft den Wert der ibei-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBEI() {
        return ibei;
    }

    /**
     * Legt den Wert der ibei-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBEI(String value) {
        this.ibei = value;
    }

    /**
     * Ruft den Wert der bei-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBEI() {
        return bei;
    }

    /**
     * Legt den Wert der bei-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBEI(String value) {
        this.bei = value;
    }

    /**
     * Ruft den Wert der eangln-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEANGLN() {
        return eangln;
    }

    /**
     * Legt den Wert der eangln-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEANGLN(String value) {
        this.eangln = value;
    }

    /**
     * Ruft den Wert der uschu-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSCHU() {
        return uschu;
    }

    /**
     * Legt den Wert der uschu-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSCHU(String value) {
        this.uschu = value;
    }

    /**
     * Ruft den Wert der duns-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDUNS() {
        return duns;
    }

    /**
     * Legt den Wert der duns-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDUNS(String value) {
        this.duns = value;
    }

    /**
     * Ruft den Wert der bkPtyId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBkPtyId() {
        return bkPtyId;
    }

    /**
     * Legt den Wert der bkPtyId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBkPtyId(String value) {
        this.bkPtyId = value;
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
     * Ruft den Wert der prtryId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GenericIdentification3 }
     *     
     */
    public GenericIdentification3 getPrtryId() {
        return prtryId;
    }

    /**
     * Legt den Wert der prtryId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericIdentification3 }
     *     
     */
    public void setPrtryId(GenericIdentification3 value) {
        this.prtryId = value;
    }

}
