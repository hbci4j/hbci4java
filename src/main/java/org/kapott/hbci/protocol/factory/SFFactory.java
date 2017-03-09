
/*  $Id: SFFactory.java,v 1.1 2011/05/04 22:37:49 willuhn Exp $

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

package org.kapott.hbci.protocol.factory;

import java.util.Hashtable;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.protocol.SF;
import org.kapott.hbci.tools.ObjectFactory;
import org.w3c.dom.Document;

public class SFFactory 
    extends ObjectFactory 
{
    private static SFFactory instance;
    
    public static synchronized SFFactory getInstance()
    {
        if (instance==null) {
            instance=new SFFactory();
        }
        return instance;
    }
    
    private SFFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.SF","128")));
    }
    
    public SF createSF(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        SF ret=(SF)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new SF object",HBCIUtils.LOG_DEBUG);
            ret=new SF(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing SF object",HBCIUtils.LOG_DEBUG);
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
    
    public SF createSF(String type, String name, String path, int idx, Document syntax)
    {
        SF ret=(SF)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new SF object",HBCIUtils.LOG_DEBUG);
            ret=new SF(type, name, path, idx, syntax);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing SF object",HBCIUtils.LOG_DEBUG);
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
            ((SF)o).destroy();
            super.unuseObject(o);
        }
    }
}
