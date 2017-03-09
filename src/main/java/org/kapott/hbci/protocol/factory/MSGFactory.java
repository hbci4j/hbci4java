
/*  $Id: MSGFactory.java,v 1.1 2011/05/04 22:37:49 willuhn Exp $

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
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.tools.ObjectFactory;

public class MSGFactory 
    extends ObjectFactory
{
    private static MSGFactory instance;
    
    public static synchronized MSGFactory getInstance()
    {
        if (instance==null) {
            instance=new MSGFactory();
        }
        return instance;
    }
    
    private MSGFactory()
    {
    	super(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.MSG","8")));
    }
    
    public MSG createMSG(String type,MsgGen gen,Hashtable<String,String> clientValues)
    {
        MSG ret=(MSG)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new MSG object",HBCIUtils.LOG_DEBUG);
            ret=new MSG(type,gen,clientValues);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("resuing MSG object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(type,gen,clientValues);
                addToUsedPool(ret);
            } catch (Exception e) {
                addToFreePool(ret);
                throw (RuntimeException)e;
            }
        }
        
        return ret;
    }
    
    public MSG createMSG(String type, String res, int fullResLen, MsgGen gen)
    {
        return createMSG(type,res,fullResLen,gen,MSG.CHECK_SEQ);
    }
    
    public MSG createMSG(String type,String res,int fullResLen,MsgGen gen,boolean checkSeq)
    {
        return createMSG(type,res,fullResLen,gen,checkSeq,true);
    }
    
    public MSG createMSG(String type,String res,int fullResLen,MsgGen gen,boolean checkSeq,boolean checkValids)
    {
        MSG ret=(MSG)getFreeObject();
        
        if (ret==null) {
            // HBCIUtils.log("creating new MSG object",HBCIUtils.LOG_DEBUG);
            ret=new MSG(type,res,fullResLen,gen,checkSeq,checkValids);
            addToUsedPool(ret);
        } else {
            // HBCIUtils.log("reusing MSG object",HBCIUtils.LOG_DEBUG);
            try {
                ret.init(type,res,fullResLen,gen,checkSeq,checkValids);
                addToUsedPool(ret);
            } catch (Exception e) {
                addToFreePool(ret);
                throw (RuntimeException)e;
            }
        }

        return ret;
    }
    
    public void unuseObject(Object o)
    {
        if (o!=null) {
            ((MSG)o).destroy();
            super.unuseObject(o);
        }
    }
}
