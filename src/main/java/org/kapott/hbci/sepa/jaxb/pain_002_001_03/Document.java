
package org.kapott.hbci.sepa.jaxb.pain_002_001_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Document complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CstmrPmtStsRpt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.001.03}CustomerPaymentStatusReportV03"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03", propOrder = {
    "cstmrPmtStsRpt"
})
public class Document {

    @XmlElement(name = "CstmrPmtStsRpt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03", required = true)
    protected CustomerPaymentStatusReportV03 cstmrPmtStsRpt;

    /**
     * Ruft den Wert der cstmrPmtStsRpt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CustomerPaymentStatusReportV03 }
     *     
     */
    public CustomerPaymentStatusReportV03 getCstmrPmtStsRpt() {
        return cstmrPmtStsRpt;
    }

    /**
     * Legt den Wert der cstmrPmtStsRpt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerPaymentStatusReportV03 }
     *     
     */
    public void setCstmrPmtStsRpt(CustomerPaymentStatusReportV03 value) {
        this.cstmrPmtStsRpt = value;
    }

}
