
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentificationSCT1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentificationSCT1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nm" type="{urn:swift:xsd:$pain.001.002.02}Max70Text" minOccurs="0"/>
 *         &lt;element name="Id" type="{urn:swift:xsd:$pain.001.002.02}PartySCTChoice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentificationSCT1", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "nm",
    "id"
})
public class PartyIdentificationSCT1 {

    @XmlElement(name = "Nm", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected String nm;
    @XmlElement(name = "Id", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected PartySCTChoice id;

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
     *     {@link PartySCTChoice }
     *     
     */
    public PartySCTChoice getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartySCTChoice }
     *     
     */
    public void setId(PartySCTChoice value) {
        this.id = value;
    }

}
