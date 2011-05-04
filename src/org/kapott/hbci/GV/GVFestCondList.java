
/*  $Id: GVFestCondList.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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

import org.kapott.hbci.GV_Result.GVRFestCondList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Value;

public class GVFestCondList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "FestCondList";
    }
    
    public GVFestCondList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRFestCondList());
        
        addConstraint("curr","curr","EUR", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        for (int i=0;;i++) {
            GVRFestCondList.Cond entry=new GVRFestCondList.Cond();
            String               condheader=HBCIUtilsInternal.withCounter(header+".FestCond",i);
            
            if (result.getProperty(condheader+".anlagedate")==null)
                break;
            
            entry.ablaufdatum=HBCIUtils.string2DateISO(result.getProperty(condheader+".ablaufdate"));
            entry.anlagedatum=HBCIUtils.string2DateISO(result.getProperty(condheader+".anlagedate"));
            entry.id=result.getProperty(condheader+".condid");
            entry.name=result.getProperty(condheader+".condbez");
            
            entry.date=HBCIUtils.strings2DateTimeISO(result.getProperty(header+".FestCondVersion.date"),
                                                      result.getProperty(header+".FestCondVersion.time"));
            entry.version=result.getProperty(header+".FestCondVersion.version");
            
            String st=result.getProperty(condheader+".zinsmethode");
            if (st.equals("A"))
                entry.zinsmethode=GVRFestCondList.Cond.METHOD_30_360;
            else if (st.equals("B"))
                entry.zinsmethode=GVRFestCondList.Cond.METHOD_2831_360;
            else if (st.equals("C"))
                entry.zinsmethode=GVRFestCondList.Cond.METHOD_2831_365366;
            else if (st.equals("D"))
                entry.zinsmethode=GVRFestCondList.Cond.METHOD_30_365366;
            else if (st.equals("E"))
                entry.zinsmethode=GVRFestCondList.Cond.METHOD_2831_365;
            else if (st.equals("F"))
                entry.zinsmethode=GVRFestCondList.Cond.METHOD_30_365;
            
            entry.zinssatz=HBCIUtilsInternal.string2Long(result.getProperty(condheader+".zinssatz"), 1000);
            entry.minbetrag=new Value(
                result.getProperty(condheader+".MinBetrag.value"),
                result.getProperty(condheader+".MinBetrag.curr"));
            entry.name=result.getProperty(condheader+".condbez");

            if (result.getProperty(condheader+".MaxBetrag.value")!=null) {
                entry.maxbetrag=new Value(
                    result.getProperty(condheader+".MaxBetrag.value"),
                    result.getProperty(condheader+".MaxBetrag.curr"));
            }
            
            ((GVRFestCondList)jobResult).addEntry(entry);
        }
    }
}
