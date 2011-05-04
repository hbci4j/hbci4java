
/*  $Id: GVDauerEdit.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRDauerEdit;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

public final class GVDauerEdit
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "DauerEdit";
    }
    
    public GVDauerEdit(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRDauerEdit());

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
        addConstraint("orderid","orderid",null, LogFilter.FILTER_NONE);

        addConstraint("src.blz","My.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("src.country","My.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("dst.country","Other.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("name2","name2","", LogFilter.FILTER_IDS);
        addConstraint("key","key","52", LogFilter.FILTER_NONE);
        addConstraint("date","date","", LogFilter.FILTER_NONE);
        addConstraint("lastdate","DauerDetails.lastdate","", LogFilter.FILTER_NONE);
        
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
        
        ((GVRDauerEdit)(jobResult)).setOrderId(orderid);
        ((GVRDauerEdit)(jobResult)).setOrderIdOld(result.getProperty(header+".orderidold"));

        if (orderid!=null && orderid.length()!=0) {
            Properties p=getLowlevelParams();
            Properties p2=new Properties();

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                if (!key.endsWith(".orderid")) {
                    p2.setProperty(key.substring(key.indexOf(".")+1),
                                   p.getProperty(key));
                }
            }

            getMainPassport().setPersistentData("dauer_"+orderid,p2);
        }
    }
    
    public void setParam(String paramName,String value)
    {
        Properties res=getJobRestrictions();
        
        if (paramName.equals("date")) {
            String st=res.getProperty("numtermchanges");
            if (st!=null && Integer.parseInt(st)==0) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_SCHEDMODSTANDORDUNAVAIL");
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
            // TODO: numtermchanges richtig auswerten
        } else if (paramName.equals("timeunit")) {
            if (!(value.equals("W")||value.equals("M"))) {
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

            if (atLeastOne&&!found) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_KEY",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
        } else if (paramName.equals("orderid")) {
            Properties p=(Properties)getMainPassport().getPersistentData("dauer_"+value);
            if (p!=null) {
                for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                    String key=(String)e.nextElement();
                    String key2=getName()+"."+key;
                    
                    if (!key.equals("date") && 
                            !key.startsWith("Aussetzung.") &&
                            getLowlevelParams().getProperty(key2)==null) {
                        setLowlevelParam(key2,
                            p.getProperty(key));
                    }
                }
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

    // TODO: this is disabled for now because the hbci specification is inconsistent concerning this
    /* public void verifyConstraints()
    {
        super.verifyConstraints();

        if (das_ist_eine_terminierte_ueberweisung) {
        Properties newParams=getParams();
        Properties oldParams=(Properties)getPassport().getPersistentData("dauer_"+newParams.getProperty(getName()+".orderid"));

        String st1;
        String st2;
        String key;

        Properties res=getJobRestrictions();

        if (res.getProperty("recktoeditable").equals("N")) {
            if ((st1=newParams.getProperty(getName()+".Other.KIK.country"))!=null &&
                (st2=oldParams.getProperty("Other.KIK.country"))!=null &&
                !st1.equals(st2) ||
                (st1=newParams.getProperty(getName()+".Other.KIK.blz"))!=null &&
                (st2=oldParams.getProperty("Other.KIK.blz"))!=null &&
                !st1.equals(st2) ||
                (st1=newParams.getProperty(getName()+".Other.number"))!=null &&
                (st2=oldParams.getProperty("Other.number"))!=null &&
                !st1.equals(st2)) {
                throw new HBCI_Exception("*** changing of destination account not allowed");
            }
        }

        if (res.getProperty("recnameeditable").equals("N")) {
            if ((st1=newParams.getProperty(getName()+".name"))!=null &&
                (st2=oldParams.getProperty("name"))!=null &&
                !st1.equals(st2) ||
                (st1=newParams.getProperty(getName()+".name2"))!=null &&
                (st2=oldParams.getProperty("name2"))!=null &&
                !st1.equals(st2)) {
                throw new HBCI_Exception("*** can not edit recipient name"); 
            }
        }

        if (res.getProperty("valueeditable").equals("N")) {
            if ((st1=newParams.getProperty(getName()+".BTG.value"))!=null &&
                (st2=oldParams.getProperty("BTG.value"))!=null &&
                Float.parseFloat(st1)!=Float.parseFloat(st2) ||
                (st1=newParams.getProperty(getName()+".BTG.curr"))!=null &&
                (st2=oldParams.getProperty("BTG.curr"))!=null &&
                !st1.equals(st2)) {
                throw new HBCI_Exception("*** changing value is not allowed");
            }
        }

        if (res.getProperty("keyeditable").equals("N")) {
            if ((st1=newParams.getProperty(getName()+".key"))!=null &&
                (st2=oldParams.getProperty("key"))!=null &&
                !st1.equals(st2) ||
                (st1=newParams.getProperty(getName()+".addkey"))!=null &&
                (st2=oldParams.getProperty("addkey"))!=null &&
                !st1.equals(st2)) {
                throw new HBCI_Exception("*** changing key is not allowed");
            }
        }

        if (res.getProperty("usageeditable").equals("N")) {
            boolean equal=true;

            for (int i=0;;i++) {
                String h=HBCIUtils.withCounter("usage.usage",i);
                String uo=oldParams.getProperty(h);
                String un=newParams.getProperty(getName()+"."+h);
                if (uo==null) {
                    if (un!=null) {
                        equal=false;
                    }
                    break;
                }
                if (un==null||!un.equals(uo)) {
                    equal=false;
                    break;
                }
            }

            if (!equal) {
                throw new HBCI_Exception("*** changing usage not allowed");
            }
        }

        if (res.getProperty("firstexeceditable").equals("N")) {
            if (!HBCIUtils.string2Date(newParams.getProperty((key=getName()+".DauerDetails.firstdate"))).equals(HBCIUtils.string2Date(oldParams.getProperty("DauerDetails.firstdate")))) {
                throw new HBCI_Exception("*** changing firstdate not allowed");
            }
        }

        if (res.getProperty("timeuniteditable").equals("N")) {
            if (!newParams.getProperty((key=getName()+".DauerDetails.timeunit")).equals(oldParams.getProperty("DauerDetails.timeunit"))) {
                throw new HBCI_Exception("*** changing timeunit not allowed");
            }
        }

        if (res.getProperty("turnuseditable").equals("N")) {
            if (Integer.parseInt(newParams.getProperty((key=getName()+".DauerDetails.turnus")))!=Integer.parseInt(oldParams.getProperty("DauerDetails.turnus"))) {
                throw new HBCI_Exception("*** changing turnus not allowed");
            }
        }

        if (res.getProperty("execdayeditable").equals("N")) {
            if (Integer.parseInt(newParams.getProperty((key=getName()+".DauerDetails.execday")))!=Integer.parseInt(oldParams.getProperty("DauerDetails.execday"))) {
                throw new HBCI_Exception("*** changing execday not allowed");
            }
        }

        if (res.getProperty("lastexeceditable").equals("N")) {
            if ((st1=newParams.getProperty((key=getName()+".DauerDetails.lastdate")))!=null &&
                (st2=oldParams.getProperty("DauerDetails.lastdate"))!=null &&
                !HBCIUtils.string2Date(st1).equals(HBCIUtils.string2Date(st2))) {
                throw new HBCI_Exception("*** chaning lastdate not allowed");
            }
        }

        if (Integer.parseInt(res.getProperty("numtermchanges"))>1) {
            if (res.getProperty("recktoeditable").equals("J")) {
                if ((st1=newParams.getProperty(getName()+".Other.KIK.country"))!=null &&
                    (st2=oldParams.getProperty("Other.KIK.country"))!=null &&
                    st1.equals(st2) &&
                    (st1=newParams.getProperty(getName()+".Other.KIK.blz"))!=null &&
                    (st2=oldParams.getProperty("Other.KIK.blz"))!=null &&
                    st1.equals(st2) &&
                    (st1=newParams.getProperty(getName()+".Other.number"))!=null &&
                    (st2=oldParams.getProperty("Other.number"))!=null &&
                    st1.equals(st2)) {
                    newParams.setProperty(getName()+".Other.KIK.country","");
                    newParams.setProperty(getName()+".Other.KIK.blz","");
                    newParams.setProperty(getName()+".Other.number","");
                }
            }
            
            if (res.getProperty("recnameeditable").equals("J")) {
                if ((st1=newParams.getProperty(getName()+".name"))!=null &&
                    (st2=oldParams.getProperty("name"))!=null &&
                    st1.equals(st2) &&
                    (st1=newParams.getProperty(getName()+".name2"))!=null &&
                    (st2=oldParams.getProperty("name2"))!=null &&
                    st1.equals(st2)) {
                    newParams.setProperty(getName()+".name","");
                    newParams.setProperty(getName()+".name2","");
                }
            }
            
            if (res.getProperty("valueeditable").equals("J")) {
                if ((st1=newParams.getProperty(getName()+".BTG.value"))!=null &&
                    (st2=oldParams.getProperty("BTG.value"))!=null &&
                    Float.parseFloat(st1)==Float.parseFloat(st2) &&
                    (st1=newParams.getProperty(getName()+".BTG.curr"))!=null &&
                    (st2=oldParams.getProperty("BTG.curr"))!=null &&
                    st1.equals(st2)) {
                    newParams.setProperty(getName()+".BTG.value","");
                    newParams.setProperty(getName()+".BTG.curr","");
                }
            }
            
            if (res.getProperty("keyeditable").equals("J")) {
                if ((st1=newParams.getProperty(getName()+".key"))!=null &&
                    (st2=oldParams.getProperty("key"))!=null &&
                    st1.equals(st2) &&
                    (st1=newParams.getProperty(getName()+".addkey"))!=null &&
                    (st2=oldParams.getProperty("addkey"))!=null &&
                    st1.equals(st2)) {
                    newParams.setProperty(getName()+".key","");
                    newParams.setProperty(getName()+".addkey","");
                }
            }
            
            if (res.getProperty("usageeditable").equals("J")) {
                boolean equal=true;

                for (int i=0;;i++) {
                    String h=HBCIUtils.withCounter("usage.usage",i);
                    String uo=oldParams.getProperty(h);
                    String un=newParams.getProperty(getName()+"."+h);
                    if (uo==null) {
                        if (un!=null) {
                            equal=false;
                        }
                        break;
                    }
                    if (un==null||!un.equals(uo)) {
                        equal=false;
                        break;
                    }
                }

                if (equal) {
                    for (int i=0;;i++) {
                        String h=HBCIUtils.withCounter(getName()+".usage.usage",i);
                        if (newParams.getProperty(h)==null)
                            break;
                        newParams.setProperty(h,"");
                    }
                }
            }
            
            if (res.getProperty("firstexeceditable").equals("J")) {
                if (HBCIUtils.string2Date(newParams.getProperty((key=getName()+".DauerDetails.firstdate"))).equals(HBCIUtils.string2Date(oldParams.getProperty("DauerDetails.firstdate")))) {
                    newParams.setProperty(key,"");
                }
            }
            
            if (res.getProperty("timeuniteditable").equals("J")) {
                if (newParams.getProperty((key=getName()+".DauerDetails.timeunit")).equals(oldParams.getProperty("DauerDetails.timeunit"))) {
                    newParams.setProperty(key,"");
                }
            }
            
            if (res.getProperty("turnuseditable").equals("J")) {
                if (Integer.parseInt(newParams.getProperty((key=getName()+".DauerDetails.turnus")))==Integer.parseInt(oldParams.getProperty("DauerDetails.turnus"))) {
                    newParams.setProperty(key,"");
                }
            }
            
            if (res.getProperty("execdayeditable").equals("J")) {
                if (Integer.parseInt(newParams.getProperty((key=getName()+".DauerDetails.execday")))==Integer.parseInt(oldParams.getProperty("DauerDetails.execday"))) {
                    newParams.setProperty(key,"");
                }
            }
            
            if (res.getProperty("lastexeceditable").equals("J")) {
                if ((st1=newParams.getProperty((key=getName()+".DauerDetails.lastdate")))!=null &&
                    (st2=oldParams.getProperty("DauerDetails.lastdate"))!=null &&
                    HBCIUtils.string2Date(st1).equals(HBCIUtils.string2Date(st2))) {
                    newParams.setProperty(key,"");
                }
            }
        }
    } */
}
