
/*  $Id: GVUebBZU.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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

import java.util.Properties;

import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;

public final class GVUebBZU
    extends GVUeb
{
    public static String getLowlevelName()
    {
        return "Ueb";
    }
    
    public GVUebBZU(HBCIHandler handler)
    {
        super(handler,getLowlevelName());
        
        addConstraint("src.country","My.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("src.blz","My.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("src.number","My.number",null, LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("dst.country","Other.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("dst.blz","Other.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("dst.number","Other.number",null, LogFilter.FILTER_IDS);
        addConstraint("dst.subnumber","Other.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("btg.value","BTG.value",null, LogFilter.FILTER_MOST);
        addConstraint("btg.curr","BTG.curr",null, LogFilter.FILTER_NONE);
        addConstraint("name","name",null, LogFilter.FILTER_IDS);
        addConstraint("bzudata","usage.usage",null, LogFilter.FILTER_MOST);

        addConstraint("name2","name2","", LogFilter.FILTER_IDS);
        addConstraint("key","key","67", LogFilter.FILTER_NONE);

        Properties parameters=getJobRestrictions();
        int        maxusage=Integer.parseInt(parameters.getProperty("maxusage"));

        for (int i=1;i<maxusage;i++) {
            String name=HBCIUtilsInternal.withCounter("usage",i);
            addConstraint(name,"usage."+name,"", LogFilter.FILTER_MOST);
        }
    }
    
    private void checkBZUData(String bzudata)
    {
        if (bzudata==null)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_BZUMISSING"));

        int len=bzudata.length();
        if (len!=13)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_INV_BZULEN",Integer.toString(len)));

        byte[] digits=bzudata.getBytes();
        int p=10;
        int s=0;
        int mod;
        
        for (int j=1;j<=13;j++) {
            s=(p%11) + (digits[j-1]-0x30);
            
            if ((mod=(s%10))==0)
                mod=10;
            p=mod<<1;
        }
        
        if ((s%10)!=1) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_BZUERR",bzudata);
            if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                throw new InvalidUserDataException(msg);
        }
    }
    
    public void setParam(String paramName,String value)
    {
        if (paramName.equals("bzudata"))
            checkBZUData(value);
            
        super.setParam(paramName,value);
    }
}
