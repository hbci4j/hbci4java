
/*  $Id: GVFestList.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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
import org.kapott.hbci.GV_Result.GVRFestList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public class GVFestList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "FestList";
    }
    
    public GVFestList(String name,HBCIHandler handler)
    {
        super(handler,name,new GVRFestList());
    }
    
    public GVFestList(HBCIHandler handler)
    {
        this(getLowlevelName(),handler);
        
        addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("dummy","allaccounts","N", LogFilter.FILTER_NONE);
        
        // TODO: kontakt fehlt
        // TODO: maxentries fehlen
    }
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        GVRFestList.Entry entry=new GVRFestList.Entry();
        
        entry.anlagebetrag=new Value(
            result.getProperty(header+".Anlagebetrag.value"),
            result.getProperty(header+".Anlagebetrag.curr"));
        
        if (result.getProperty(header+".Anlagekto.number")!=null) {
            entry.anlagekonto=new Konto();
            entry.anlagekonto.blz=result.getProperty(header+".Anlagekto.KIK.blz");
            entry.anlagekonto.country=result.getProperty(header+".Anlagekto.KIK.country");
            entry.anlagekonto.number=result.getProperty(header+".Anlagekto.number");
            entry.anlagekonto.subnumber=result.getProperty(header+".Anlagekto.subnumber");
            getMainPassport().fillAccountInfo(entry.anlagekonto);
        }
        
        if (result.getProperty(header+".Ausbuchungskto.number")!=null) {
            entry.ausbuchungskonto=new Konto();
            entry.ausbuchungskonto.blz=result.getProperty(header+".Ausbuchungskto.KIK.blz");
            entry.ausbuchungskonto.country=result.getProperty(header+".Ausbuchungskto.KIK.country");
            entry.ausbuchungskonto.number=result.getProperty(header+".Ausbuchungskto.number");
            entry.ausbuchungskonto.subnumber=result.getProperty(header+".Ausbuchungskto.subnumber");
            getMainPassport().fillAccountInfo(entry.ausbuchungskonto);
        }
        
        entry.belastungskonto=new Konto();
        entry.belastungskonto.blz=result.getProperty(header+".Belastungskto.KIK.blz");
        entry.belastungskonto.country=result.getProperty(header+".Belastungskto.KIK.country");
        entry.belastungskonto.number=result.getProperty(header+".Belastungskto.number");
        entry.belastungskonto.subnumber=result.getProperty(header+".Belastungskto.subnumber");
        getMainPassport().fillAccountInfo(entry.belastungskonto);
        
        if (result.getProperty(header+".Zinskto.number")!=null) {
            entry.zinskonto=new Konto();
            entry.zinskonto.blz=result.getProperty(header+".Zinskto.KIK.blz");
            entry.zinskonto.country=result.getProperty(header+".Zinskto.KIK.country");
            entry.zinskonto.number=result.getProperty(header+".Zinskto.number");
            entry.zinskonto.subnumber=result.getProperty(header+".Zinskto.subnumber");
            getMainPassport().fillAccountInfo(entry.zinskonto);
        }
        
        entry.id=result.getProperty(header+".kontakt");
        
        String st=result.getProperty(header+".kontoauszug");
        entry.kontoauszug=(st!=null)?Integer.parseInt(st):0;
        st=result.getProperty(header+".status");
        entry.status=(st!=null)?Integer.parseInt(st):0;

        entry.verlaengern=result.getProperty(header+".wiederanlage").equals("2");

        if (result.getProperty(header+".Zinsbetrag.value")!=null) {
            entry.zinsbetrag=new Value(
                result.getProperty(header+".Zinsbetrag.value"),
                result.getProperty(header+".Zinsbetrag.curr"));
        }
        
        entry.konditionen=new GVRFestCondList.Cond();
        entry.konditionen.ablaufdatum=HBCIUtils.string2DateISO(result.getProperty(header+".FestCond.ablaufdate"));
        entry.konditionen.anlagedatum=HBCIUtils.string2DateISO(result.getProperty(header+".FestCond.anlagedate"));
        entry.konditionen.id=result.getProperty(header+".FestCond.condid");
        entry.konditionen.name=result.getProperty(header+".FestCond.condbez");

        if (result.getProperty(header+".FestCondVersion.version")!=null) {
            entry.konditionen.date=HBCIUtils.strings2DateTimeISO(result.getProperty(header+".FestCondVersion.date"),
                                                                  result.getProperty(header+".FestCondVersion.time"));
            entry.konditionen.version=result.getProperty(header+".FestCondVersion.version");
        }
        
        st=result.getProperty(header+".FestCond.zinsmethode");
        if (st.equals("A"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_30_360;
        else if (st.equals("B"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_2831_360;
        else if (st.equals("C"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_2831_365366;
        else if (st.equals("D"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_30_365366;
        else if (st.equals("E"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_2831_365;
        else if (st.equals("F"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_30_365;
        
        entry.konditionen.zinssatz=HBCIUtilsInternal.string2Long(result.getProperty(header+".FestCond.zinssatz"), 1000);
        entry.konditionen.minbetrag=new Value(
            result.getProperty(header+".FestCond.MinBetrag.value"),
            result.getProperty(header+".FestCond.MinBetrag.curr"));
        entry.konditionen.name=result.getProperty(header+".FestCond.condbez");

        if (result.getProperty(header+".FestCond.MaxBetrag.value")!=null) {
            entry.konditionen.maxbetrag=new Value(
                result.getProperty(header+".FestCond.MaxBetrag.value"),
                result.getProperty(header+".FestCond.MaxBetrag.curr"));
        }
        
        if (result.getProperty(header+".Prolong.laufzeit")!=null) {
            entry.verlaengerung=new GVRFestList.Entry.Prolong();
            entry.verlaengerung.betrag=new Value(
                result.getProperty(header+".Prolong.BTG.value"),
                result.getProperty(header+".Prolong.BTG.curr"));
            entry.verlaengerung.laufzeit=Integer.parseInt(result.getProperty(header+".Prolong.laufzeit"));
            entry.verlaengerung.verlaengern=result.getProperty(header+".Prolong.wiederanlage").equals("2");
        }
        
        ((GVRFestList)jobResult).addEntry(entry);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
