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

public class RInvalidSegment
    extends Rewrite
{
    public String incomingClearText(String st,MsgGen gen) 
    {
        StringBuffer sb=new StringBuffer(st);

        int idx=sb.indexOf("'IIDIA:");
        if (idx!=-1) {
            int idx2=sb.indexOf("'",idx+1);
            HBCIUtils.log("removing invalid segment '"+sb.substring(idx+1,idx2+1)+"'",HBCIUtils.LOG_WARN);
            sb.delete(idx+1,idx2+1);
        }

        return sb.toString();
    }
}
