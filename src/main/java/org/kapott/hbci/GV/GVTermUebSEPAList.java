
/*  $Id: GVDauerList.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.GV;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRTermUebList;
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

public final class GVTermUebSEPAList extends AbstractSEPAGV
{
    private final static SepaVersion DEFAULT = SepaVersion.PAIN_001_001_02;

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
        return Type.PAIN_001;
    }
    
    public static String getLowlevelName()
    {
        return "TermUebSEPAList";
    }
    
    public GVTermUebSEPAList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTermUebList());

        addConstraint("src.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);

        if (this.canNationalAcc(handler)) // nationale Bankverbindung mitschicken, wenn erlaubt
        {
            addConstraint("src.country",  "My.KIK.country", "", LogFilter.FILTER_NONE);
            addConstraint("src.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
            addConstraint("src.number",   "My.number",      "", LogFilter.FILTER_IDS);
            addConstraint("src.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
        }

        addConstraint("_sepadescriptor", "sepadescr", this.getPainVersion().getURN(), LogFilter.FILTER_NONE);
        addConstraint("startdate","startdate","", LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }

    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRTermUebList.Entry entry=new GVRTermUebList.Entry();

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
            // Wir duerfen das hier nicht als UTF-8 interpretieren (das war vorher hier das Fall),
            // auch dann nicht, wenn wir genau wissen, dass das XML mit "<?xml version="1.0" encoding="UTF-8"?>"
            // beginnt. Stattdessen muessen wir den selben Zeichensatz nehmen, der bei der Byte->String Conversion
            // beim Empfamg der rohen HBCI-Daten ueber TCP verwendet wurde. Siehe CommStandard/CommPinTan.
            // Die eigentliche Codierung der XML-Datei spielt hier keine Rolle - wichtig ist, dass die
            // Rueckwandlung String->Bytes (in pain.getBytes) den selben Zeichensatz verwendet wie beim Empfang der
            // Daten vom Server. Nur so ist sichergestellt, dass die Bytes wieder genauso aussehen, wie sie
            // beim Empfang vom Server kamen, wenn der XML-Parser sie kriegt. Er macht dann die Conversion Byte->String
            // korrekt basierend auf dem im XML angegebenen Header.
            // Siehe auch AbstractSEPAGenerator#marshal
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
        entry.addUsage(sepaResult.getProperty("usage"));
 
        entry.orderid=result.getProperty(header+".orderid");
        entry.date = HBCIUtils.string2DateISO(sepaResult.getProperty("date"));
        
        entry.can_change = result.getProperty(header+".canchange")==null || result.getProperty(header+".canchange").equals("J");
        entry.can_delete = result.getProperty(header+".candel")==null || result.getProperty(header+".candel").equals("J");

        ((GVRTermUebList)(jobResult)).addEntry(entry);

        if (entry.orderid!=null && entry.orderid.length()!=0) {
            Properties p2=new Properties();

            for (Enumeration e=result.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();

                if (key.startsWith(header+".") && 
                    !key.startsWith(header+".SegHead.") &&
                    !key.endsWith(".orderid")) {
                    p2.setProperty(key.substring(header.length()+1),
                                   result.getProperty(key));
                }
            }

            getMainPassport().setPersistentData("termueb_"+entry.orderid,p2);
        }
    }
    
    public String getPainJobName() {
        return "UebSEPA";
    }
    
}
