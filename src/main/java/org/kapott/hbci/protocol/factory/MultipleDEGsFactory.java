/*  $Id: MultipleDEGsFactory.java,v 1.1 2011/05/04 22:37:49 willuhn Exp $

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
import org.kapott.hbci.protocol.MultipleDEGs;
import org.kapott.hbci.tools.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MultipleDEGsFactory 
    extends ObjectFactory 
{
    private static MultipleDEGsFactory instance;
    
    public static synchronized MultipleDEGsFactory getInstance()
    {
        if (instance==null) {
            instance=new MultipleDEGsFactory();
        }
        return instance;
    }
    
    private MultipleDEGsFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.DEG","512")));
    }
    
    public MultipleDEGs createMultipleDEGs(Node sfref, char delimiter, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen,Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        MultipleDEGs ret=(MultipleDEGs)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new multi DEG object",HBCIUtils.LOG_DEBUG);
            ret=new MultipleDEGs(sfref,delimiter,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing multi DEG object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(sfref,delimiter,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
                addToUsedPool(ret);
            } catch (RuntimeException e) {
                addToFreePool(ret);
                throw e;
            }
        }

        return ret;
    }

    public MultipleDEGs createMultipleDEGs(Node sfref, char delimiter,String path, Document syntax)
    {
        MultipleDEGs ret=(MultipleDEGs)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new multi DEG object",HBCIUtils.LOG_DEBUG);
            ret=new MultipleDEGs(sfref,delimiter,path,syntax);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing multi DEG object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(sfref,delimiter,path,syntax);
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
            ((MultipleDEGs)o).destroy();
            super.unuseObject(o);
        }
    }
}
