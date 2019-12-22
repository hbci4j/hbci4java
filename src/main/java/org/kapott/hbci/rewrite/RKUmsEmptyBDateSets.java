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

package org.kapott.hbci.rewrite;

import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SyntaxElement;

public class RKUmsEmptyBDateSets 
    extends Rewrite
{
    private String rewriteKUms(String st)
    {
        StringBuffer temp=new StringBuffer(st);
        boolean      foundError=false;
        int          posi;
        
        while ((posi=temp.indexOf("\r\n-\r\n-\r\n"))!=-1) {
            temp.delete(posi,posi+3);
            foundError=true;
        }
        
        HBCIUtils.log(
            "rewriter KUmsEmptyBDateSets: found empty bdate sets: "+foundError,
            foundError?HBCIUtils.LOG_WARN:HBCIUtils.LOG_DEBUG);
        
        return temp.toString();
    }

    public MSG incomingData(MSG msg,MsgGen gen)
    {
        String     header="GVRes";
        Properties data=msg.getData();
        
        for (Enumeration i=data.propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.startsWith(header) && 
                key.indexOf("KUms")!=-1 &&
                key.endsWith(".booked")) {
                    
                String st=msg.getValueOfDE(msg.getName()+"."+key);
                st=rewriteKUms(st);
                msg.propagateValue(msg.getName()+"."+key,"B"+st,
                        SyntaxElement.DONT_TRY_TO_CREATE,
                        SyntaxElement.ALLOW_OVERWRITE);
            }
        }

        return msg;
    }
}
