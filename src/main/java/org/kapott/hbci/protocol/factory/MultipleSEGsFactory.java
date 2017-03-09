
/* $Id: MultipleSEGsFactory.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import org.kapott.hbci.protocol.MultipleSEGs;
import org.kapott.hbci.tools.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MultipleSEGsFactory 
    extends ObjectFactory 
{
    private static MultipleSEGsFactory instance;
    
    public static synchronized MultipleSEGsFactory getInstance()
    {
        if (instance==null) {
            instance=new MultipleSEGsFactory();
        }
        return instance;
    }
    
    private MultipleSEGsFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.SEG","128")));
    }
    
    public MultipleSEGs createMultipleSEGs(Node sfref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        MultipleSEGs ret=(MultipleSEGs)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new multi SEG object",HBCIUtils.LOG_DEBUG);
            ret=new MultipleSEGs(sfref,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing multi SEG object",HBCIUtils.LOG_DEBUG);
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

    public MultipleSEGs createMultipleSEGs(Node sfref, String path, Document syntax)
    {
        MultipleSEGs ret=(MultipleSEGs)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new multi SEG object",HBCIUtils.LOG_DEBUG);
            ret=new MultipleSEGs(sfref,path,syntax);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing multi SEG object",HBCIUtils.LOG_DEBUG);
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
            ((MultipleSEGs)o).destroy();
            super.unuseObject(o);
        }
    }
}
