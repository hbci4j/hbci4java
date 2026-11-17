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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRTermLastSEPAList;
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

/**
 * Geschaeftsvorfall zum Abrufen des Bestandes terminierter SEPA-Einzellastschriften (HKDBS).
 */
public final class GVTermLastSEPAList extends AbstractSEPAGV
{
    private final static SepaVersion DEFAULT = SepaVersion.PAIN_008_001_01;

    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getDefaultPainVersion()
     */
    @Override
    protected SepaVersion getDefaultPainVersion()
    {
        return DEFAULT;
    }

    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainType()
     */
    @Override
    protected Type getPainType()
    {
        return Type.PAIN_008;
    }

    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName()
    {
        return "TermLastSEPAList";
    }

    /**
     * ct.
     * @param handler
     */
    public GVTermLastSEPAList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTermLastSEPAList());

        addConstraint("src.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);

        if (this.canNationalAcc(handler)) // nationale Bankverbindung mitschicken, wenn erlaubt
        {
            addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
            addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
        }

        addConstraint("_sepadescriptor", "formats.format", this.getPainVersion().getURN(), LogFilter.FILTER_NONE);
        addConstraint("startdate","startdate","", LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    @Override
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRTermLastSEPAList.Entry entry=new GVRTermLastSEPAList.Entry();

        entry.my=new Konto();
        entry.my.country=result.getProperty(header+".My.KIK.country");
        entry.my.blz=result.getProperty(header+".My.KIK.blz");
        entry.my.number=result.getProperty(header+".My.number");
        entry.my.subnumber=result.getProperty(header+".My.subnumber");
        entry.my.iban = result.getProperty(header+".My.iban");
        entry.my.bic = result.getProperty(header+".My.bic");
        getMainPassport().fillAccountInfo(entry.my);

        entry.other=new Konto();

        final String sepadescr    = result.getProperty(header+".sepadescr");
        final String pain         = result.getProperty(header+".sepapain");
        final SepaVersion version = SepaVersion.choose(sepadescr,pain);

        ISEPAParser<List<Properties>> parser = SEPAParserFactory.get(version);
        List<Properties> sepaResults = new ArrayList<Properties>();
        try
        {
            // Encoding siehe GVTermUebSEPAList
            parser.parse(new ByteArrayInputStream(pain.getBytes(Comm.ENCODING)),sepaResults);
        }
        catch(Exception e)
        {
            throw new HBCI_Exception("Error parsing SEPA pain document",e);
        }

        if(sepaResults.isEmpty()) return;
        Properties sepaResult = sepaResults.get(0);
        entry.other.iban = sepaResult.getProperty("dst.iban");
        entry.other.bic = sepaResult.getProperty("dst.bic");
        entry.other.name = sepaResult.getProperty("dst.name");
        entry.value=new Value(
                        sepaResult.getProperty("value"),
                        sepaResult.getProperty("curr"));
        if (sepaResult.getProperty("usage")!=null)
            entry.addUsage(sepaResult.getProperty("usage"));

        entry.type = sepaResult.getProperty("type");
        entry.sequencetype = sepaResult.getProperty("sequencetype");
        entry.creditorid = sepaResult.getProperty("creditorid");
        entry.mandateid = sepaResult.getProperty("mandateid");
        entry.endtoendid = sepaResult.getProperty("endtoendid");
        entry.pmtinfid = sepaResult.getProperty("pmtinfid");

        String st=sepaResult.getProperty("manddateofsig");
        if (st!=null)
            entry.manddateofsig = HBCIUtils.string2DateISO(st);
        st=sepaResult.getProperty("targetdate");
        if (st!=null)
            entry.date = HBCIUtils.string2DateISO(st);

        entry.orderid=result.getProperty(header+".orderid");
        entry.sepadescr=sepadescr;

        entry.can_change = result.getProperty(header+".canchange")==null || result.getProperty(header+".canchange").equals("J");
        entry.can_delete = result.getProperty(header+".candel")==null || result.getProperty(header+".candel").equals("J");
        entry.ccode = result.getProperty(header+".ccode");
        entry.orderstatus = result.getProperty(header+".orderstatus");

        ((GVRTermLastSEPAList)(jobResult)).addEntry(entry);
    }

    /**
     * @see org.kapott.hbci.GV.AbstractSEPAGV#getPainJobName()
     */
    @Override
    public String getPainJobName()
    {
        return "LastSEPA";
    }
}
