
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentification20 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentification20">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nm" type="{urn:sepade:xsd:pain.008.001.01}Max70Text"/>
 *         &lt;element name="PstlAdr" type="{urn:sepade:xsd:pain.008.001.01}PostalAddress5" minOccurs="0"/>
 *         &lt;element name="Id" type="{urn:sepade:xsd:pain.008.001.01}Party5Choice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification20", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "nm",
    "pstlAdr",
    "id"
})
public class PartyIdentification20 {

    @XmlElement(name = "Nm", namespace = "urn:sepade:xsd:pain.008.001.01", required = true)
    protected String nm;
    @XmlElement(name = "PstlAdr", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected PostalAddress5 pstlAdr;
    @XmlElement(name = "Id", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected Party5Choice id;

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
     *     {@link PostalAddress5 }
     *     
     */
    public PostalAddress5 getPstlAdr() {
        return pstlAdr;
    }

    /**
     * Sets the value of the pstlAdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddress5 }
     *     
     */
    public void setPstlAdr(PostalAddress5 value) {
        this.pstlAdr = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Party5Choice }
     *     
     */
    public Party5Choice getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party5Choice }
     *     
     */
    public void setId(Party5Choice value) {
        this.id = value;
    }

}
