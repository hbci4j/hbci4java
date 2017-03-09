
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für pain.001.001.02 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="pain.001.001.02">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:swift:xsd:$pain.001.002.02}GroupHeaderSCT"/>
 *         &lt;element name="PmtInf" type="{urn:swift:xsd:$pain.001.002.02}PaymentInstructionInformationSCT" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pain.001.001.02", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "grpHdr",
    "pmtInf"
})
public class Pain00100102 {

    @XmlElement(name = "GrpHdr", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected GroupHeaderSCT grpHdr;
    @XmlElement(name = "PmtInf", namespace = "urn:swift:xsd:$pain.001.002.02", required = true)
    protected List<PaymentInstructionInformationSCT> pmtInf;

    /**
     * Ruft den Wert der grpHdr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeaderSCT }
     *     
     */
    public GroupHeaderSCT getGrpHdr() {
        return grpHdr;
    }

    /**
     * Legt den Wert der grpHdr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeaderSCT }
     *     
     */
    public void setGrpHdr(GroupHeaderSCT value) {
        this.grpHdr = value;
    }

    /**
     * Gets the value of the pmtInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pmtInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPmtInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentInstructionInformationSCT }
     * 
     * 
     */
    public List<PaymentInstructionInformationSCT> getPmtInf() {
        if (pmtInf == null) {
            pmtInf = new ArrayList<PaymentInstructionInformationSCT>();
        }
        return this.pmtInf;
    }

}
