
/*  $Id: SyntaxTime.java,v 1.1 2011/05/04 22:37:55 willuhn Exp $

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

package org.kapott.hbci.datatypes;

import java.text.SimpleDateFormat;

import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/* a class for the datatype "time" */
// interne Speicherung im HBCI-MSG-Format
public final class SyntaxTime
     extends SyntaxDE
{
    private static String parseTime(String x)
    {
        return new SimpleDateFormat("HHmmss").format(HBCIUtils.string2TimeISO(x));
    }

    public SyntaxTime(String x, int minsize, int maxsize)
    {
        super(parseTime(x),6,6);
    }

    public void init(String x, int minsize, int maxsize)
    {
        super.init(parseTime(x),6,6);
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        unparseTime(st);
        setContent(st, 6, 6);
        res.delete(0, endidx);
    }

    public SyntaxTime(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    private String unparseTime(String x)
    {
        try {
            return HBCIUtils.time2StringISO(new SimpleDateFormat("HHmmss").parse(x));
        } catch (Exception e) {
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_TIMEERR"),e);
        }
    }

    public String toString()
    {
        String c = getContent();
        return (c == null) ? "" : unparseTime(c);
    }
}
