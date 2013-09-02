
package org.kapott.hbci.sepa.jaxb.pain_008_002_02;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for ActiveOrHistoricCurrencyAndAmountSEPA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActiveOrHistoricCurrencyAndAmountSEPA">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:iso:std:iso:20022:tech:xsd:pain.008.002.02>ActiveOrHistoricCurrencyAndAmount_SimpleTypeSEPA">
 *       &lt;attribute name="Ccy" use="required" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}ActiveOrHistoricCurrencyCodeEUR" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActiveOrHistoricCurrencyAndAmountSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", propOrder = {
    "value"
})
public class ActiveOrHistoricCurrencyAndAmountSEPA {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "Ccy", required = true)
    protected ActiveOrHistoricCurrencyCodeEUR ccy;

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
     *     {@link ActiveOrHistoricCurrencyCodeEUR }
     *     
     */
    public ActiveOrHistoricCurrencyCodeEUR getCcy() {
        return ccy;
    }

    /**
     * Sets the value of the ccy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveOrHistoricCurrencyCodeEUR }
     *     
     */
    public void setCcy(ActiveOrHistoricCurrencyCodeEUR value) {
        this.ccy = value;
    }

}
