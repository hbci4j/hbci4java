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

import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;

public final class GVDauerDel
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "DauerDel";
    }
    
    public GVDauerDel(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new HBCIJobResultImpl());
        
        addConstraint("src.number","My.number","", LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("dst.blz","Other.KIK.blz","", LogFilter.FILTER_MOST);
        addConstraint("dst.number","Other.number","", LogFilter.FILTER_IDS);
        addConstraint("dst.subnumber","Other.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("btg.value","BTG.value","", LogFilter.FILTER_MOST);
        addConstraint("btg.curr","BTG.curr","", LogFilter.FILTER_NONE);
        addConstraint("name","name","", LogFilter.FILTER_IDS);
        addConstraint("firstdate","DauerDetails.firstdate","", LogFilter.FILTER_NONE);
        addConstraint("timeunit","DauerDetails.timeunit","", LogFilter.FILTER_NONE);
        addConstraint("turnus","DauerDetails.turnus","", LogFilter.FILTER_NONE);
        addConstraint("execday","DauerDetails.execday","", LogFilter.FILTER_NONE);

        addConstraint("src.blz","My.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("src.country","My.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("dst.country","Other.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("name2","name2","", LogFilter.FILTER_IDS);
        addConstraint("key","key","52", LogFilter.FILTER_NONE);
        addConstraint("date","date","", LogFilter.FILTER_NONE);
        addConstraint("orderid","orderid","", LogFilter.FILTER_NONE);
        addConstraint("lastdate","DauerDetails.lastdate","", LogFilter.FILTER_NONE);
        
        int maxusage=99;

        for (int i=0;i<maxusage;i++) {
            String name=HBCIUtilsInternal.withCounter("usage",i);
            addConstraint(name,"usage."+name,"", LogFilter.FILTER_MOST);
        }
    }
    
    public void setParam(String paramName,String value)
    {
        if (paramName.equals("date")) {
            Properties res=getJobRestrictions();
            String st_cantermdel=res.getProperty("cantermdel");
            
            if (st_cantermdel!=null && st_cantermdel.equals("N")) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_SCHEDDELSTANDORDUNAVAIL");
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
            
        } else if (paramName.equals("orderid")) {
            Properties p=(Properties)getMainPassport().getPersistentData("dauer_"+value);
            if (p!=null && p.size()!=0) {
                for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                    String key=(String)e.nextElement();
                    
                    if (!key.equals("date") &&
                            !key.startsWith("Aussetzung.")) {
                        setLowlevelParam(getName()+"."+key,
                            p.getProperty(key));
                    }
                }
            }
        }
        
        super.setParam(paramName,value);
    }
}
