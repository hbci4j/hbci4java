
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

import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRDauerList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public final class GVDauerList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "DauerList";
    }
    
    public GVDauerList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRDauerList());

        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("orderid","orderid","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }

    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRDauerList.Dauer entry=new GVRDauerList.Dauer();

        entry.my=new Konto();
        entry.my.country=result.getProperty(header+".My.KIK.country");
        entry.my.blz=result.getProperty(header+".My.KIK.blz");
        entry.my.number=result.getProperty(header+".My.number");
        entry.my.subnumber=result.getProperty(header+".My.subnumber");
        getMainPassport().fillAccountInfo(entry.my);

        entry.other=new Konto();
        entry.other.country=result.getProperty(header+".Other.KIK.country");
        entry.other.blz=result.getProperty(header+".Other.KIK.blz");
        entry.other.number=result.getProperty(header+".Other.number");
        entry.other.subnumber=result.getProperty(header+".Other.subnumber");
        entry.other.name=result.getProperty(header+".name");
        entry.other.name2=result.getProperty(header+".name2");

        entry.value=new Value(
            result.getProperty(header+".BTG.value"),
            result.getProperty(header+".BTG.curr"));
        entry.key=result.getProperty(header+".key");
        entry.addkey=result.getProperty(header+".addkey");

        for (int i=0;;i++) {
            String usage=result.getProperty(header+".usage."+HBCIUtilsInternal.withCounter("usage",i));
            if (usage==null)
                break;
            entry.addUsage(usage);
        }

        String st;
        if ((st=result.getProperty(header+".date"))!=null)
            entry.nextdate=HBCIUtils.string2DateISO(st);

        entry.orderid=result.getProperty(header+".orderid");

        entry.firstdate=HBCIUtils.string2DateISO(result.getProperty(header+".DauerDetails.firstdate"));
        entry.timeunit=result.getProperty(header+".DauerDetails.timeunit");
        entry.turnus=Integer.parseInt(result.getProperty(header+".DauerDetails.turnus"));
        entry.execday=Integer.parseInt(result.getProperty(header+".DauerDetails.execday"));
        if ((st=result.getProperty(header+".DauerDetails.lastdate"))!=null)
            entry.lastdate=HBCIUtils.string2DateISO(st);

        entry.aus_available=result.getProperty(header+".Aussetzung.annual")!=null;
        if (entry.aus_available) {
            entry.aus_annual=result.getProperty(header+".Aussetzung.annual").equals("J");
            if ((st=result.getProperty(header+".Aussetzung.startdate"))!=null)
                entry.aus_start=HBCIUtils.string2DateISO(st);
            if ((st=result.getProperty(header+".Aussetzung.enddate"))!=null)
                entry.aus_end=HBCIUtils.string2DateISO(st);
            entry.aus_breakcount=result.getProperty(header+".Aussetzung.number");
            if ((st=result.getProperty(header+".Aussetzung.newvalue.value"))!=null) {
                entry.aus_newvalue=new Value(
                    st,
                    result.getProperty(header+".Aussetzung.newvalue.curr"));
            }
        }

        ((GVRDauerList)(jobResult)).addEntry(entry);

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

            getMainPassport().setPersistentData("dauer_"+entry.orderid,p2);
        }
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
