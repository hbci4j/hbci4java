
/*  $Id: Rewrite.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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
