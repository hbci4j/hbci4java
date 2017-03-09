
package org.kapott.hbci.sepa.jaxb.pain_008_001_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DirectDebitTransactionSDD complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DirectDebitTransactionSDD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MndtRltdInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}MandateRelatedInformationSDD"/>
 *         &lt;element name="CdtrSchmeId" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.001.02}PartyIdentificationSEPA3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectDebitTransactionSDD", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", propOrder = {
    "mndtRltdInf",
    "cdtrSchmeId"
})
public class DirectDebitTransactionSDD {

    @XmlElement(name = "MndtRltdInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02", required = true)
    protected MandateRelatedInformationSDD mndtRltdInf;
    @XmlElement(name = "CdtrSchmeId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02")
    protected PartyIdentificationSEPA3 cdtrSchmeId;

    /**
     * Ruft den Wert der mndtRltdInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MandateRelatedInformationSDD }
     *     
     */
    public MandateRelatedInformationSDD getMndtRltdInf() {
        return mndtRltdInf;
    }

    /**
     * Legt den Wert der mndtRltdInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MandateRelatedInformationSDD }
     *     
     */
    public void setMndtRltdInf(MandateRelatedInformationSDD value) {
        this.mndtRltdInf = value;
    }

    /**
     * Ruft den Wert der cdtrSchmeId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA3 }
     *     
     */
    public PartyIdentificationSEPA3 getCdtrSchmeId() {
        return cdtrSchmeId;
    }

    /**
     * Legt den Wert der cdtrSchmeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA3 }
     *     
     */
    public void setCdtrSchmeId(PartyIdentificationSEPA3 value) {
        this.cdtrSchmeId = value;
    }

}
