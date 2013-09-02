
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentificationSDD3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentificationSDD3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nm" type="{urn:swift:xsd:$pain.008.002.01}Max70Text"/>
 *         &lt;element name="PstlAdr" type="{urn:swift:xsd:$pain.008.002.01}PostalAddressSDD" minOccurs="0"/>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.008.002.01}PartySDDChoice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentificationSDD3", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "nm",
    "pstlAdr",
    "id"
})
public class PartyIdentificationSDD3 {

    @XmlElement(name = "Nm", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected String nm;
    @XmlElement(name = "PstlAdr", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PostalAddressSDD pstlAdr;
    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PartySDDChoice id;

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
     *     {@link PostalAddressSDD }
     *     
     */
    public PostalAddressSDD getPstlAdr() {
        return pstlAdr;
    }

    /**
     * Sets the value of the pstlAdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSDD }
     *     
     */
    public void setPstlAdr(PostalAddressSDD value) {
        this.pstlAdr = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link PartySDDChoice }
     *     
     */
    public PartySDDChoice getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartySDDChoice }
     *     
     */
    public void setId(PartySDDChoice value) {
        this.id = value;
    }

}
