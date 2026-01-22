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

package org.hbci4java.hbci.protocol.factory;

import java.util.Hashtable;

import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.protocol.DEG;
import org.hbci4java.hbci.tools.ObjectFactory;
import org.w3c.dom.Document;

public class DEGFactory 
    extends ObjectFactory 
{
    private static DEGFactory instance;
    
    public static synchronized DEGFactory getInstance()
    {
        if (instance==null) {
            instance=new DEGFactory();
        }
        return instance;
    }
    
    private DEGFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.DEG","512")));
    }
    
    public DEG createDEG(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        DEG ret=(DEG)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new DEG object",HBCIUtils.LOG_DEBUG);
            ret=new DEG(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing DEG object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
                addToUsedPool(ret);
            } catch (RuntimeException e) {
                addToFreePool(ret);
                throw e;
            }
        }

        return ret;
    }
    
    public DEG createDEG(String type, String name, String path, int idx, Document syntax)
    {
        DEG ret=(DEG)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new DEG object",HBCIUtils.LOG_DEBUG);
            ret=new DEG(type, name, path, idx, syntax);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing DEG object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(type, name, path, idx, syntax);
                addToUsedPool(ret);
            } catch (RuntimeException e) {
                addToFreePool(ret);
                throw e;
            }
        }

        return ret;
    }
    
    public void unuseObject(Object o)
    {
        if (o!=null) {
            ((DEG)o).destroy();
            super.unuseObject(o);
        }
    }
}
