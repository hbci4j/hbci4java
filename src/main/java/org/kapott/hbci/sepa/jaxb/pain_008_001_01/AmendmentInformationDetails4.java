
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AmendmentInformationDetails4 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AmendmentInformationDetails4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgnlMndtId" type="{urn:sepade:xsd:pain.008.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="OrgnlCdtrSchmeId" type="{urn:sepade:xsd:pain.008.001.01}PartyIdentification17" minOccurs="0"/>
 *         &lt;element name="OrgnlDbtrAcct" type="{urn:sepade:xsd:pain.008.001.01}CashAccount8" minOccurs="0"/>
 *         &lt;element name="OrgnlDbtrAgt" type="{urn:sepade:xsd:pain.008.001.01}FinancialInstitution3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmendmentInformationDetails4", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "orgnlMndtId",
    "orgnlCdtrSchmeId",
    "orgnlDbtrAcct",
    "orgnlDbtrAgt"
})
public class AmendmentInformationDetails4 {

    @XmlElement(name = "OrgnlMndtId", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String orgnlMndtId;
    @XmlElement(name = "OrgnlCdtrSchmeId", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected PartyIdentification17 orgnlCdtrSchmeId;
    @XmlElement(name = "OrgnlDbtrAcct", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected CashAccount8 orgnlDbtrAcct;
    @XmlElement(name = "OrgnlDbtrAgt", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected FinancialInstitution3 orgnlDbtrAgt;

    /**
     * Ruft den Wert der orgnlMndtId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlMndtId() {
        return orgnlMndtId;
    }

    /**
     * Legt den Wert der orgnlMndtId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlMndtId(String value) {
        this.orgnlMndtId = value;
    }

    /**
     * Ruft den Wert der orgnlCdtrSchmeId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification17 }
     *     
     */
    public PartyIdentification17 getOrgnlCdtrSchmeId() {
        return orgnlCdtrSchmeId;
    }

    /**
     * Legt den Wert der orgnlCdtrSchmeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification17 }
     *     
     */
    public void setOrgnlCdtrSchmeId(PartyIdentification17 value) {
        this.orgnlCdtrSchmeId = value;
    }

    /**
     * Ruft den Wert der orgnlDbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount8 }
     *     
     */
    public CashAccount8 getOrgnlDbtrAcct() {
        return orgnlDbtrAcct;
    }

    /**
     * Legt den Wert der orgnlDbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount8 }
     *     
     */
    public void setOrgnlDbtrAcct(CashAccount8 value) {
        this.orgnlDbtrAcct = value;
    }

    /**
     * Ruft den Wert der orgnlDbtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitution3 }
     *     
     */
    public FinancialInstitution3 getOrgnlDbtrAgt() {
        return orgnlDbtrAgt;
    }

    /**
     * Legt den Wert der orgnlDbtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitution3 }
     *     
     */
    public void setOrgnlDbtrAgt(FinancialInstitution3 value) {
        this.orgnlDbtrAgt = value;
    }

}
