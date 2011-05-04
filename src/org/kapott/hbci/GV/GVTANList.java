
/*  $Id: GVTANList.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRTANList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

public class GVTANList 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "TANListList";
    }
    
    public GVTANList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTANList());
    }
    
    public void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRTANList.TANList list=new GVRTANList.TANList();
        
        list.status=result.getProperty(header+".liststatus").charAt(0);
        list.number=result.getProperty(header+".listnumber");
        String st=result.getProperty(header+".date");
        if (st!=null)
            list.date=HBCIUtils.string2DateISO(st);
        list.nofTANsPerList=Integer.parseInt(result.getProperty(header+".noftansperlist","0"));
        list.nofUsedTANsPerList=Integer.parseInt(result.getProperty(header+".nofusedtansperlist","0"));
            
        for (int i=0;;i++) {
            String tanheader=HBCIUtilsInternal.withCounter(header+".TANInfo",i);
            
            st=result.getProperty(tanheader+".usagecode");
            if (st==null)
                break;
            
            GVRTANList.TANInfo info=new GVRTANList.TANInfo();
            
            info.usagecode=Integer.parseInt(st);
            info.usagetxt=result.getProperty(tanheader+".usagetxt");
            info.tan=result.getProperty(tanheader+".tan");
            
            String usagedate=result.getProperty(tanheader+".usagedate");
            String usagetime=result.getProperty(tanheader+".usagetime");
            if (usagedate!=null) {
                if (usagetime==null) {
                    info.timestamp=HBCIUtils.string2DateISO(usagedate);
                } else {
                    info.timestamp=HBCIUtils.strings2DateTimeISO(usagedate,usagetime);
                }
            }
                
            list.addTANInfo(info);
        }
        
        ((GVRTANList)jobResult).addTANList(list);
    }
}
