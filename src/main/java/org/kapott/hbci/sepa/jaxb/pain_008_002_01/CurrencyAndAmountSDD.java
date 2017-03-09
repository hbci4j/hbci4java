
package org.kapott.hbci.sepa.jaxb.pain_008_002_01;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for CurrencyAndAmountSDD complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CurrencyAndAmountSDD">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:swift:xsd:$pain.008.002.01>CurrencyAndAmount_SimpleTypeSDD">
 *       &lt;attribute name="Ccy" use="required" type="{urn:swift:xsd:$pain.008.002.01}CurrencyCodeSDD" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrencyAndAmountSDD", namespace = "urn:swift:xsd:$pain.008.002.01", propOrder = {
    "value"
})
public class CurrencyAndAmountSDD {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "Ccy", required = true)
    protected CurrencyCodeSDD ccy;

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
     *     {@link CurrencyCodeSDD }
     *     
     */
    public CurrencyCodeSDD getCcy() {
        return ccy;
    }

    /**
     * Sets the value of the ccy property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeSDD }
     *     
     */
    public void setCcy(CurrencyCodeSDD value) {
        this.ccy = value;
    }

}
