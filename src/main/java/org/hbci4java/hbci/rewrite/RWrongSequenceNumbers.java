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

import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.MsgGen;

public final class RWrongSequenceNumbers
    extends Rewrite
{
    public String incomingClearText(String st,MsgGen gen) 
    {
        StringBuffer sb=new StringBuffer(st);

        int     idx;
        boolean quoteNext=false;
        int     correctSeq=1;
        
        for (int i=0;i<sb.length();i++) {
            char ch=sb.charAt(i);
            
            if (!quoteNext && ch=='@') {
                // skip binary values
                idx=sb.indexOf("@",i+1);
                String len_st=sb.substring(i+1,idx);
                i+=Integer.parseInt(len_st)+1+len_st.length();
            } else if (!quoteNext && ch=='\'' || i==0) {
                idx=sb.indexOf(":",i+1);
                if (idx!=-1) {
                    int idx2=sb.indexOf(":",idx+1);
                    int seq=Integer.parseInt(sb.substring(idx+1,idx2));
                    if (seq!=correctSeq) {
                        HBCIUtils.log("found wrong sequence number "+seq+"; replacing with "+correctSeq,HBCIUtils.LOG_WARN);
                        sb.replace(idx+1,idx2,Integer.toString(correctSeq));
                    }
                    i=idx2;
                }
                correctSeq++;
            }
            quoteNext=!quoteNext && ch=='?';
        }
        
        return sb.toString();
    }
}
