
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditorReferenceTypeSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditorReferenceTypeSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:swift:xsd:$pain.008.002.01}DocumentType3CodeSDD"/>
 *         &lt;element name="Issr" type="{urn:swift:xsd:$pain.008.002.01}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditorReferenceTypeSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "cd",
    "issr"
})
public class CreditorReferenceTypeSDD {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.008.002.01", required = true)
    protected DocumentType3CodeSDD cd;
    @XmlElement(name = "Issr", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected String issr;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentType3CodeSDD }
     *     
     */
    public DocumentType3CodeSDD getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType3CodeSDD }
     *     
     */
    public void setCd(DocumentType3CodeSDD value) {
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
