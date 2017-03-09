
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AmendmentInformationDetailsSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AmendmentInformationDetailsSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgnlMndtId" type="{urn:swift:xsd:$pain.008.002.01}RestrictedIdentification1" minOccurs="0"/>
 *         &lt;element name="OrgnlCdtrSchmeId" type="{urn:swift:xsd:$pain.008.002.01}PartyIdentificationSDD5" minOccurs="0"/>
 *         &lt;element name="OrgnlDbtrAcct" type="{urn:swift:xsd:$pain.008.002.01}CashAccountSDD2" minOccurs="0"/>
 *         &lt;element name="OrgnlDbtrAgt" type="{urn:swift:xsd:$pain.008.002.01}BranchAndFinancialInstitutionIdentificationSDD2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmendmentInformationDetailsSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "orgnlMndtId",
    "orgnlCdtrSchmeId",
    "orgnlDbtrAcct",
    "orgnlDbtrAgt"
})
public class AmendmentInformationDetailsSDD {

    @XmlElement(name = "OrgnlMndtId", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected String orgnlMndtId;
    @XmlElement(name = "OrgnlCdtrSchmeId", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected PartyIdentificationSDD5 orgnlCdtrSchmeId;
    @XmlElement(name = "OrgnlDbtrAcct", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected CashAccountSDD2 orgnlDbtrAcct;
    @XmlElement(name = "OrgnlDbtrAgt", namespace = "urn:swift:xsd:$pain.008.002.01")
    protected BranchAndFinancialInstitutionIdentificationSDD2 orgnlDbtrAgt;

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
     *     {@link PartyIdentificationSDD5 }
     *     
     */
    public PartyIdentificationSDD5 getOrgnlCdtrSchmeId() {
        return orgnlCdtrSchmeId;
    }

    /**
     * Legt den Wert der orgnlCdtrSchmeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSDD5 }
     *     
     */
    public void setOrgnlCdtrSchmeId(PartyIdentificationSDD5 value) {
        this.orgnlCdtrSchmeId = value;
    }

    /**
     * Ruft den Wert der orgnlDbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountSDD2 }
     *     
     */
    public CashAccountSDD2 getOrgnlDbtrAcct() {
        return orgnlDbtrAcct;
    }

    /**
     * Legt den Wert der orgnlDbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountSDD2 }
     *     
     */
    public void setOrgnlDbtrAcct(CashAccountSDD2 value) {
        this.orgnlDbtrAcct = value;
    }

    /**
     * Ruft den Wert der orgnlDbtrAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSDD2 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSDD2 getOrgnlDbtrAgt() {
        return orgnlDbtrAgt;
    }

    /**
     * Legt den Wert der orgnlDbtrAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSDD2 }
     *     
     */
    public void setOrgnlDbtrAgt(BranchAndFinancialInstitutionIdentificationSDD2 value) {
        this.orgnlDbtrAgt = value;
    }

}
