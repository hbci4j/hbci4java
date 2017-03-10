
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentification8 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentification8">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nm" type="{urn:sepade:xsd:pain.001.001.02}Max70Text" minOccurs="0"/>
 *         &lt;element name="PstlAdr" type="{urn:sepade:xsd:pain.001.001.02}PostalAddress1" minOccurs="0"/>
 *         &lt;element name="Id" type="{urn:sepade:xsd:pain.001.001.02}Party2Choice" minOccurs="0"/>
 *         &lt;element name="CtryOfRes" type="{urn:sepade:xsd:pain.001.001.02}CountryCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification8", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "nm",
    "pstlAdr",
    "id",
    "ctryOfRes"
})
public class PartyIdentification8 {

    @XmlElement(name = "Nm", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String nm;
    @XmlElement(name = "PstlAdr", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected PostalAddress1 pstlAdr;
    @XmlElement(name = "Id", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected Party2Choice id;
    @XmlElement(name = "CtryOfRes", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected String ctryOfRes;

    /**
     * Gets the value of the nm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNm() {
        return nm;
    }

    /**
     * Sets the value of the nm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNm(String value) {
        this.nm = value;
    }

    /**
     * Gets the value of the pstlAdr property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddress1 }
     *     
     */
    public PostalAddress1 getPstlAdr() {
        return pstlAdr;
    }

    /**
     * Sets the value of the pstlAdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddress1 }
     *     
     */
    public void setPstlAdr(PostalAddress1 value) {
        this.pstlAdr = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Party2Choice }
     *     
     */
    public Party2Choice getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party2Choice }
     *     
     */
    public void setId(Party2Choice value) {
        this.id = value;
    }

    /**
     * Gets the value of the ctryOfRes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtryOfRes() {
        return ctryOfRes;
    }

    /**
     * Sets the value of the ctryOfRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtryOfRes(String value) {
        this.ctryOfRes = value;
    }

}
