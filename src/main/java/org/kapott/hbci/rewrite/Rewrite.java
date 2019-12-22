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

package org.kapott.hbci.rewrite;

import java.util.Hashtable;

import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;

public abstract class Rewrite
{
    private static Hashtable<String,Object> kernelData;
    
    static {
        kernelData=new Hashtable<String, Object>();
    }
    
    protected Rewrite()
    {
    }
    
    public static synchronized void setData(String name,Object value)
    {
        ThreadGroup threadgroup=Thread.currentThread().getThreadGroup();
        kernelData.put(threadgroup.getName()+"_"+name,
                       value);
    }

    public static synchronized Object getData(String name)
    {
        ThreadGroup threadgroup=Thread.currentThread().getThreadGroup();
        return kernelData.get(threadgroup.getName()+"_"+name);
    }

    public MSG outgoingClearText(MSG msg,MsgGen gen)
    {
        return msg;
    }
    
    public MSG outgoingSigned(MSG msg,MsgGen gen)
    {
        return msg;
    }
    
    public MSG outgoingCrypted(MSG msg,MsgGen gen)
    {
        return msg;
    }
    
    
    
    public String incomingCrypted(String st,MsgGen gen)
    {
        return st;
    }
    
    public String incomingClearText(String st,MsgGen gen)
    {
        return st;
    }
    
    public MSG incomingData(MSG msg,MsgGen gen)
    {
        return msg;
    }
}
