
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für StatusReasonInformationSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="StatusReasonInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Orgtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}PartyIdentificationSEPA6Choice" minOccurs="0"/>
 *         &lt;element name="Rsn" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}StatusReasonSEPA" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusReasonInformationSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "orgtr",
    "rsn"
})
public class StatusReasonInformationSEPA {

    @XmlElement(name = "Orgtr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected PartyIdentificationSEPA6Choice orgtr;
    @XmlElement(name = "Rsn", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected StatusReasonSEPA rsn;

    /**
     * Ruft den Wert der orgtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentificationSEPA6Choice }
     *     
     */
    public PartyIdentificationSEPA6Choice getOrgtr() {
        return orgtr;
    }

    /**
     * Legt den Wert der orgtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentificationSEPA6Choice }
     *     
     */
    public void setOrgtr(PartyIdentificationSEPA6Choice value) {
        this.orgtr = value;
    }

    /**
     * Ruft den Wert der rsn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StatusReasonSEPA }
     *     
     */
    public StatusReasonSEPA getRsn() {
        return rsn;
    }

    /**
     * Legt den Wert der rsn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusReasonSEPA }
     *     
     */
    public void setRsn(StatusReasonSEPA value) {
        this.rsn = value;
    }

}
