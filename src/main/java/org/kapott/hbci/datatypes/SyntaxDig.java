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
import org.kapott.hbci.manager.HBCIUtilsInternal;

/* a representation of the datatype "dig", which
    consists of a string of digits (including zeroes) */
// Speicherung des mit Nullen aufgefüllten Strings
public final class SyntaxDig
    extends SyntaxDE
{
    /** @brief prepends leading "0" to a number

        @param st number in string-format
        @param destlen required length of string
        @return the given number with leading zeroes, so that the
                resulting string is @p len characters long
     */
    private static String buildString(String st, int destlen)
    {
        StringBuffer ret=new StringBuffer(destlen);
        ret.append(st);

        int len=st.length();
        for (int i=0; i<len; i++) {
            char c=st.charAt(i);
            if (c<'0' || c>'9')
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXC_DTDIG_ONLY_DIGS",st));
        }

        for (int i=len; i<destlen; i++) {
            ret.insert(0,'0');
        }

        return ret.toString();
    }

    public SyntaxDig(String st, int minsize, int maxsize)
    {
        super(buildString(st.trim(),minsize),minsize,maxsize);
    }

    public void init(String st, int minsize, int maxsize)
    {
        super.init(buildString(st.trim(),minsize),minsize,maxsize);
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);
        int len=st.length();

        for (int i=0;i<len;i++) {
            char ch=st.charAt(i);

            if (ch>'9' || ch<'0')
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXC_DTDIG_ONLY_DIGS",st));
        }

        setContent(st,minsize,maxsize);
        res.delete(0, endidx);
    }
    
    public SyntaxDig(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
}
