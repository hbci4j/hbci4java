
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AmendmentInformationDetailsSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AmendmentInformationDetailsSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgnlMndtId" type="{urn:swift:xsd:$pain.002.002.02}Max35Text" minOccurs="0"/>
 *         &lt;element name="OrgnlCdtrSchmeId" type="{urn:swift:xsd:$pain.002.002.02}PartyIdentificationSEPA3" minOccurs="0"/>
 *         &lt;element name="OrgnlDbtrAcct" type="{urn:swift:xsd:$pain.002.002.02}CashAccountSEPA2" minOccurs="0"/>
 *         &lt;element name="OrgnlDbtrAgt" type="{urn:swift:xsd:$pain.002.002.02}BranchAndFinancialInstitutionIdentificationSEPA2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmendmentInformationDetailsSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "orgnlMndtId",
    "orgnlCdtrSchmeId",
    "orgnlDbtrAcct",
    "orgnlDbtrAgt"
})
public class AmendmentInformationDetailsSEPA {

    @XmlElement(name = "OrgnlMndtId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String orgnlMndtId;
    @XmlElement(name = "OrgnlCdtrSchmeId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected PartyIdentificationSEPA3 orgnlCdtrSchmeId;
    @XmlElement(name = "OrgnlDbtrAcct", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected CashAccountSEPA2 orgnlDbtrAcct;
    @XmlElement(name = "OrgnlDbtrAgt", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected BranchAndFinancialInstitutionIdentificationSEPA2 orgnlDbtrAgt;

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
     *     {@link PartyIdentificationSEPA3 }
     *     
     */
    public PartyIdentificationSEPA3 getOrgnlCdtrSchmeId() {
        return orgnlCdtrSchmeId;
    }

    /**
     * Legt den Wert der orgnlCdtrSchmeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA3 }
     *     
     */
    public void setOrgnlCdtrSchmeId(PartyIdentificationSEPA3 value) {
        this.orgnlCdtrSchmeId = value;
    }

    /**
     * Ruft den Wert der orgnlDbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSEPA2 }
     *     
     */
    public CashAccountSEPA2 getOrgnlDbtrAcct() {
        return orgnlDbtrAcct;
    }

    /**
     * Legt den Wert der orgnlDbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSEPA2 }
     *     
     */
    public void setOrgnlDbtrAcct(CashAccountSEPA2 value) {
        this.orgnlDbtrAcct = value;
    }

    /**
     * Ruft den Wert der orgnlDbtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA2 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSEPA2 getOrgnlDbtrAgt() {
        return orgnlDbtrAgt;
    }

    /**
     * Legt den Wert der orgnlDbtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA2 }
     *     
     */
    public void setOrgnlDbtrAgt(BranchAndFinancialInstitutionIdentificationSEPA2 value) {
        this.orgnlDbtrAgt = value;
    }

}
