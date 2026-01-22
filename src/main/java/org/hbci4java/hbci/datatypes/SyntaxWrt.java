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

/* a special case of the "float"-class with maxsize
    to be defaulted to 15 */
public final class SyntaxWrt
     extends SyntaxFloat
{
    public SyntaxWrt(String x, int minsize, int maxsize)
    {
        super(x, minsize, 15);
    }

    public void init(String x, int minsize, int maxsize)
    {
        super.init(x, minsize, 15);
    }

    // --------------------------------------------------------------------------------

    public SyntaxWrt(StringBuffer res, int minsize, int maxsize)
    {
        super(res, minsize, 15);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        super.init(res, minsize, 15);
    }
    
}
