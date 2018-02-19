
/*  $Id: ConvertRDHNewToRDHXFile.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.AbstractRDHSWPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportRDHXFile;

public class ConvertRDHNewToRDHXFile
{
    public static void main(String[] args) 
    throws IOException
    {
        HBCIUtils.init(null,new HBCICallbackConsole());

        String nameOld=readParam(args,0,"Filename of old RDHNew passport file");
        HBCIUtils.setParam("client.passport.RDHNew.filename",nameOld);
        HBCIUtils.setParam("client.passport.RDHNew.init","1");
        HBCIPassportInternal passportOld=(HBCIPassportInternal)AbstractHBCIPassport.getInstance("RDHNew");

        String nameNew=readParam(args,1,"Filename of new RDHXFile passport file");
        HBCIUtils.setParam("client.passport.RDHXFile.filename",nameNew);
        HBCIUtils.setParam("client.passport.RDHXFile.init","0");
        HBCIPassportInternal passportNew=(HBCIPassportInternal)AbstractHBCIPassport.getInstance("RDHXFile");

        passportNew.setBLZ(passportOld.getBLZ());
        passportNew.setCountry(passportOld.getCountry());
        passportNew.setHost(passportOld.getHost());
        passportNew.setPort(passportOld.getPort());
        passportNew.setUserId(passportOld.getUserId());
        passportNew.setCustomerId(passportOld.getCustomerId());
        passportNew.setSysId(passportOld.getSysId());
        passportNew.setSigId(passportOld.getSigId());
        passportNew.setProfileVersion(passportOld.getProfileVersion());
        passportNew.setHBCIVersion(passportOld.getHBCIVersion());
        passportNew.setBPD(passportOld.getBPD());
        passportNew.setUPD(passportOld.getUPD());

        ((HBCIPassportRDHXFile)passportNew).setInstSigKey(((AbstractRDHSWPassport)passportOld).getInstSigKey());
        ((HBCIPassportRDHXFile)passportNew).setInstEncKey(((AbstractRDHSWPassport)passportOld).getInstEncKey());
        ((HBCIPassportRDHXFile)passportNew).setMyPublicSigKey(((AbstractRDHSWPassport)passportOld).getMyPublicSigKey());
        ((HBCIPassportRDHXFile)passportNew).setMyPrivateSigKey(((AbstractRDHSWPassport)passportOld).getMyPrivateSigKey());
        ((HBCIPassportRDHXFile)passportNew).setMyPublicEncKey(((AbstractRDHSWPassport)passportOld).getMyPublicEncKey());
        ((HBCIPassportRDHXFile)passportNew).setMyPrivateEncKey(((AbstractRDHSWPassport)passportOld).getMyPrivateEncKey());
        ((HBCIPassportRDHXFile)passportNew).setProfileVersion(((AbstractRDHSWPassport)passportOld).getProfileVersion());

        passportNew.saveChanges();

        passportOld.close();
        passportNew.close();            
    }

    private static String readParam(String[] args,int idx,String st)
    throws IOException
    {
        String ret;

        System.out.print(st+": ");
        System.out.flush();

        if (args.length<=idx) {
            ret=new BufferedReader(new InputStreamReader(System.in)).readLine();
        } else {
            System.out.println(args[idx]);
            ret=args[idx];
        }

        return ret;
    }
}
