
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für ActiveOrHistoricCurrencyAndAmountSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ActiveOrHistoricCurrencyAndAmountSEPA">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:iso:std:iso:20022:tech:xsd:pain.002.003.03>ActiveOrHistoricCurrencyAndAmount_SimpleTypeSEPA">
 *       &lt;attribute name="Ccy" use="required" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}ActiveOrHistoricCurrencyCodeEUR" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActiveOrHistoricCurrencyAndAmountSEPA", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "value"
})
public class ActiveOrHistoricCurrencyAndAmountSEPA {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "Ccy", required = true)
    protected ActiveOrHistoricCurrencyCodeEUR ccy;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
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
     * Legt den Wert der value-Eigenschaft fest.
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
     * Ruft den Wert der ccy-Eigenschaft ab.
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
     * Legt den Wert der ccy-Eigenschaft fest.
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
