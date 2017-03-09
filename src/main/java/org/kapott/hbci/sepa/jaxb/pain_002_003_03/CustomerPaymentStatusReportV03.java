
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CustomerPaymentStatusReportV03 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CustomerPaymentStatusReportV03">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}GroupHeaderSEPA"/>
 *         &lt;element name="OrgnlGrpInfAndSts" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}OriginalGroupInformationSEPA"/>
 *         &lt;element name="OrgnlPmtInfAndSts" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}OriginalPaymentInformationSEPA" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerPaymentStatusReportV03", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "grpHdr",
    "orgnlGrpInfAndSts",
    "orgnlPmtInfAndSts"
})
public class CustomerPaymentStatusReportV03 {

    @XmlElement(name = "GrpHdr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", required = true)
    protected GroupHeaderSEPA grpHdr;
    @XmlElement(name = "OrgnlGrpInfAndSts", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", required = true)
    protected OriginalGroupInformationSEPA orgnlGrpInfAndSts;
    @XmlElement(name = "OrgnlPmtInfAndSts", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected List<OriginalPaymentInformationSEPA> orgnlPmtInfAndSts;

    /**
     * Ruft den Wert der grpHdr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeaderSEPA }
     *     
     */
    public GroupHeaderSEPA getGrpHdr() {
        return grpHdr;
    }

    /**
     * Legt den Wert der grpHdr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeaderSEPA }
     *     
     */
    public void setGrpHdr(GroupHeaderSEPA value) {
        this.grpHdr = value;
    }

    /**
     * Ruft den Wert der orgnlGrpInfAndSts-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OriginalGroupInformationSEPA }
     *     
     */
    public OriginalGroupInformationSEPA getOrgnlGrpInfAndSts() {
        return orgnlGrpInfAndSts;
    }

    /**
     * Legt den Wert der orgnlGrpInfAndSts-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginalGroupInformationSEPA }
     *     
     */
    public void setOrgnlGrpInfAndSts(OriginalGroupInformationSEPA value) {
        this.orgnlGrpInfAndSts = value;
    }

    /**
     * Gets the value of the orgnlPmtInfAndSts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orgnlPmtInfAndSts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrgnlPmtInfAndSts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OriginalPaymentInformationSEPA }
     * 
     * 
     */
    public List<OriginalPaymentInformationSEPA> getOrgnlPmtInfAndSts() {
        if (orgnlPmtInfAndSts == null) {
            orgnlPmtInfAndSts = new ArrayList<OriginalPaymentInformationSEPA>();
        }
        return this.orgnlPmtInfAndSts;
    }

}
