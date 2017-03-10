
package org.kapott.hbci.sepa.jaxb.pain_001_001_02;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Party2Choice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Party2Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="OrgId" type="{urn:sepade:xsd:pain.001.001.02}OrganisationIdentification2"/>
 *           &lt;element name="PrvtId" type="{urn:sepade:xsd:pain.001.001.02}PersonIdentification3" maxOccurs="4"/>
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
@XmlType(name = "Party2Choice", namespace = "urn:sepade:xsd:pain.001.001.02", propOrder = {
    "orgId",
    "prvtId"
})
public class Party2Choice {

    @XmlElement(name = "OrgId", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected OrganisationIdentification2 orgId;
    @XmlElement(name = "PrvtId", namespace = "urn:sepade:xsd:pain.001.001.02")
    protected List<PersonIdentification3> prvtId;

    /**
     * Gets the value of the orgId property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationIdentification2 }
     *     
     */
    public OrganisationIdentification2 getOrgId() {
        return orgId;
    }

    /**
     * Sets the value of the orgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationIdentification2 }
     *     
     */
    public void setOrgId(OrganisationIdentification2 value) {
        this.orgId = value;
    }

    /**
     * Gets the value of the prvtId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prvtId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrvtId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonIdentification3 }
     * 
     * 
     */
    public List<PersonIdentification3> getPrvtId() {
        if (prvtId == null) {
            prvtId = new ArrayList<PersonIdentification3>();
        }
        return this.prvtId;
    }

}
