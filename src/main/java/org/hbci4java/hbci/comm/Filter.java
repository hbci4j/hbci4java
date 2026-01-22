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

package org.hbci4java.hbci.comm;

import java.lang.reflect.Constructor;

import org.hbci4java.hbci.exceptions.HBCI_Exception;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;

public abstract class Filter
{
    public abstract byte[] encode(String st);
    public abstract String decode(String st);
        
    public static Filter getInstance(String filter)
    {
        try {
            Class cl=Class.forName("org.hbci4java.hbci.comm.Filter"+filter);
            Constructor cons=cl.getConstructor((Class[])null);
            return (Filter)cons.newInstance((Object[])null);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANTCREATEFILT",filter),e);
        }
    }
}
