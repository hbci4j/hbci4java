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

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;

public class RSigIdLeadingZero 
    extends Rewrite
{
    public String incomingClearText(String st,MsgGen gen)
    {
        StringBuffer ret=new StringBuffer(st); 
        int          firstPlus=st.indexOf("HNSHK");
        
        if (firstPlus!=-1) {
            for (int i=0;i<6;i++) {
                firstPlus=st.indexOf("+",firstPlus+1);
                if (firstPlus==-1) {
                    break;
                }
            }
            
            if (firstPlus!=-1) {
                int secondPlus=st.indexOf("+",firstPlus+1);
                
                if (secondPlus!=-1) {
                    StringBuffer value=new StringBuffer(st.substring(firstPlus+1,secondPlus));
                    
                    if (value.length()>1 && value.charAt(0)=='0') {
                        HBCIUtils.log("RSigIdLeadingZero: found leading zero ("+value+"), removing it",HBCIUtils.LOG_WARN);
                        while (value.length()>1 && value.charAt(0)=='0') {
                            value.deleteCharAt(0);
                        }
                        
                        ret.replace(firstPlus+1,secondPlus,value.toString());
                        HBCIUtils.log("RSigIdLeadingZero: setting new sigid: "+value,HBCIUtils.LOG_WARN);
                    }
                } else {
                    HBCIUtils.log("RSigIdLeadingZero: can not find end of sigid in segment",HBCIUtils.LOG_WARN);
                }
            } else {
                HBCIUtils.log("RSigIdLeadingZero: can not find sigid in segment",HBCIUtils.LOG_WARN);
            }
        }
        
        return ret.toString();
    }
}
