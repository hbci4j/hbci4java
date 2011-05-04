
/*  $Id: SyntaxDate.java,v 1.1 2011/05/04 22:37:55 willuhn Exp $

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

/* represents the datatype "date" */
// interne Speicherung im HBCI-MSG-Format
public final class SyntaxDate
    extends SyntaxDE
{
    /** @brief converts a given date into the hbci-format

        @param x date in format d.m.y (with no restrictions concerning the number
               of digits for each field)
        @return this date in format yyyymmdd (as required by hbci)
    */
    private static String parseDate(String x)
    {
        return new SimpleDateFormat("yyyyMMdd").format(HBCIUtils.string2DateISO(x));
    }

    public SyntaxDate(String x,int minsize,int maxsize)
    {
        super(parseDate(x),8,8);
    }

    public void init(String x,int minsize,int maxsize)
    {
        super.init(parseDate(x),8,8);
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        unparseDate(st);
        setContent(st,8,8);
        res.delete(0, endidx);
    }

    public SyntaxDate(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    /** @brief makes a given date in hbci-format human-readable

        @param x a date in hbci-format (i.e. yyyymmdd)
        @return this date in human-readable-format dd.mm.yyyy with
                this number of digits (i.e. using leading zeroes)
     */
    private String unparseDate(String x)
    {
        try {
            return HBCIUtils.date2StringISO(new SimpleDateFormat("yyyyMMdd").parse(x));
        } catch (Exception e) {
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_DATEERR"),e);
        }
    }

    public String toString()
    {
        String c=getContent();
        return (c==null)?"":unparseDate(c);
    }
}
