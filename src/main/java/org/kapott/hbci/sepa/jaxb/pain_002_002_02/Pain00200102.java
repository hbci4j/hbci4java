
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pain.002.001.02 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pain.002.001.02">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:swift:xsd:$pain.002.002.02}GroupHeaderSEPA"/>
 *         &lt;element name="OrgnlGrpInfAndSts" type="{urn:swift:xsd:$pain.002.002.02}OriginalGroupInformationSEPA"/>
 *         &lt;element name="TxInfAndSts" type="{urn:swift:xsd:$pain.002.002.02}PaymentTransactionInformationSEPA" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pain.002.001.02", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "grpHdr",
    "orgnlGrpInfAndSts",
    "txInfAndSts"
})
public class Pain00200102 {

    @XmlElement(name = "GrpHdr", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected GroupHeaderSEPA grpHdr;
    @XmlElement(name = "OrgnlGrpInfAndSts", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected OriginalGroupInformationSEPA orgnlGrpInfAndSts;
    @XmlElement(name = "TxInfAndSts", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected List<PaymentTransactionInformationSEPA> txInfAndSts;

    /**
     * Gets the value of the grpHdr property.
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
     * Sets the value of the grpHdr property.
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
     * Gets the value of the orgnlGrpInfAndSts property.
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
     * Sets the value of the orgnlGrpInfAndSts property.
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
     * Gets the value of the txInfAndSts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the txInfAndSts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTxInfAndSts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentTransactionInformationSEPA }
     * 
     * 
     */
    public List<PaymentTransactionInformationSEPA> getTxInfAndSts() {
        if (txInfAndSts == null) {
            txInfAndSts = new ArrayList<PaymentTransactionInformationSEPA>();
        }
        return this.txInfAndSts;
    }

}
