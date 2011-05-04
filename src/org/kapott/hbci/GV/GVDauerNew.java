
/*  $Id: GVDauerNew.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRDauerNew;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

public final class GVDauerNew
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "DauerNew";
    }
    
    public GVDauerNew(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRDauerNew());

        addConstraint("src.number","My.number",null, LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("dst.blz","Other.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("dst.number","Other.number",null, LogFilter.FILTER_IDS);
        addConstraint("dst.subnumber","Other.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("btg.value","BTG.value",null, LogFilter.FILTER_MOST);
        addConstraint("btg.curr","BTG.curr",null, LogFilter.FILTER_NONE);
        addConstraint("name","name",null, LogFilter.FILTER_IDS);
        addConstraint("firstdate","DauerDetails.firstdate",null, LogFilter.FILTER_NONE);
        addConstraint("timeunit","DauerDetails.timeunit",null, LogFilter.FILTER_NONE);
        addConstraint("turnus","DauerDetails.turnus",null, LogFilter.FILTER_NONE);
        addConstraint("execday","DauerDetails.execday",null, LogFilter.FILTER_NONE);

        addConstraint("src.blz","My.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("src.country","My.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("dst.country","Other.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("name2","name2","", LogFilter.FILTER_IDS);
        addConstraint("lastdate","DauerDetails.lastdate","", LogFilter.FILTER_NONE);
        addConstraint("key","key","52", LogFilter.FILTER_NONE);
        
        // TODO: aussetzung fehlt
        // TODO: addkey fehlt

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
        ((GVRDauerNew)(jobResult)).setOrderId(orderid);

        if (orderid!=null && orderid.length()!=0) {
            Properties p=getLowlevelParams();
            Properties p2=new Properties();

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                p2.setProperty(key.substring(key.indexOf(".")+1),
                               p.getProperty(key));
            }

            getMainPassport().setPersistentData("dauer_"+orderid,p2);
        }
    }
    
    public void setParam(String paramName,String value)
    {
        Properties res=getJobRestrictions();
        
        if (paramName.equals("timeunit")) {
            if (!(value.equals("W") || value.equals("M"))) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_TIMEUNIT",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
        } else if (paramName.equals("turnus")) {
            String timeunit=getLowlevelParams().getProperty(getName()+".DauerDetails.timeunit");
            
            if (timeunit!=null) {
                if (timeunit.equals("W")) {
                    String st=res.getProperty("turnusweeks");
                    
                    if (st!=null) {
                        String value2=new DecimalFormat("00").format(Integer.parseInt(value));

                        if (!st.equals("00") && !twoDigitValueInList(value2,st)) {
                            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_TURNUS",value);
                            if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                                throw new InvalidUserDataException(msg);
                        }
                    }
                } else if (timeunit.equals("M")) {
                    String st=res.getProperty("turnusmonths");

                    if (st!=null) {
                        String value2=new DecimalFormat("00").format(Integer.parseInt(value));

                        if (!st.equals("00") && !twoDigitValueInList(value2,st)) {
                            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_TURNUS",value);
                            if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                                throw new InvalidUserDataException(msg);
                        }
                    }
                }
            }
        } else if (paramName.equals("execday")) {
            String timeunit=getLowlevelParams().getProperty(getName()+".DauerDetails.timeunit");

            if (timeunit!=null) {
                if (timeunit.equals("W")) {
                    String st=res.getProperty("daysperweek");

                    if (st!=null && !st.equals("0") && st.indexOf(value)==-1) {
                        String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_EXECDAY",value);
                        if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                            throw new InvalidUserDataException(msg);
                    }
                } else if (timeunit.equals("M")) {
                    String st=res.getProperty("dayspermonth");

                    if (st!=null) {
                        String value2=new DecimalFormat("00").format(Integer.parseInt(value));

                        if (!st.equals("00") && !twoDigitValueInList(value2,st)) {
                            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_EXECDAY",value);
                            if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                                throw new InvalidUserDataException(msg);
                        }
                    }
                }
            }
        } else if (paramName.equals("key")) {
            boolean atLeastOne=false;
            boolean found=false;
            
            for (int i=0;;i++) {
                String st=res.getProperty(HBCIUtilsInternal.withCounter("textkey",i));
                
                if (st==null)
                    break;
                
                atLeastOne=true;
                
                if (st.equals(value)) {
                    found=true;
                    break;
                }
            }
            
            if (atLeastOne && !found) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_KEY",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
        }
        
        super.setParam(paramName,value);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("src");
        checkAccountCRC("dst");
    }
}
