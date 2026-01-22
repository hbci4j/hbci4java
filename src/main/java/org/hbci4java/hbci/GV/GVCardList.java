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


import java.util.Properties;

import org.hbci4java.hbci.GV_Result.GVRCardList;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.status.HBCIMsgStatus;
import org.hbci4java.hbci.structures.Value;

public class GVCardList 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "CardList";
    }
    
    public GVCardList(HBCIHandler handler)
   {
       super(handler,getLowlevelName(),new GVRCardList());
       
       addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
       addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
       addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
       addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
   }

    /**
     * @see org.hbci4java.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }

   public void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
   {
       Properties result=msgstatus.getData();
       GVRCardList.CardInfo info=new GVRCardList.CardInfo();
       String st;
       
       info.cardnumber=result.getProperty(header+".cardnumber");
       info.cardordernumber=result.getProperty(header+".nextcardnumber");
       info.cardtype=Integer.parseInt(result.getProperty(header+".cardtype"));
       info.comment=result.getProperty(header+".comment");
       if ((st=result.getProperty(header+".cardlimit.value"))!=null) {
           info.limit=new Value(st,result.getProperty(header+".cardlimit.curr"));
       }
       info.owner=result.getProperty(header+".name");
       if ((st=result.getProperty(header+".validfrom"))!=null) {
           info.validFrom=HBCIUtils.string2DateISO(st);
       }
       if ((st=result.getProperty(header+".validuntil"))!=null) {
           info.validUntil=HBCIUtils.string2DateISO(st);
       }
       
       ((GVRCardList)getJobResult()).addEntry(info);
   }
    
   public void verifyConstraints()
   {
       super.verifyConstraints();
       checkAccountCRC("my");
   }
}
