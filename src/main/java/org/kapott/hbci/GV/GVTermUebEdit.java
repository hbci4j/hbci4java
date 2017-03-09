
/*  $Id: GVTermUebEdit.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRTermUebEdit;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

public final class GVTermUebEdit
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "TermUebEdit";
    }
    
    public GVTermUebEdit(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTermUebEdit());
        
        addConstraint("src.country","My.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("src.blz","My.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("src.number","My.number",null, LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("dst.country","Other.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("dst.blz","Other.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("dst.number","Other.number","", LogFilter.FILTER_IDS);
        addConstraint("dst.subnumber","Other.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("btg.value","BTG.value",null, LogFilter.FILTER_MOST);
        addConstraint("btg.curr","BTG.curr",null, LogFilter.FILTER_NONE);
        addConstraint("name","name",null, LogFilter.FILTER_IDS);
        addConstraint("date","date",null, LogFilter.FILTER_NONE);
        addConstraint("orderid","id",null, LogFilter.FILTER_NONE);

        addConstraint("name2","name2","", LogFilter.FILTER_IDS);
        addConstraint("key","key","51", LogFilter.FILTER_NONE);

        Properties parameters=getJobRestrictions();
        int        maxusage=Integer.parseInt(parameters.getProperty("maxusage"));

        for (int i=0;i<maxusage;i++) {
            String name=HBCIUtilsInternal.withCounter("usage",i);
            addConstraint(name,"usage."+name,"", LogFilter.FILTER_MOST);
        }
    }

    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        String orderid=result.getProperty(header+".orderid");
        
        ((GVRTermUebEdit)(jobResult)).setOrderId(orderid);
        ((GVRTermUebEdit)(jobResult)).setOrderIdOld(result.getProperty(header+".orderidold"));

        if (orderid!=null && orderid.length()!=0) {
            Properties p=getLowlevelParams();
            Properties p2=new Properties();

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                if (!key.endsWith(".id")) {
                    p2.setProperty(key.substring(key.indexOf(".")+1),
                                   p.getProperty(key));
                }
            }

            getMainPassport().setPersistentData("termueb_"+orderid,p2);
        }
    }
    
    public void setParam(String paramName,String value)
    {
        super.setParam(paramName,value);

        if (paramName.equals("orderid")) {
            Properties p=(Properties)getMainPassport().getPersistentData("termueb_"+value);
            if (p==null) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_NOSUCHSCHEDTRANS",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
                p=new Properties();
            }

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                String key2=getName()+"."+key;
                
                if (getLowlevelParams().getProperty(key2)==null) {
                    setLowlevelParam(key2,
                                     p.getProperty(key));
                }
            }
        }
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("src");
        checkAccountCRC("dst");
    }
}
