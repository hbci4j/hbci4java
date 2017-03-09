
/*  $Id: SigFactory.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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
import org.kapott.hbci.passport.HBCIPassportList;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.security.Sig;
import org.kapott.hbci.tools.ObjectFactory;

public class SigFactory 
    extends ObjectFactory 
{
    private static SigFactory instance;
    
    public static SigFactory getInstance()
    {
        if (instance==null) {
            instance=new SigFactory();
        }
        return instance;
    }
    
    private SigFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.Sig","8")));
    }
    
    public Sig createSig(IHandlerData handlerdata, MSG msg, HBCIPassportList passports)
    {
        Sig ret=(Sig)getFreeObject();
        
        if (ret==null) {
            ret=new Sig(handlerdata,msg,passports);
            addToUsedPool(ret);
        } else {
            try {
                ret.init(handlerdata,msg,passports);
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
            ((Sig)o).destroy();
            super.unuseObject(o);
        }
    }
}
