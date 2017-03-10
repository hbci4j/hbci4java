
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentificationSEPA1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentificationSEPA1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Nm" type="{urn:swift:xsd:$pain.002.002.02}Max70Text"/>
 *           &lt;element name="Id" type="{urn:swift:xsd:$pain.002.002.02}PartySEPA1"/>
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
@XmlType(name = "PartyIdentificationSEPA1", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "nm",
    "id"
})
public class PartyIdentificationSEPA1 {

    @XmlElement(name = "Nm", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String nm;
    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected PartySEPA1 id;

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
     *     {@link PartySEPA1 }
     *     
     */
    public PartySEPA1 getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartySEPA1 }
     *     
     */
    public void setId(PartySEPA1 value) {
        this.id = value;
    }

}
