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

package org.hbci4java.hbci.GV;

import org.hbci4java.hbci.GV_Result.HBCIJobResultImpl;
import org.hbci4java.hbci.exceptions.InvalidUserDataException;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;
import org.hbci4java.hbci.manager.LogFilter;

public final class GVCustomMsg
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "CustomMsg";
    }
    
    public GVCustomMsg(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new HBCIJobResultImpl());
        
        addConstraint("msg","msg",null, LogFilter.FILTER_NONE);

        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("my.curr","curr","EUR", LogFilter.FILTER_NONE);
        addConstraint("betreff","betreff","", LogFilter.FILTER_NONE);
        addConstraint("recpt","recpt","", LogFilter.FILTER_NONE);
    }
    
    public void setParam(String paramName,String value)
    {
        if (paramName.equals("msg")) {
            String st_maxlen=getJobRestrictions().getProperty("maxlen");
            
            if (st_maxlen!=null) {
                int maxlen=Integer.parseInt(st_maxlen);
                
                if (value.length()>maxlen) {
                    String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_TOOLONG",new String[] {paramName,value,Integer.toString(maxlen)});
                    if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                        throw new InvalidUserDataException(msg);
                }
            }
        }
        super.setParam(paramName,value);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
