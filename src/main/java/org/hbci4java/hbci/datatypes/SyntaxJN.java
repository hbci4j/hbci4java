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

package org.hbci4java.hbci.datatypes;

import org.hbci4java.hbci.exceptions.InvalidArgumentException;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;

/* a class representing the datatype "jn", where the
    value can be only either "J" or "N" */
public class SyntaxJN
     extends SyntaxAN
{
    private static String check(String x)
    {
        if (!x.equals("J") && !x.equals("N"))
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXC_DTJN_ONLY_JN"));
        return x;
    }

    public SyntaxJN(String x, int minlen, int maxlen)
    {
        super(check(x.trim()), 1, 1);
    }

    public void init(String x, int minlen, int maxlen)
    {
        super.init(check(x.trim()), 1, 1);
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        setContent(check(st), 1, 1);
        res.delete(0, endidx);
    }
    
    public SyntaxJN(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
}
