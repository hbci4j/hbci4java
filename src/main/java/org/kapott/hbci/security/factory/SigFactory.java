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
