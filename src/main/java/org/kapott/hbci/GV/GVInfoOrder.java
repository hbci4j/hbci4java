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


import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRInfoOrder;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

public final class GVInfoOrder
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "InfoDetails";
    }
    
    public GVInfoOrder(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRInfoOrder());

        addConstraint("code","InfoCodes.code",null, LogFilter.FILTER_NONE);
        
        addConstraint("name","Address.name1","", LogFilter.FILTER_IDS);
        addConstraint("name2","Address.name2","", LogFilter.FILTER_IDS);
        addConstraint("street","Address.street_pf","", LogFilter.FILTER_IDS);
        addConstraint("ort","Address.ort","", LogFilter.FILTER_MOST);
        addConstraint("plz","Address.plz_ort","", LogFilter.FILTER_MOST);
        addConstraint("plz","Address.plz","", LogFilter.FILTER_MOST);
        addConstraint("country","Address.country","", LogFilter.FILTER_NONE);
        addConstraint("tel","Address.tel","", LogFilter.FILTER_IDS);
        addConstraint("fax","Address.fax","", LogFilter.FILTER_IDS);
        addConstraint("email","Address.email","", LogFilter.FILTER_IDS);
        
        for (int i=1;i<10;i++) {
            addConstraint(HBCIUtilsInternal.withCounter("code",i),
                          HBCIUtilsInternal.withCounter("InfoCodes.code",i),
                          "",
                          LogFilter.FILTER_NONE);
        }
    }

    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        for (int i=0;;i++) {
            String header2=HBCIUtilsInternal.withCounter(header+".Info",i);
            
            if (result.getProperty(header2+".code")==null)
                break;
            
            GVRInfoOrder.Info entry=new GVRInfoOrder.Info();
            
            entry.code=result.getProperty(header2+".code");
            entry.msg=result.getProperty(header2+".msg");

            ((GVRInfoOrder)(jobResult)).addEntry(entry);
        }
    }
}
