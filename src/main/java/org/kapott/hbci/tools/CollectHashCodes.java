
/*  $Id: CollectHashCodes.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

 This file is part of hbci4java
 Copyright (C) 2001-2008  Stefan Palme

 hbci4java is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 hbci4java is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.kapott.hbci.tools;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIInstitute;
import org.kapott.hbci.manager.HBCIKernelFactory;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.INILetter;

public class CollectHashCodes
{
    private static class MyCallback 
        extends HBCICallbackConsole
    {
        public void callback(HBCIPassport passport,int reason,String msg,
                             int datatype,StringBuffer retData)
        {
            Properties data=(Properties)passport.getClientData("init");
            
            switch (reason) {
                case NEED_BLZ:
                    retData.setLength(0);
                    retData.append(data.getProperty("blz"));
                    break;
                    
                case NEED_COUNTRY:
                    retData.setLength(0);
                    retData.append("DE");
                    break;
                    
                case NEED_HOST:
                    retData.setLength(0);
                    retData.append(data.getProperty("host"));
                    break;
                    
                case NEED_PORT:
                    retData.setLength(0);
                    retData.append("3000");
                    break;
                    
                case NEED_PASSPHRASE_LOAD:
                case NEED_PASSPHRASE_SAVE:
                case NEED_USERID:
                case NEED_CUSTOMERID:
                    retData.setLength(0);
                    retData.append("dummy");
                    break;
                    
                case NEED_NEW_INST_KEYS_ACK:
                    INILetter iniletter=new INILetter(passport,INILetter.TYPE_INST);
                    data.setProperty("hash",HBCIUtils.data2hex(iniletter.getKeyHashDisplay()));
                    break;
                    
                case NEED_CONNECTION:
                case HAVE_INST_MSG:
                case CLOSE_CONNECTION:
                    break;
            }
        }
        public synchronized void status(HBCIPassport passport,int statusTag,
                                        Object[] o)
        {
        }
    }
    
    public static void main(String[] args)
        throws Exception
    {
        HBCIUtils.init(null,new MyCallback());
        
        Properties blzs=new Properties();
        InputStream fin=new FileInputStream("src/blz.properties");
        blzs.load(fin);
        fin.close();
        
        for (Enumeration e=blzs.propertyNames();e.hasMoreElements();) {
            String blz=(String)e.nextElement();
            String host=HBCIUtils.getHBCIHostForBLZ(blz);
            if (host!=null && host.length()!=0) {
                addHashValue(blzs,blz);
            }
        }
    }
    
    private static void addHashValue(Properties blzs, String blz)
    {
        System.out.println();
        System.out.println(blz+": "+HBCIUtils.getNameForBLZ(blz));
        
        HBCIUtils.setParam("log.loglevel.default","0");
        HBCIUtils.setParam("client.passport.RDHNew.filename","passports/"+blz+".passport");
        HBCIUtils.setParam("client.passport.RDHNew.init","1");
        
        Properties   data=new Properties();
        data.setProperty("blz",blz);
        data.setProperty("host",HBCIUtils.getHBCIHostForBLZ(blz));
        
        HBCIPassportInternal passport=(HBCIPassportInternal)AbstractHBCIPassport.getInstance("RDHNew",data);
        HBCIKernelImpl       kernel=(HBCIKernelImpl)HBCIKernelFactory.getKernel(null,"210");
        HBCIInstitute inst=new HBCIInstitute(kernel,passport,true);
        
        try {
            inst.fetchBPD();
        } catch (Exception e) {
            System.out.println("  "+blz+": fetching BPD failed");
        }
        
        if (passport.isSupported()) {
            try {
                inst.fetchKeys();
                System.out.println("  "+blz+": found hashvalue "+data.getProperty("hash"));
            } catch (Exception e) {
                System.out.println("  "+blz+": fetching keys failed");
                System.out.println(HBCIUtils.exception2StringShort(e));
            }
        } else {
            System.out.println("  "+blz+": RDH not supported");
        }
    }
}
