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

package org.kapott.hbci.datatypes;

import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/* the class for representing the datatype "num" */
// interne Speicherung im HBCI-Nachrichten-Format
public final class SyntaxNum
    extends SyntaxDE
{
    private static String check(String st)
    {
        try {
          Long.valueOf(st);
            StringBuffer ret=new StringBuffer(st);

            // remove heading '0's
            while (ret.length()!=1&&ret.charAt(0)=='0') {
                ret.deleteCharAt(0);
            }

            return ret.toString();
        } catch (Exception e) {
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_LONGERR",st),e); 
        }
    }

    public SyntaxNum(String st, int minsize, int maxsize)
    {
        super(check(st),minsize,maxsize);
    }

    public void init(String st, int minsize, int maxsize)
    {
        super.init(check(st),minsize,maxsize);
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);
        int len=st.length();

        for (int i=0; i<len; i++) {
            char ch = st.charAt(i);

            if (ch>'9' || ch<'0' || (i==0 && len!=1 && ch=='0'))
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXC_DTNUM_INV_CHAR",st));
        }

        setContent(st, minsize, maxsize);
        res.delete(0, endidx);
    }
    
    public SyntaxNum(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    
}
