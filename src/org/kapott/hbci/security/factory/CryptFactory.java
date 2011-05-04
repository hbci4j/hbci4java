
/*  $Id: CryptFactory.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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

package org.kapott.hbci.security.factory;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.IHandlerData;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.security.Crypt;
import org.kapott.hbci.tools.ObjectFactory;

public class CryptFactory 
    extends ObjectFactory 
{
    private static CryptFactory instance;
    
    public static synchronized CryptFactory getInstance()
    {
        if (instance==null) {
            instance=new CryptFactory();
        }
        return instance;
    }
    
    private CryptFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.Crypt","8")));
    }
    
    public Crypt createCrypt(IHandlerData handlerdata, MSG msg)
    {
        Crypt ret=(Crypt)getFreeObject();
        
        if (ret==null) {
            ret=new Crypt(handlerdata,msg);
            addToUsedPool(ret);
        } else {
            try {
                ret.init(handlerdata,msg);
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
            ((Crypt)o).destroy();
            super.unuseObject(o);
        }
    }
}
