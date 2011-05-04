
/*  $Id: GVDauerDel.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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
        
        // TODO: daten fuer aussetzung fehlen
        // TODO: addkey fehlt

        // Properties parameters=getJobRestrictions();
        // int        maxusage=Integer.parseInt(parameters.getProperty("maxusage"));
        //
        // TODO this is a dirty hack because we need the "maxusage" job restriction
        // from GVDauerNew here, but we have no chance to access this parameter
        // from here. The design changes of the next HBCI4Java version may solve
        // this problem.
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
            
            // TODO: minpretime und maxpretime auswerten
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
