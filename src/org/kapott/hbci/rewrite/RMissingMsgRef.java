
/*  $Id: RMissingMsgRef.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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

package org.kapott.hbci.rewrite;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.status.HBCIMsgStatus;

public class RMissingMsgRef
    extends Rewrite
{
    // TODO: msgsize muss angepasst werden
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
