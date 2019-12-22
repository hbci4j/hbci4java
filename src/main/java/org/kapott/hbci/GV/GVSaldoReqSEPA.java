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

import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;

public class GVSaldoReqSEPA 
	extends GVSaldoReq 
{
    public static String getLowlevelName()
    {
        return "SaldoSEPA";
    }
    
    public GVSaldoReqSEPA(HBCIHandler handler,String name)
    {
        super(handler,name);
    }
    
    public GVSaldoReqSEPA(HBCIHandler handler)
    {
        this(handler,getLowlevelName());

        /*
        addConstraint("my.country","KTV.KIK.country","", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz","", LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number","", LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        */
        addConstraint("my.bic","KTV.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("my.iban","KTV.iban",null, LogFilter.FILTER_IDS);
        addConstraint("dummyall","allaccounts", "N", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }
}
