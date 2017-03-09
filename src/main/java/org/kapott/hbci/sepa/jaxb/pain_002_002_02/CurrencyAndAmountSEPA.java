
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für CurrencyAndAmountSEPA complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CurrencyAndAmountSEPA">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:swift:xsd:$pain.002.002.02>CurrencyAndAmount_SimpleTypeSEPA">
 *       &lt;attribute name="Ccy" use="required" type="{urn:swift:xsd:$pain.002.002.02}CurrencyCodeEUR" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrencyAndAmountSEPA", namespace = "urn:swift:xsd:$pain.002.002.02", propOrder = {
    "value"
})
public class CurrencyAndAmountSEPA {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "Ccy", required = true)
    protected CurrencyCodeEUR ccy;

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
     *     {@link CurrencyCodeEUR }
     *     
     */
    public CurrencyCodeEUR getCcy() {
        return ccy;
    }

    /**
     * Legt den Wert der ccy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeEUR }
     *     
     */
    public void setCcy(CurrencyCodeEUR value) {
        this.ccy = value;
    }

}
