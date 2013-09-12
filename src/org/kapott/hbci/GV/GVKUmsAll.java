
/*  $Id: GVKUmsAll.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.swift.Swift;

public class GVKUmsAll
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "KUmsZeit";
    }
    
    public GVKUmsAll(HBCIHandler handler,String name)
    {
        super(handler, name, new GVRKUms());
    }

    public GVKUmsAll(HBCIHandler handler)
    {
        this(handler,getLowlevelName());

        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        //currency wird in neueren Versionen nicht mehr benötigt, constraint liefert unnötige Warnung
        //im Prinzip müsste es möglich sein, die constraints versionsabhängig zu definieren
        //addConstraint("my.curr","curr","EUR", LogFilter.FILTER_NONE);
        addConstraint("startdate","startdate","", LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        
        addConstraint("dummy","allaccounts","N", LogFilter.FILTER_NONE);
    }

    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRKUms    umsResult=(GVRKUms)jobResult; 
        
        StringBuffer paramName = new StringBuffer(header).append(".booked");
        String       rawData = result.getProperty(paramName.toString());
        if (rawData!=null) {
            umsResult.appendMT940Data(Swift.decodeUmlauts(rawData));
        }
        
        paramName = new StringBuffer(header).append(".notbooked");
        rawData = result.getProperty(paramName.toString());
        if (rawData!=null) {
            umsResult.appendMT942Data(Swift.decodeUmlauts(rawData));
        }
        
        // TODO: this is for compatibility reasons only
        jobResult.storeResult("notbooked",result.getProperty(header+".notbooked"));
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
