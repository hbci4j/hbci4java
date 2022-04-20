package org.kapott.hbci.GV;

import org.kapott.hbci.GV_Result.GVRDauerLastNew;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;

import java.text.DecimalFormat;
import java.util.Properties;

/**
 * Geschaeftsvorfall zum Anlegen eines neuen SEPA-Dauerlastschriftauftrages.
 */
public class GVDauerLastSEPANew extends AbstractGVLastSEPA {

    /**
     * Liefert den Lowlevel-Namen des Jobs.
     *
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName() {
        return "DauerLastSEPANew";
    }

    /**
     * ct.
     *
     * @param handler
     */
    public GVDauerLastSEPANew(HBCIHandler handler) {
        super(handler, getLowlevelName(), new GVRDauerLastNew());

        // Typ der Lastschrift. Moegliche Werte:
        // CORE = Basis-Lastschrift (Default)
        // COR1 = Basis-Lastschrift mit verkuerzter Vorlaufzeit
        // B2B  = Business-2-Business-Lastschrift mit eingeschraenkter Rueckgabe-Moeglichkeit
        //
        addConstraint("type", "sepa.type", "CORE", LogFilter.FILTER_NONE);

        // DauerDetails
        addConstraint("firstdate", "DauerDetails.firstdate", null, LogFilter.FILTER_NONE);
        addConstraint("timeunit", "DauerDetails.timeunit", null, LogFilter.FILTER_NONE);
        addConstraint("turnus", "DauerDetails.turnus", null, LogFilter.FILTER_NONE);
        addConstraint("execday", "DauerDetails.execday", null, LogFilter.FILTER_NONE);
        addConstraint("lastdate", "DauerDetails.lastdate", "", LogFilter.FILTER_NONE);
    }

    /**
     * @see HBCIJobImpl#setParam(String, String)
     */
    public void setParam(String paramName, String value) {
        Properties res = getJobRestrictions();

        if (paramName.equals("timeunit")) {
            if (!(value.equals("W") || value.equals("M"))) {
                String msg = HBCIUtilsInternal.getLocMsg("EXCMSG_INV_TIMEUNIT", value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(), "client.errors.ignoreWrongJobDataErrors", msg))
                    throw new InvalidUserDataException(msg);
            }
        } else if (paramName.equals("turnus")) {
            String timeunit = getLowlevelParams().getProperty(getName() + ".DauerDetails.timeunit");

            if (timeunit != null) {
                if (timeunit.equals("W")) {
                    String st = res.getProperty("turnusweeks");

                    if (st != null) {
                        String value2 = new DecimalFormat("00").format(Integer.parseInt(value));

                        if (!st.equals("00") && !twoDigitValueInList(value2, st)) {
                            String msg = HBCIUtilsInternal.getLocMsg("EXCMSG_INV_TURNUS", value);
                            if (!HBCIUtilsInternal.ignoreError(getMainPassport(), "client.errors.ignoreWrongJobDataErrors", msg))
                                throw new InvalidUserDataException(msg);
                        }
                    }
                } else if (timeunit.equals("M")) {
                    String st = res.getProperty("turnusmonths");

                    if (st != null) {
                        String value2 = new DecimalFormat("00").format(Integer.parseInt(value));

                        if (!st.equals("00") && !twoDigitValueInList(value2, st)) {
                            String msg = HBCIUtilsInternal.getLocMsg("EXCMSG_INV_TURNUS", value);
                            if (!HBCIUtilsInternal.ignoreError(getMainPassport(), "client.errors.ignoreWrongJobDataErrors", msg))
                                throw new InvalidUserDataException(msg);
                        }
                    }
                }
            }
        } else if (paramName.equals("execday")) {
            String timeunit = getLowlevelParams().getProperty(getName() + ".DauerDetails.timeunit");

            if (timeunit != null) {
                if (timeunit.equals("W")) {
                    String st = res.getProperty("daysperweek");

                    if (st != null && !st.equals("0") && !st.contains(value)) {
                        String msg = HBCIUtilsInternal.getLocMsg("EXCMSG_INV_EXECDAY", value);
                        if (!HBCIUtilsInternal.ignoreError(getMainPassport(), "client.errors.ignoreWrongJobDataErrors", msg))
                            throw new InvalidUserDataException(msg);
                    }
                } else if (timeunit.equals("M")) {
                    String st = res.getProperty("dayspermonth");

                    if (st != null) {
                        String value2 = new DecimalFormat("00").format(Integer.parseInt(value));

                        if (!st.equals("00") && !twoDigitValueInList(value2, st)) {
                            String msg = HBCIUtilsInternal.getLocMsg("EXCMSG_INV_EXECDAY", value);
                            if (!HBCIUtilsInternal.ignoreError(getMainPassport(), "client.errors.ignoreWrongJobDataErrors", msg))
                                throw new InvalidUserDataException(msg);
                        }
                    }
                }
            }
        }

        super.setParam(paramName, value);
    }

}
