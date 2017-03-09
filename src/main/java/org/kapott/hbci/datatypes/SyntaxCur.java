
/*  $Id: SyntaxCur.java,v 1.1 2011/05/04 22:37:56 willuhn Exp $

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

/* the representation of the datatype "cur" (currency) */
public final class SyntaxCur
    extends SyntaxDE
{
    public SyntaxCur(String x, int minsize, int maxsize)
    {
        super(x.trim(),3,3);
    }

    public void init(String x, int minsize, int maxsize)
    {
        super.init(x.trim(),3,3);
    }

    // --------------------------------------------------------------------------------
    
    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        setContent(st,3,3);
        res.delete(0, endidx);
    }

    public SyntaxCur(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
}
