
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditorReferenceTypeSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditorReferenceTypeSCT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:swift:xsd:$pain.001.002.02}DocumentType3CodeSCT"/>
 *         &lt;element name="Issr" type="{urn:swift:xsd:$pain.001.002.02}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditorReferenceTypeSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "cd",
    "issr"
})
public class CreditorReferenceTypeSCT {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected DocumentType3CodeSCT cd;
    @XmlElement(name = "Issr", namespace = "urn:swift:xsd:$pain.001.002.02")
    protected String issr;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentType3CodeSCT }
     *     
     */
    public DocumentType3CodeSCT getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType3CodeSCT }
     *     
     */
    public void setCd(DocumentType3CodeSCT value) {
        this.cd = value;
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
