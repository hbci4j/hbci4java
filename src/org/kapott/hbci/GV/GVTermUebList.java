
/*  $Id: GVTermUebList.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRTermUebList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public final class GVTermUebList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "TermUebList";
    }
    
    public GVTermUebList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTermUebList());

        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("startdate","startdate","", LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRTermUebList.Entry entry=new GVRTermUebList.Entry();
        
        entry.my=new Konto();
        entry.my.blz=result.getProperty(header+".My.KIK.blz");
        entry.my.country=result.getProperty(header+".My.KIK.country");
        entry.my.number=result.getProperty(header+".My.number");
        entry.my.subnumber=result.getProperty(header+".My.subnumber");
        getMainPassport().fillAccountInfo(entry.my);

        entry.other=new Konto();
        entry.other.blz=result.getProperty(header+".Other.KIK.blz");
        entry.other.country=result.getProperty(header+".Other.KIK.country");
        entry.other.number=result.getProperty(header+".Other.number");
        entry.other.subnumber=result.getProperty(header+".Other.subnumber");
        entry.other.name=result.getProperty(header+".name");
        entry.other.name2=result.getProperty(header+".name2");
        getMainPassport().fillAccountInfo(entry.other);
        
        entry.key=result.getProperty(header+".key");
        entry.addkey=result.getProperty(header+".addkey");
        entry.orderid=result.getProperty(header+".id");
        entry.date=HBCIUtils.string2DateISO(result.getProperty(header+".date"));
        
        entry.value=new Value(
            result.getProperty(header+".BTG.value"),
            result.getProperty(header+".BTG.curr"));
        
        for (int i=0;;i++) {
            String usage=result.getProperty(HBCIUtilsInternal.withCounter(header+".usage.usage",i));
            if (usage==null) {
                break;
            }
            entry.addUsage(usage);
        }
        
        ((GVRTermUebList)jobResult).addEntry(entry);

        if (entry.orderid!=null && entry.orderid.length()!=0) {
            Properties p2=new Properties();

            for (Enumeration e=result.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                
                if (key.startsWith(header+".") && 
                    !key.startsWith(header+".SegHead.") &&
                    !key.endsWith(".id")) {
                    p2.setProperty(key.substring(header.length()+1),
                                   result.getProperty(key));
                }
            }

            getMainPassport().setPersistentData("termueb_"+entry.orderid,p2);
        }
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
