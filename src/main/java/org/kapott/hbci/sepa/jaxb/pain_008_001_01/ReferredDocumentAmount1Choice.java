
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReferredDocumentAmount1Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReferredDocumentAmount1Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="DuePyblAmt" type="{urn:sepade:xsd:pain.008.001.01}CurrencyAndAmount"/>
 *           &lt;element name="DscntApldAmt" type="{urn:sepade:xsd:pain.008.001.01}CurrencyAndAmount"/>
 *           &lt;element name="RmtdAmt" type="{urn:sepade:xsd:pain.008.001.01}CurrencyAndAmount"/>
 *           &lt;element name="CdtNoteAmt" type="{urn:sepade:xsd:pain.008.001.01}CurrencyAndAmount"/>
 *           &lt;element name="TaxAmt" type="{urn:sepade:xsd:pain.008.001.01}CurrencyAndAmount"/>
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
@XmlType(name = "ReferredDocumentAmount1Choice", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "duePyblAmt",
    "dscntApldAmt",
    "rmtdAmt",
    "cdtNoteAmt",
    "taxAmt"
})
public class ReferredDocumentAmount1Choice {

    @XmlElement(name = "DuePyblAmt", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected CurrencyAndAmount duePyblAmt;
    @XmlElement(name = "DscntApldAmt", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected CurrencyAndAmount dscntApldAmt;
    @XmlElement(name = "RmtdAmt", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected CurrencyAndAmount rmtdAmt;
    @XmlElement(name = "CdtNoteAmt", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected CurrencyAndAmount cdtNoteAmt;
    @XmlElement(name = "TaxAmt", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected CurrencyAndAmount taxAmt;

    /**
     * Ruft den Wert der duePyblAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getDuePyblAmt() {
        return duePyblAmt;
    }

    /**
     * Legt den Wert der duePyblAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setDuePyblAmt(CurrencyAndAmount value) {
        this.duePyblAmt = value;
    }

    /**
     * Ruft den Wert der dscntApldAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getDscntApldAmt() {
        return dscntApldAmt;
    }

    /**
     * Legt den Wert der dscntApldAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setDscntApldAmt(CurrencyAndAmount value) {
        this.dscntApldAmt = value;
    }

    /**
     * Ruft den Wert der rmtdAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getRmtdAmt() {
        return rmtdAmt;
    }

    /**
     * Legt den Wert der rmtdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setRmtdAmt(CurrencyAndAmount value) {
        this.rmtdAmt = value;
    }

    /**
     * Ruft den Wert der cdtNoteAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getCdtNoteAmt() {
        return cdtNoteAmt;
    }

    /**
     * Legt den Wert der cdtNoteAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setCdtNoteAmt(CurrencyAndAmount value) {
        this.cdtNoteAmt = value;
    }

    /**
     * Ruft den Wert der taxAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getTaxAmt() {
        return taxAmt;
    }

    /**
     * Legt den Wert der taxAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setTaxAmt(CurrencyAndAmount value) {
        this.taxAmt = value;
    }

}
