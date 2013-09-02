
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentification17 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentification17">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nm" type="{urn:sepade:xsd:pain.008.001.01}Max70Text" minOccurs="0"/>
 *         &lt;element name="Id" type="{urn:sepade:xsd:pain.008.001.01}PartyPrivate1" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentification17", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "nm",
    "id"
})
public class PartyIdentification17 {

    @XmlElement(name = "Nm", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String nm;
    @XmlElement(name = "Id", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected PartyPrivate1 id;

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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link PartyPrivate1 }
     *     
     */
    public PartyPrivate1 getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyPrivate1 }
     *     
     */
    public void setId(PartyPrivate1 value) {
        this.id = value;
    }

}
