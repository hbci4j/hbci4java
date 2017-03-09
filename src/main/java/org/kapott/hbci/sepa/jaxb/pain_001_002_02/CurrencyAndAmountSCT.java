
package org.kapott.hbci.sepa.jaxb.pain_001_002_02;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for CurrencyAndAmountSCT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CurrencyAndAmountSCT">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:swift:xsd:$pain.001.002.02>CurrencyAndAmount_SimpleTypeSCT">
 *       &lt;attribute name="Ccy" use="required" type="{urn:swift:xsd:$pain.001.002.02}CurrencyCodeSCT" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrencyAndAmountSCT", namespace = "urn:swift:xsd:$pain.001.002.02", propOrder = {
    "value"
})
public class CurrencyAndAmountSCT {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "Ccy", required = true)
    protected CurrencyCodeSCT ccy;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the ccy property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyCodeSCT }
     *     
     */
    public CurrencyCodeSCT getCcy() {
        return ccy;
    }

    /**
     * Sets the value of the ccy property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeSCT }
     *     
     */
    public void setCcy(CurrencyCodeSCT value) {
        this.ccy = value;
    }

}
