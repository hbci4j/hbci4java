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

import org.hbci4java.hbci.GV_Result.HBCIJobResultImpl;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.LogFilter;

public class GVUebForeign 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "UebForeign";
    }
    
    public GVUebForeign(String name,HBCIHandler handler)
    {
        super(handler,name,new HBCIJobResultImpl());
    }

    public GVUebForeign(HBCIHandler handler)
    {
        this(getLowlevelName(),handler);
        
        addConstraint("src.country","My.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("src.blz","My.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("src.number","My.number",null, LogFilter.FILTER_IDS);
        addConstraint("src.subnumber","My.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("src.name","myname",null, LogFilter.FILTER_IDS);
        addConstraint("dst.country","Other.KIK.country","", LogFilter.FILTER_NONE);
        addConstraint("dst.blz","Other.KIK.blz","", LogFilter.FILTER_MOST);
        addConstraint("dst.number","Other.number","", LogFilter.FILTER_IDS);
        addConstraint("dst.subnumber","Other.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("dst.iban","otheriban","", LogFilter.FILTER_IDS);
        addConstraint("dst.name","othername",null, LogFilter.FILTER_IDS);
        addConstraint("dst.kiname","otherkiname",null, LogFilter.FILTER_MOST);
        addConstraint("btg.value","BTG.value",null, LogFilter.FILTER_MOST);
        addConstraint("btg.curr","BTG.curr",null, LogFilter.FILTER_NONE);

        addConstraint("kostentraeger","kostentraeger","1", LogFilter.FILTER_NONE);
        addConstraint("usage","usage","", LogFilter.FILTER_MOST);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("src");
    }
}
