
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OriginalGroupInformationSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OriginalGroupInformationSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="OrgnlMsgId" type="{urn:swift:xsd:$pain.002.002.02}RestrictedIdentification1"/>
 *           &lt;element name="NtwkFileNm" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *         &lt;/choice>
 *         &lt;element name="OrgnlMsgNmId" type="{urn:swift:xsd:$pain.002.002.02}Max35Text"/>
 *         &lt;element name="GrpSts" type="{urn:swift:xsd:$pain.002.002.02}TransactionGroupStatus1CodeSEPA" minOccurs="0"/>
 *         &lt;element name="StsRsnInf" type="{urn:swift:xsd:$pain.002.002.02}StatusReasonInformationSEPA" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OriginalGroupInformationSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "orgnlMsgId",
    "ntwkFileNm",
    "orgnlMsgNmId",
    "grpSts",
    "stsRsnInf"
})
public class OriginalGroupInformationSEPA {

    @XmlElement(name = "OrgnlMsgId", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String orgnlMsgId;
    @XmlElement(name = "NtwkFileNm", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected String ntwkFileNm;
    @XmlElement(name = "OrgnlMsgNmId", namespace = "urn:swift:xsd:$pain.002.002.02", required = true)
    protected String orgnlMsgNmId;
    @XmlElement(name = "GrpSts", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected TransactionGroupStatus1CodeSEPA grpSts;
    @XmlElement(name = "StsRsnInf", namespace = "urn:swift:xsd:$pain.002.002.02")
    protected List<StatusReasonInformationSEPA> stsRsnInf;

    /**
     * Gets the value of the orgnlMsgId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlMsgId() {
        return orgnlMsgId;
    }

    /**
     * Sets the value of the orgnlMsgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlMsgId(String value) {
        this.orgnlMsgId = value;
    }

    /**
     * Gets the value of the ntwkFileNm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNtwkFileNm() {
        return ntwkFileNm;
    }

    /**
     * Sets the value of the ntwkFileNm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNtwkFileNm(String value) {
        this.ntwkFileNm = value;
    }

    /**
     * Gets the value of the orgnlMsgNmId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgnlMsgNmId() {
        return orgnlMsgNmId;
    }

    /**
     * Sets the value of the orgnlMsgNmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgnlMsgNmId(String value) {
        this.orgnlMsgNmId = value;
    }

    /**
     * Gets the value of the grpSts property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionGroupStatus1CodeSEPA }
     *     
     */
    public TransactionGroupStatus1CodeSEPA getGrpSts() {
        return grpSts;
    }

    /**
     * Sets the value of the grpSts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionGroupStatus1CodeSEPA }
     *     
     */
    public void setGrpSts(TransactionGroupStatus1CodeSEPA value) {
        this.grpSts = value;
    }

    /**
     * Gets the value of the stsRsnInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stsRsnInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStsRsnInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StatusReasonInformationSEPA }
     * 
     * 
     */
    public List<StatusReasonInformationSEPA> getStsRsnInf() {
        if (stsRsnInf == null) {
            stsRsnInf = new ArrayList<StatusReasonInformationSEPA>();
        }
        return this.stsRsnInf;
    }

}
