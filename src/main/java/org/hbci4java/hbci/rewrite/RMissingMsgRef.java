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

package org.hbci4java.hbci.rewrite;

import org.hbci4java.hbci.exceptions.HBCI_Exception;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.status.HBCIMsgStatus;

public class RMissingMsgRef
    extends Rewrite
{
    public String incomingCrypted(String st, MsgGen gen) 
    {
        int idx=st.indexOf("'");
        if (idx!=-1) {
            try {
                String msghead_st=st.substring(0,idx);
                int plusidx=0;
                for (int i=0;i<5;i++)
                    plusidx=msghead_st.indexOf("+",plusidx+1);
                if (plusidx==-1) {
                    HBCIUtils.log("MsgRef is missing, adding it", HBCIUtils.LOG_WARN);
                    String[] des={"dialogid","msgnum"};
                    for (int i=0;i<2;i++) {
                        HBCIMsgStatus msgStatus=(HBCIMsgStatus)getData("msgStatus");
                        String        msgName=(String)getData("msgName");
                        String        temp=(msgStatus.getData().getProperty("orig_"+msgName+".MsgHead."+des[i]));
                        HBCIUtils.log("setting MsgRef."+des[i]+" to "+temp,HBCIUtils.LOG_WARN);
                        msghead_st+=(i==0?"+":":");
                        msghead_st+=temp;
                    }
                    st=new StringBuffer(st).replace(0,idx,msghead_st).toString();
                }
            } catch (Exception ex) {
                throw new HBCI_Exception("*** error while fixing missing MsgRef",ex);
            }
        }
        return st;
    }

}
