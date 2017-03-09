
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CreditorReferenceType1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CreditorReferenceType1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Cd" type="{urn:sepade:xsd:pain.008.001.01}DocumentType3Code"/>
 *           &lt;element name="Prtry" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
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
@XmlType(name = "CreditorReferenceType1", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "cd",
    "prtry",
    "issr"
})
public class CreditorReferenceType1 {

    @XmlElement(name = "Cd", namespace = "urn:sepade:xsd:pain.008.001.01")
    @XmlSchemaType(name = "string")
    protected DocumentType3Code cd;
    @XmlElement(name = "Prtry", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String prtry;
    @XmlElement(name = "Issr", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String issr;

    /**
     * Ruft den Wert der cd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DocumentType3Code }
     *     
     */
    public DocumentType3Code getCd() {
        return cd;
    }

    /**
     * Legt den Wert der cd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType3Code }
     *     
     */
    public void setCd(DocumentType3Code value) {
        this.cd = value;
    }

    /**
     * Ruft den Wert der prtry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrtry() {
        return prtry;
    }

    /**
     * Legt den Wert der prtry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrtry(String value) {
        this.prtry = value;
    }

    /**
     * Ruft den Wert der issr-Eigenschaft ab.
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
     * Legt den Wert der issr-Eigenschaft fest.
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
