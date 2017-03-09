
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for GroupHeaderSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GroupHeaderSEPA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgId" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}RestrictedIdentificationSEPA1"/>
 *         &lt;element name="CreDtTm" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}ISODateTime"/>
 *         &lt;choice>
 *           &lt;element name="DbtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}BranchAndFinancialInstitutionIdentificationSEPA1" minOccurs="0"/>
 *           &lt;element name="CdtrAgt" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}BranchAndFinancialInstitutionIdentificationSEPA1" minOccurs="0"/>
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
@XmlType(name = "GroupHeaderSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "msgId",
    "creDtTm",
    "dbtrAgt",
    "cdtrAgt"
})
public class GroupHeaderSEPA {

    @XmlElement(name = "MsgId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", required = true)
    protected String msgId;
    @XmlElement(name = "CreDtTm", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", required = true)
    protected XMLGregorianCalendar creDtTm;
    @XmlElement(name = "DbtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected BranchAndFinancialInstitutionIdentificationSEPA1 dbtrAgt;
    @XmlElement(name = "CdtrAgt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected BranchAndFinancialInstitutionIdentificationSEPA1 cdtrAgt;

    /**
     * Gets the value of the msgId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Sets the value of the msgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgId(String value) {
        this.msgId = value;
    }

    /**
     * Gets the value of the creDtTm property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreDtTm() {
        return creDtTm;
    }

    /**
     * Sets the value of the creDtTm property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreDtTm(XMLGregorianCalendar value) {
        this.creDtTm = value;
    }

    /**
     * Gets the value of the dbtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSEPA1 getDbtrAgt() {
        return dbtrAgt;
    }

    /**
     * Sets the value of the dbtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public void setDbtrAgt(BranchAndFinancialInstitutionIdentificationSEPA1 value) {
        this.dbtrAgt = value;
    }

    /**
     * Gets the value of the cdtrAgt property.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public BranchAndFinancialInstitutionIdentificationSEPA1 getCdtrAgt() {
        return cdtrAgt;
    }

    /**
     * Sets the value of the cdtrAgt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentificationSEPA1 }
     *     
     */
    public void setCdtrAgt(BranchAndFinancialInstitutionIdentificationSEPA1 value) {
        this.cdtrAgt = value;
    }

}
