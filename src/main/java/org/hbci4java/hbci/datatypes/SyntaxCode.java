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


public class SyntaxCode
     extends SyntaxAN
{
    public SyntaxCode(String x,int minlen,int maxlen)
    {
        super(x,minlen,maxlen);
    }

    public void init(String x,int minlen,int maxlen)
    {
        super.init(x,minlen,maxlen);
    }

    // --------------------------------------------------------------------------------

    public SyntaxCode(StringBuffer res,int minsize,int maxsize)
    {
        super(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res,int minsize,int maxsize)
    {
        super.init(res,minsize,maxsize);
    }
    
}
