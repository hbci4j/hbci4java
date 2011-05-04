
/*  $Id: GVAccInfo.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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

package org.kapott.hbci.GV;


import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRAccInfo;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public class GVAccInfo 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "AccInfo";
    }
    
    public GVAccInfo(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRAccInfo());
 
        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("all","allaccounts","N", LogFilter.FILTER_NONE);
    }

    public void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRAccInfo.AccInfo info=new GVRAccInfo.AccInfo();
        String st;
        
        info.account=new Konto();
        info.account.blz=result.getProperty(header+".My.KIK.blz");
        info.account.country=result.getProperty(header+".My.KIK.country");
        info.account.number=result.getProperty(header+".My.number");
        info.account.subnumber=result.getProperty(header+".My.subnumber");
        info.account.curr=result.getProperty(header+".curr");
        info.account.name=result.getProperty(header+".name");
        info.account.name2=result.getProperty(header+".name2");
        info.account.type=result.getProperty(header+".accbez");
        
        info.comment=result.getProperty(header+".info");
        if ((st=result.getProperty(header+".opendate"))!=null)
            info.created=HBCIUtils.string2DateISO(st);
        
        info.habenzins=((st=result.getProperty(header+".habenzins"))!=null)?HBCIUtilsInternal.string2Long(st,1000):-1;
        info.sollzins=((st=result.getProperty(header+".sollzins"))!=null)?HBCIUtilsInternal.string2Long(st,1000):-1;
        info.ueberzins=((st=result.getProperty(header+".overdrivezins"))!=null)?HBCIUtilsInternal.string2Long(st,1000):-1;
        
        if ((st=result.getProperty(header+".kredit.value"))!=null)
            info.kredit=new Value(st,result.getProperty(header+".kredit.curr"));
        if ((st=result.getProperty(header+".refkto.number"))!=null)
            info.refAccount=new Konto(result.getProperty(header+".refkto.KIK.country"),
                                      result.getProperty(header+".refkto.KIK.blz"),
                                      st,
                                      result.getProperty(header+".refkto.subnumber"));
        info.turnus=((st=result.getProperty(header+".turnus"))!=null)?Integer.parseInt(st):-1;
        info.versandart=((st=result.getProperty(header+".versandart"))!=null)?Integer.parseInt(st):-1;
        info.type=((st=result.getProperty(header+".acctype"))!=null)?Integer.parseInt(st):-1;
        
        if (result.getProperty(header+".Address.name1")!=null) {
        	info.address=new GVRAccInfo.AccInfo.Address();
        	info.address.name1=result.getProperty(header+".Address.name1");
        	info.address.name2=result.getProperty(header+".Address.name2");
        	info.address.street_pf=result.getProperty(header+".Address.street_pf");
        	
        	if (result.getProperty(header+".Address.plz")!=null) {
        		// Version 2
        		info.address.plz=result.getProperty(header+".Address.plz");
        		info.address.ort=result.getProperty(header+".Address.ort");
        		info.address.country=result.getProperty(header+".Address.country");
        		info.address.tel=result.getProperty(header+".Address.tel");
        		info.address.fax=result.getProperty(header+".Address.fax");
        		info.address.email=result.getProperty(header+".Address.email");
        	} else {
        		// Version 1
        		info.address.plz_ort=result.getProperty(header+".Address.plz_ort");
        		info.address.tel=result.getProperty(header+".Address.tel");
        	}
        }
 
        ((GVRAccInfo)getJobResult()).addEntry(info);
    }
 
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
