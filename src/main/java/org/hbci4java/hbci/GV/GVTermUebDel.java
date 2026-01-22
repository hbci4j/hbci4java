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

import java.util.Enumeration;
import java.util.Properties;

import org.hbci4java.hbci.GV_Result.HBCIJobResultImpl;
import org.hbci4java.hbci.exceptions.InvalidUserDataException;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;
import org.hbci4java.hbci.manager.LogFilter;

public final class GVTermUebDel
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "TermUebDel";
    }
    
    public GVTermUebDel(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new HBCIJobResultImpl());
        
        addConstraint("orderid","id",null, LogFilter.FILTER_NONE);
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
                
                setLowlevelParam(getName()+"."+key,
                                 p.getProperty(key));
            }
        }
    }
}
