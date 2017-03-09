
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für StatusReason1Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="StatusReason1Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Cd" type="{urn:swift:xsd:$pain.002.002.02}TransactionRejectReason2Code"/>
 *           &lt;element name="Prtry" type="{urn:swift:xsd:$pain.002.002.02}RestrictedProprietaryReasonSEPA"/>
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
@XmlType(name = "StatusReason1Choice", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "cd",
    "prtry"
})
public class StatusReason1Choice {

    @XmlElement(name = "Cd", namespace = "urn:swift:xsd:$pain.002.002.02")
    @XmlSchemaType(name = "string")
    protected TransactionRejectReason2Code cd;
    @XmlElement(name = "Prtry", namespace = "urn:swift:xsd:$pain.002.002.02")
    @XmlSchemaType(name = "string")
    protected RestrictedProprietaryReasonSEPA prtry;

    /**
     * Ruft den Wert der cd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionRejectReason2Code }
     *     
     */
    public TransactionRejectReason2Code getCd() {
        return cd;
    }

    /**
     * Legt den Wert der cd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionRejectReason2Code }
     *     
     */
    public void setCd(TransactionRejectReason2Code value) {
        this.cd = value;
    }

    /**
     * Ruft den Wert der prtry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RestrictedProprietaryReasonSEPA }
     *     
     */
    public RestrictedProprietaryReasonSEPA getPrtry() {
        return prtry;
    }

    /**
     * Legt den Wert der prtry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictedProprietaryReasonSEPA }
     *     
     */
    public void setPrtry(RestrictedProprietaryReasonSEPA value) {
        this.prtry = value;
    }

}
