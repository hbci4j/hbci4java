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

package org.hbci4java.hbci.GV;

import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.LogFilter;

public final class GVSaldoReqAll
    extends GVSaldoReq
{
    public static String getLowlevelName()
    {
        return "Saldo";
    }
    
    public GVSaldoReqAll(HBCIHandler handler)
    {
        super(handler,getLowlevelName());

        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        addConstraint("dummyall","allaccounts", "J", LogFilter.FILTER_NONE);
        
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
          addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
          addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
          addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
          addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
          addConstraint("my.curr","curr","EUR", LogFilter.FILTER_NONE);
        }

    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
