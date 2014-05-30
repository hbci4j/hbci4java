
/*  $Id: GVWPDepotList.java 62 2008-10-22 17:03:26Z kleiner $

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

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRWPDepotList;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.BigDecimalValue;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.swift.Swift;
import org.kapott.hbci.swift.SwiftLegacy;

public final class GVWPDepotList 
    extends HBCIJobImpl
{
    private StringBuffer buffer;

    public static String getLowlevelName()
    {
        return "WPDepotList";
    }
    
    public GVWPDepotList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRWPDepotList());
        this.buffer=new StringBuffer();
        
        HBCIPassport passport=handler.getPassport();
        
        addConstraint("my.number","Depot.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","Depot.subnumber","", LogFilter.FILTER_MOST);
        
        addConstraint("my.country","Depot.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"), LogFilter.FILTER_NONE);
        addConstraint("my.blz","Depot.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"), LogFilter.FILTER_MOST);
        //addConstraint("my.curr","curr",passport.getUPD().getProperty("KInfo.cur",""), LogFilter.FILTER_NONE);
        addConstraint("quality","quality","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }
    
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx) 
    {
        Properties result=msgstatus.getData();

        // TODO es muessen noch die antwortdaten eines 571 geparst werden
        StringBuffer paramName=new StringBuffer(header).append(".data535");
        buffer.append(Swift.decodeUmlauts(result.getProperty(paramName.toString())));

        final SimpleDateFormat date_time_format = new SimpleDateFormat("yyyyMMdd hhmmss");
        final SimpleDateFormat date_only_format = new SimpleDateFormat("yyyyMMdd");
        
        while (buffer.length()!=0) {
            try {
                String onerecord=Swift.getOneBlock(buffer);
                
                GVRWPDepotList.Entry entry=new GVRWPDepotList.Entry();
                
                String st_timestamp=null;
                String st_date=null;
                String st_time=null;
                char option='C';
                int  i=0;
                
                while (true) {
                    st_timestamp=Swift.getTagValue(onerecord,"98"+option,i++);
                    if (st_timestamp==null) {
                        if (option=='C') {
                            option='A';
                            i=0;
                        } else {
                            break;
                        }
                    } else {
                        if (st_timestamp.substring(1,5).equals("STAT")) {
                            st_date=st_timestamp.substring(7,15);
                            if (option=='C') {
                                st_time=st_timestamp.substring(15,21);
                            }
                            break;
                        }
                    }
                } 
                

                if (st_time!=null) {
                    entry.timestamp=date_time_format.parse(st_date+" "+st_time);
                } else {
                    entry.timestamp=date_only_format.parse(st_date);
                }
                
                String st_depot=Swift.getTagValue(onerecord,"97A",0);
                int pos1=st_depot.indexOf("//");
                int pos2=st_depot.indexOf("/",pos1+2);
                entry.depot=new Konto();
                entry.depot.blz=st_depot.substring(pos1+2,pos2);
                entry.depot.number=st_depot.substring(pos2+1);
                getMainPassport().fillAccountInfo(entry.depot);
                
                String st;
                i=0;
                while (true) {
                    st=Swift.getTagValue(onerecord,"19A",i++);
                    if (st==null) {
                        break;
                    }
                    if (st.substring(1,5).equals("HOLP")) {
                        pos1=7;
                        
                        if (st.charAt(pos1)=='N') 
                            pos1++;
                        
                        entry.total=new BigDecimalValue(
                            st.substring(pos1+3).replace(',','.'),
                            st.substring(pos1,pos1+3));
                        
                        if (pos1>7)
                            entry.total.setValue(entry.total.getValue().negate());
                        break;
                    }
                }
                
                st=Swift.getTagValue(onerecord,"17B",0);
                if (st.substring(st.indexOf("//")+2).equals("Y")) {
                    int subpos=onerecord.indexOf(":16R:FIN");
                    
                    while (true) {
                        pos2=onerecord.indexOf(":16S:FIN",subpos);
                        if ((pos2)==-1) {
                            break;
                        }
                        
                        String onegattung=onerecord.substring(subpos,pos2+8);
                        subpos+=onegattung.length();
                        
                        GVRWPDepotList.Entry.Gattung gattung=new GVRWPDepotList.Entry.Gattung();
                        
                        st=Swift.getTagValue(onegattung,"35B",0);
                        boolean haveISIN=st.substring(0,5).equals("ISIN ");
                        
                        if (haveISIN) {
                            pos1=st.indexOf("\r\n");
                            gattung.isin=st.substring(5,pos1);
                            if (pos1+2<st.length() && st.substring(pos1+2,pos1+6).equals("/DE/")) {
                                pos2=st.indexOf("\r\n",pos1+6);
                                if (pos2==-1) {
                                    pos2=st.length();
                                }
                                gattung.wkn=st.substring(pos1+6,pos2);
                                pos1=pos2;
                            }
                        } else {
                            pos1=st.indexOf("\r\n");
                            gattung.wkn=st.substring(4,pos1);
                        }
                        
                        pos1+=2;
                        if (pos1<st.length())
                            gattung.name=st.substring(pos1);
                        
                        if (gattung.name!=null) {
                            StringBuffer sb=new StringBuffer(gattung.name);
                            int p;
                            while ((p=sb.indexOf("\r\n"))!=-1) {
                                sb.replace(p,p+2," ");
                            }
                            gattung.name=sb.toString();
                        }
                        
                        st=SwiftLegacy.getTagValue(onegattung,"90",new String[] {"A","B"},0);
                        if (st!=null) {
                            gattung.pricequalifier=(st.substring(1,5).equals("MRKT"))?GVRWPDepotList.Entry.Gattung.PRICE_QUALIF_MRKT
                																	 :GVRWPDepotList.Entry.Gattung.PRICE_QUALIF_HINT;
                            
                            int    next=0;
                            String curr;
                            
                            if (st.substring(7,11).equals("PRCT")) {
                                gattung.pricetype=GVRWPDepotList.Entry.Gattung.PRICE_TYPE_PRCT;
                                curr="%";
                                next=12;
                            } else {
                                gattung.pricetype=GVRWPDepotList.Entry.Gattung.PRICE_TYPE_VALUE;
                                curr=st.substring(12,15);
                                next=15;
                            }
                            
                            gattung.price=new BigDecimalValue(
                                st.substring(next).replace(',','.'),
                                curr);
                        }
                        
                        st=Swift.getTagValue(onegattung,"94B",0);
                        if (st!=null) {
                            String st_source=st.substring(7,11);
                            if (st_source.equals("LMAR"))
                                gattung.source=GVRWPDepotList.Entry.Gattung.SOURCE_LOC;
                            else if (st_source.equals("THEO"))
                                gattung.source=GVRWPDepotList.Entry.Gattung.SOURCE_THEOR;
                            else if (st_source.equals("VEND"))
                                gattung.source=GVRWPDepotList.Entry.Gattung.SOURCE_SELLER;
                            
                            pos1=st.indexOf("/",11);
                            if (pos1!=-1) {
                                gattung.source_comment=st.substring(pos1+1);
                            }
                        }
                        
                        st_timestamp=null;
                        st_date=null;
                        st_time=null;
                        option='C';
                        i=0;
                        
                        while (true) {
                            st_timestamp=Swift.getTagValue(onegattung,"98"+option,i++);
                            if (st_timestamp==null) {
                                if (option=='C') {
                                    option='A';
                                    i=0;
                                } else {
                                    break;
                                }
                            } else {
                                if (st_timestamp.substring(1,5).equals("PRIC")) {
                                    st_date=st_timestamp.substring(7,15);
                                    if (option=='C') {
                                        st_time=st_timestamp.substring(15,21);
                                    }
                                    break;
                                }
                            }
                        } 
                        
                        if (st_date!=null) {
                            if (st_time!=null) {
                                gattung.timestamp_price=date_time_format.parse(st_date+" "+st_time);
                            } else {
                                gattung.timestamp_price=date_only_format.parse(st_date);
                            }
                        }
                        
                        st=Swift.getTagValue(onegattung,"93B",0);
                        String st_type=st.substring(7,11);
                        String curr="EUR";
                        
                        if (st_type.equals("FAMT")) { 
                            gattung.saldo_type=GVRWPDepotList.Entry.SALDO_TYPE_WERT;
                            curr=""; // TODO
                        } else if (st_type.equals("UNIT")) {
                            gattung.saldo_type=GVRWPDepotList.Entry.SALDO_TYPE_STCK;
                            curr="";
                        }
                        pos1=12;
                        if (st.charAt(pos1)=='N')
                            gattung.saldo=new BigDecimalValue(
                                "-"+st.substring(pos1+1).replace(',','.'),
                                curr);
                        else
                            gattung.saldo=new BigDecimalValue(
                                st.substring(pos1).replace(',','.'),
                                curr);
                        
                        st=Swift.getTagValue(onegattung,"99A",0);
                        if (st!=null) {
                            if (st.charAt(7)=='N') {
                                gattung.days=-1*Integer.parseInt(st.substring(8));
                            } else {
                                gattung.days=Integer.parseInt(st.substring(7));
                            }
                        }
                        
                        i=0;
                        while (true) {
                            st=Swift.getTagValue(onegattung,"19A",i++);
                            if (st==null) {
                                break;
                            }
                            if (st.substring(1,5).equals("HOLD")) {
                                pos1=7;
                                if (st.charAt(pos1)=='N') 
                                    pos1++;
                                gattung.depotwert=new BigDecimalValue(
                                    st.substring(pos1+3).replace(',','.'),
                                    st.substring(pos1,pos1+3));
                                if (pos1>7)
                                    gattung.depotwert.setValue(gattung.depotwert.getValue().negate());
                                break;
                            }
                        }
                        
                        i=0;
                        while (true) {
                            st=Swift.getTagValue(onegattung,"19A",i++);
                            if (st==null) {
                                break;
                            }
                            if (st.substring(1,5).equals("ACRU")) {
                                pos1=7;
                                if (st.charAt(pos1)=='N') 
                                    pos1++;
                                gattung.stueckzinsbetrag=new BigDecimalValue(
                                    st.substring(pos1+3).replace(',','.'),
                                    st.substring(pos1,pos1+3));
                                if (pos1>7)
                                    gattung.stueckzinsbetrag.setValue(gattung.stueckzinsbetrag.getValue().negate());
                                break;
                            }
                        }
                        
                        st=Swift.getTagValue(onegattung,"92B",0);
                        if (st!=null) {
                            gattung.xchg_cur1=st.substring(7,10);
                            gattung.xchg_cur2=st.substring(11,14);
                            gattung.xchg_kurs=Double.parseDouble(st.substring(15).replace(',','.'));
                        }
                        
                        st=Swift.getTagValue(onegattung,"70E",0);
                        if (st!=null) {
                            String formtext=st.substring(7);
                            
                            gattung.curr=SwiftLegacy.getLineFieldValue(formtext,"1",0);
                            gattung.wptype=SwiftLegacy.getLineFieldValue(formtext,"1",1);
                            gattung.branche=SwiftLegacy.getLineFieldValue(formtext,"1",2);
                            gattung.countryEmittent=SwiftLegacy.getLineFieldValue(formtext,"1",3);
                            
                            st=SwiftLegacy.getLineFieldValue(formtext,"1",4);
                            if (st!=null)
                                gattung.kauf=date_only_format.parse(st);
                            st=SwiftLegacy.getLineFieldValue(formtext,"1",5);
                            if (st!=null)
                                gattung.faellig=date_only_format.parse(st);
                            
                            st=SwiftLegacy.getLineFieldValue(formtext,"2",0);
                            if (st!=null) {
                                gattung.einstandspreis=new BigDecimalValue(
                                    st.replace(',','.'),
                                    "%");
                            }
                            st=SwiftLegacy.getLineFieldValue(formtext,"2",1);
                            if (st!=null)
                                gattung.einstandspreis.setCurr(st);
                            
                            st=SwiftLegacy.getLineFieldValue(formtext,"2",2);
                            if (st!=null)
                                gattung.zinssatz=HBCIUtilsInternal.string2Long(st.replace(',','.'), 1000);
                            
                            // TODO: zeug fuer kontrakte
                        }
                        
                        int subsaldopos=onegattung.indexOf(":16R:SUBBAL");
                        
                        while (true) {
                            pos2=onegattung.indexOf(":16S:SUBBAL",subsaldopos);
                            if ((pos2)==-1) {
                                break;
                            }
                            
                            String onesubsaldo=onegattung.substring(subsaldopos,pos2+11);
                            subsaldopos+=onesubsaldo.length();
                            
                            GVRWPDepotList.Entry.Gattung.SubSaldo subsaldo=new GVRWPDepotList.Entry.Gattung.SubSaldo();
                            
                            st=Swift.getTagValue(onesubsaldo,"93C",0);
                            subsaldo.qualifier=st.substring(1,5);
                            
                            st_type=st.substring(7,11);
                            curr="EUR";
                            if (st_type.equals("FAMT")) { 
                                subsaldo.saldo_type=GVRWPDepotList.Entry.SALDO_TYPE_WERT;
                                curr="";
                            } else if (st_type.equals("UNIT")) {
                                subsaldo.saldo_type=GVRWPDepotList.Entry.SALDO_TYPE_STCK;
                                curr="";
                            }
                            subsaldo.locked=st.substring(12,16).equals("NAVL");
                            pos1=17;
                            if (st.charAt(pos1)=='N')
                                subsaldo.saldo=new BigDecimalValue(
                                    "-"+st.substring(pos1+1).replace(',','.'),
                                    curr);
                            else
                                subsaldo.saldo=new BigDecimalValue(
                                    st.substring(pos1).replace(',','.'),
                                    curr);
                            
                            st=Swift.getTagValue(onesubsaldo,"94C",0);
                            if (st!=null)
                                subsaldo.country=st.substring(7);
                            
                            String formtext=Swift.getTagValue(onesubsaldo,"70C",0);
                            if (formtext!=null) {
                                st=SwiftLegacy.getLineFieldValue(formtext,"2",0);
                                if (st!=null)
                                    subsaldo.verwahrung=Integer.parseInt(st);
                                subsaldo.lager=SwiftLegacy.getLineFieldValue(formtext,"2",1);
                                
                                st=SwiftLegacy.getLineFieldValue(formtext,"2",2);
                                if (st!=null)
                                    subsaldo.lockeduntil=date_only_format.parse(st);
                                
                                subsaldo.comment=SwiftLegacy.getLineFieldValue(formtext,"3",0);
                                st=SwiftLegacy.getLineFieldValue(formtext,"4",0);
                                if (st!=null) {
                                    if (subsaldo.comment==null)
                                        subsaldo.comment=st;
                                    else
                                        subsaldo.comment+=" "+st;
                                }
                            }
                            
                            gattung.addSubSaldo(subsaldo);
                        }
                        
                        entry.addEntry(gattung);
                    }
                }
                
                ((GVRWPDepotList)jobResult).addEntry(entry);
                buffer.delete(0,onerecord.length());
            } catch (Exception e) {
                throw new HBCI_Exception("*** error while extracting data",e);
            }
        }
        
        ((GVRWPDepotList)jobResult).rest=buffer.toString();                    
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
