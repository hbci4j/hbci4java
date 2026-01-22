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
import org.hbci4java.hbci.protocol.MultipleSFs;
import org.hbci4java.hbci.tools.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MultipleSFsFactory 
    extends ObjectFactory 
{
    private static MultipleSFsFactory instance;
    
    public static synchronized MultipleSFsFactory getInstance()
    {
        if (instance==null) {
            instance=new MultipleSFsFactory();
        }
        return instance;
    }
    
    private MultipleSFsFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.SF","128")));
    }
 
    public MultipleSFs createMultipleSFs(Node sfref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        MultipleSFs ret=(MultipleSFs)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new multi SF object",HBCIUtils.LOG_DEBUG);
            ret=new MultipleSFs(sfref,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing multi SF object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(sfref,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
                addToUsedPool(ret);
            } catch (RuntimeException e) {
                addToFreePool(ret);
                throw e;
            }
        }

        return ret;
    }

    public MultipleSFs createMultipleSFs(Node sfref, String path, Document syntax)
    {
        MultipleSFs ret=(MultipleSFs)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new multi SF object",HBCIUtils.LOG_DEBUG);
            ret=new MultipleSFs(sfref,path,syntax);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing multi SF object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(sfref,path,syntax);
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
            ((MultipleSFs)o).destroy();
            super.unuseObject(o);
        }
    }
}
