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

        
        boolean sepa = false;
        try
        {
          sepa = Integer.parseInt(this.getSegVersion()) >= 7; 
        }
        catch (Exception e)
        {
          HBCIUtils.log(e);
        }
        
        boolean nat = this.canNationalAcc(handler);

        if (sepa)
        {
          addConstraint("my.bic","KTV.bic",  null, LogFilter.FILTER_MOST);
          addConstraint("my.iban","KTV.iban",null, LogFilter.FILTER_IDS);
        }

        if (nat || !sepa)
        {
          addConstraint("my.curr","curr","EUR", LogFilter.FILTER_NONE);
          addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
          addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
          addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
          addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        }
        
        addConstraint("dummyall","allaccounts", "N", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
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
