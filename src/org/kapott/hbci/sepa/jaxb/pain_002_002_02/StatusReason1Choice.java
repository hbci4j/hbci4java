
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusReason1Choice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
    protected TransactionRejectReason2Code cd;
    @XmlElement(name = "Prtry", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected RestrictedProprietaryReasonSEPA prtry;

    /**
     * Gets the value of the cd property.
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
     * Sets the value of the cd property.
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
     * Gets the value of the prtry property.
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
     * Sets the value of the prtry property.
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
