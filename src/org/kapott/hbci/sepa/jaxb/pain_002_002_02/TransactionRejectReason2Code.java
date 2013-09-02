
package org.kapott.hbci.sepa.jaxb.pain_002_002_02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionRejectReason2Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionRejectReason2Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AC01"/>
 *     &lt;enumeration value="AC04"/>
 *     &lt;enumeration value="AC06"/>
 *     &lt;enumeration value="AM01"/>
 *     &lt;enumeration value="AM02"/>
 *     &lt;enumeration value="AM03"/>
 *     &lt;enumeration value="AM04"/>
 *     &lt;enumeration value="AM05"/>
 *     &lt;enumeration value="AM06"/>
 *     &lt;enumeration value="AM07"/>
 *     &lt;enumeration value="BE01"/>
 *     &lt;enumeration value="BE04"/>
 *     &lt;enumeration value="BE05"/>
 *     &lt;enumeration value="AG01"/>
 *     &lt;enumeration value="AG02"/>
 *     &lt;enumeration value="DT01"/>
 *     &lt;enumeration value="RF01"/>
 *     &lt;enumeration value="RC01"/>
 *     &lt;enumeration value="TM01"/>
 *     &lt;enumeration value="ED01"/>
 *     &lt;enumeration value="ED03"/>
 *     &lt;enumeration value="MS03"/>
 *     &lt;enumeration value="MS02"/>
 *     &lt;enumeration value="BE06"/>
 *     &lt;enumeration value="BE07"/>
 *     &lt;enumeration value="AM09"/>
 *     &lt;enumeration value="AM10"/>
 *     &lt;enumeration value="MD01"/>
 *     &lt;enumeration value="MD02"/>
 *     &lt;enumeration value="MD03"/>
 *     &lt;enumeration value="MD04"/>
 *     &lt;enumeration value="MD06"/>
 *     &lt;enumeration value="MD07"/>
 *     &lt;enumeration value="ED05"/>
 *     &lt;enumeration value="NARR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionRejectReason2Code", namespace = "urn:swift:xsd:$pain.002.002.02")
@XmlEnum
public enum TransactionRejectReason2Code {

    @XmlEnumValue("AC01")
    AC_01("AC01"),
    @XmlEnumValue("AC04")
    AC_04("AC04"),
    @XmlEnumValue("AC06")
    AC_06("AC06"),
    @XmlEnumValue("AM01")
    AM_01("AM01"),
    @XmlEnumValue("AM02")
    AM_02("AM02"),
    @XmlEnumValue("AM03")
    AM_03("AM03"),
    @XmlEnumValue("AM04")
    AM_04("AM04"),
    @XmlEnumValue("AM05")
    AM_05("AM05"),
    @XmlEnumValue("AM06")
    AM_06("AM06"),
    @XmlEnumValue("AM07")
    AM_07("AM07"),
    @XmlEnumValue("BE01")
    BE_01("BE01"),
    @XmlEnumValue("BE04")
    BE_04("BE04"),
    @XmlEnumValue("BE05")
    BE_05("BE05"),
    @XmlEnumValue("AG01")
    AG_01("AG01"),
    @XmlEnumValue("AG02")
    AG_02("AG02"),
    @XmlEnumValue("DT01")
    DT_01("DT01"),
    @XmlEnumValue("RF01")
    RF_01("RF01"),
    @XmlEnumValue("RC01")
    RC_01("RC01"),
    @XmlEnumValue("TM01")
    TM_01("TM01"),
    @XmlEnumValue("ED01")
    ED_01("ED01"),
    @XmlEnumValue("ED03")
    ED_03("ED03"),
    @XmlEnumValue("MS03")
    MS_03("MS03"),
    @XmlEnumValue("MS02")
    MS_02("MS02"),
    @XmlEnumValue("BE06")
    BE_06("BE06"),
    @XmlEnumValue("BE07")
    BE_07("BE07"),
    @XmlEnumValue("AM09")
    AM_09("AM09"),
    @XmlEnumValue("AM10")
    AM_10("AM10"),
    @XmlEnumValue("MD01")
    MD_01("MD01"),
    @XmlEnumValue("MD02")
    MD_02("MD02"),
    @XmlEnumValue("MD03")
    MD_03("MD03"),
    @XmlEnumValue("MD04")
    MD_04("MD04"),
    @XmlEnumValue("MD06")
    MD_06("MD06"),
    @XmlEnumValue("MD07")
    MD_07("MD07"),
    @XmlEnumValue("ED05")
    ED_05("ED05"),
    NARR("NARR");
    private final String value;

    TransactionRejectReason2Code(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionRejectReason2Code fromValue(String v) {
        for (TransactionRejectReason2Code c: TransactionRejectReason2Code.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
