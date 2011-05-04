
/*  $Id: GVSaldoReq.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;

public class GVSaldoReq
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "Saldo";
    }
    
    public GVSaldoReq(HBCIHandler handler,String name)
    {
        super(handler,name,new GVRSaldoReq());
    }
    
    public GVSaldoReq(HBCIHandler handler)
    {
        this(handler,getLowlevelName());

        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("my.curr","curr","EUR", LogFilter.FILTER_NONE);
        addConstraint("dummyall","allaccounts", "N", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRSaldoReq.Info info=new GVRSaldoReq.Info();

        info.konto=new Konto();
        info.konto.country=result.getProperty(header+".KTV.KIK.country");
        info.konto.blz=result.getProperty(header+".KTV.KIK.blz");
        info.konto.number=result.getProperty(header+".KTV.number");
        info.konto.subnumber=result.getProperty(header+".KTV.subnumber");
        info.konto.bic=result.getProperty(header+".KTV.bic");
        info.konto.iban=result.getProperty(header+".KTV.iban");
        info.konto.type=result.getProperty(header+".kontobez");
        info.konto.curr=result.getProperty(header+".curr");
        getMainPassport().fillAccountInfo(info.konto);
        
        info.ready=new Saldo();
        String cd=result.getProperty(header+".booked.CreditDebit");
        String st=(cd.equals("D")?"-":"") + result.getProperty(header+".booked.BTG.value","0");
        info.ready.value=new Value(
            st,
            result.getProperty(header+".booked.BTG.curr"));
        info.ready.timestamp=HBCIUtils.strings2DateTimeISO(result.getProperty(header+".booked.date"),
                                                           result.getProperty(header+".booked.time"));
        
        cd=result.getProperty(header+".pending.CreditDebit");
        if (cd!=null) {
            st=(cd.equals("D")?"-":"") + result.getProperty(header+".pending.BTG.value", "0");
            info.unready=new Saldo();
            info.unready.value=new Value(
                st,
                result.getProperty(header+".pending.BTG.curr"));
            info.unready.timestamp=HBCIUtils.strings2DateTimeISO(result.getProperty(header+".pending.date"),
                                                                  result.getProperty(header+".pending.time"));
        }
        
        st=result.getProperty(header+".kredit.value");
        if (st!=null) {
            info.kredit=new Value(
                st,
                result.getProperty(header+".kredit.curr"));
        }
        
        st=result.getProperty(header+".available.value");
        if (st!=null) {
            info.available=new Value(
                st,
                result.getProperty(header+".available.curr"));
        }
        
        st=result.getProperty(header+".used.value");
        if (st!=null) {
            info.used=new Value(
                st,
                result.getProperty(header+".used.curr"));
        }
        
        ((GVRSaldoReq)(jobResult)).store(info);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
