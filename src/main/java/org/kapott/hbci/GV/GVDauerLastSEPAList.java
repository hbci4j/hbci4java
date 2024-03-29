/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.kapott.hbci.GV;

import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRDauerLastList;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Geschaeftsvorfall zum Abrufen der Liste der SEPA-Dauerlastschriften.
 */
public final class GVDauerLastSEPAList extends AbstractSEPAGV {
    private final static SepaVersion DEFAULT = SepaVersion.PAIN_008_001_02;

    /**
     * @see AbstractSEPAGV#getDefaultPainVersion()
     */
    @Override
    protected SepaVersion getDefaultPainVersion() {
        return DEFAULT;
    }

    /**
     * @see AbstractSEPAGV#getPainType()
     */
    @Override
    protected Type getPainType() {
        return Type.PAIN_008;
    }

    public static String getLowlevelName() {
        return "DauerLastSEPAList";
    }

    /**
     * ct.
     *
     * @param handler
     */
    public GVDauerLastSEPAList(HBCIHandler handler) {
        super(handler, getLowlevelName(), new GVRDauerLastList());

        addConstraint("src.bic", "My.bic", null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);

        if (this.canNationalAcc(handler)) // nationale Bankverbindung mitschicken, wenn erlaubt
        {
            addConstraint("src.country", "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz", "My.KIK.blz", "", LogFilter.FILTER_MOST);
            addConstraint("src.number", "My.number", "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber", "My.subnumber", "", LogFilter.FILTER_MOST);
        }

        addConstraint("_sepadescriptor", "sepadescr", this.getPainVersion().getURN(), LogFilter.FILTER_NONE);
        addConstraint("orderid", "orderid", "", LogFilter.FILTER_NONE);
        addConstraint("maxentries", "maxentries", "", LogFilter.FILTER_NONE);
    }

    /**
     * @see HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed() {
        return true;
    }

    /**
     * @see HBCIJobImpl#extractResults(HBCIMsgStatus, String, int)
     */
    @Override
    protected void extractResults(HBCIMsgStatus msgstatus, String header, int idx) {
        Properties result = msgstatus.getData();
        GVRDauerLastList.Dauer entry = new GVRDauerLastList.Dauer();

        HBCIUtils.log("parsing SEPA standing orders from msg data [size: " + result.size() + "]", HBCIUtils.LOG_DEBUG);

        entry.my = new Konto();
        entry.my.country = result.getProperty(header + ".My.KIK.country");
        entry.my.blz = result.getProperty(header + ".My.KIK.blz");
        entry.my.number = result.getProperty(header + ".My.number");
        entry.my.subnumber = result.getProperty(header + ".My.subnumber");
        entry.my.iban = result.getProperty(header + ".My.iban");
        entry.my.bic = result.getProperty(header + ".My.bic");
        getMainPassport().fillAccountInfo(entry.my);

        entry.other = new Konto();

        final String sepadescr = result.getProperty(header + ".sepadescr");
        final String pain = result.getProperty(header + ".sepapain");
        final SepaVersion version = SepaVersion.choose(sepadescr, pain);

        ISEPAParser<List<Properties>> parser = SEPAParserFactory.get(version);
        List<Properties> sepaResults = new ArrayList<Properties>();
        try {
            // Encoding siehe GVTermUebSEPAList
            HBCIUtils.log("  parsing sepa data: " + pain, HBCIUtils.LOG_DEBUG2);
            parser.parse(new ByteArrayInputStream(pain.getBytes(Comm.ENCODING)), sepaResults);
            HBCIUtils.log("  parsed sepa data, entries: " + sepaResults.size(), HBCIUtils.LOG_INFO);
        } catch (Exception e) {
            HBCIUtils.log("  unable to parse sepa data: " + e.getMessage(), HBCIUtils.LOG_ERR);
            throw new HBCI_Exception("Error parsing SEPA pain document", e);
        }

        if (sepaResults.isEmpty()) {
            HBCIUtils.log("  found no sepa data", HBCIUtils.LOG_WARN);
            return;
        }
        Properties sepaResult = sepaResults.get(0);
        entry.other.iban = sepaResult.getProperty("dst.iban");
        entry.other.bic = sepaResult.getProperty("dst.bic");
        entry.other.name = sepaResult.getProperty("dst.name");
        entry.pmtinfid = sepaResult.getProperty("pmtinfid");
        entry.type = sepaResult.getProperty("type");
        entry.sequencetype = sepaResult.getProperty("sequencetype");
        entry.creditorid = sepaResult.getProperty("creditorid");
        entry.mandateid = sepaResult.getProperty("mandateid");
        entry.manddateofsig = HBCIUtils.string2DateISO(sepaResult.getProperty("manddateofsig"));
        entry.endtoendid = sepaResult.getProperty("endtoendid");

        entry.value = new Value(
                sepaResult.getProperty("value"),
                sepaResult.getProperty("curr"));
        entry.addUsage(sepaResult.getProperty("usage"));

        String st;
        entry.orderid = result.getProperty(header + ".orderid");

        entry.firstdate = HBCIUtils.string2DateISO(result.getProperty(header + ".DauerDetails.firstdate"));
        entry.timeunit = result.getProperty(header + ".DauerDetails.timeunit");
        entry.turnus = Integer.parseInt(result.getProperty(header + ".DauerDetails.turnus"));
        entry.execday = Integer.parseInt(result.getProperty(header + ".DauerDetails.execday"));
        if ((st = result.getProperty(header + ".DauerDetails.lastdate")) != null)
            entry.lastdate = HBCIUtils.string2DateISO(st);

        entry.aus_available = result.getProperty(header + ".Aussetzung.annual") != null;
        if (entry.aus_available) {
            entry.aus_annual = result.getProperty(header + ".Aussetzung.annual").equals("J");
            if ((st = result.getProperty(header + ".Aussetzung.startdate")) != null)
                entry.aus_start = HBCIUtils.string2DateISO(st);
            if ((st = result.getProperty(header + ".Aussetzung.enddate")) != null)
                entry.aus_end = HBCIUtils.string2DateISO(st);
            entry.aus_breakcount = result.getProperty(header + ".Aussetzung.number");
            if ((st = result.getProperty(header + ".Aussetzung.newvalue.value")) != null) {
                entry.aus_newvalue = new Value(
                        st,
                        result.getProperty(header + ".Aussetzung.newvalue.curr"));
            }
        }

        entry.can_change = result.getProperty(header + ".canchange") == null || result.getProperty(header + ".canchange").equals("J");
        entry.can_skip = result.getProperty(header + ".canskip") == null || result.getProperty(header + ".canskip").equals("J");
        entry.can_delete = result.getProperty(header + ".candel") == null || result.getProperty(header + ".candel").equals("J");

        ((GVRDauerLastList) (jobResult)).addEntry(entry);

        if (entry.orderid != null && entry.orderid.length() != 0) {
            Properties p2 = new Properties();

            for (Enumeration<?> e = result.propertyNames(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();

                if (key.startsWith(header + ".") &&
                        !key.startsWith(header + ".SegHead.") &&
                        !key.endsWith(".orderid")) {
                    p2.setProperty(key.substring(header.length() + 1), result.getProperty(key));
                }
            }

            getMainPassport().setPersistentData("dauer_" + entry.orderid, p2);
        }
    }

    public String getPainJobName() {
        return "LastSEPA";
    }

}
