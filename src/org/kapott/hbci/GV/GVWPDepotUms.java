
/*  $Id: GVWPDepotUms.java 62 2008-10-22 17:03:26Z kleiner $

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

import org.kapott.hbci.GV_Result.GVRWPDepotUms;
import org.kapott.hbci.GV_Result.GVRWPDepotUms.Entry.FinancialInstrument;
import org.kapott.hbci.GV_Result.GVRWPDepotUms.Entry.FinancialInstrument.Transaction;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.BigDecimalValue;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.TypedValue;
import org.kapott.hbci.swift.Swift;

public class GVWPDepotUms 
extends HBCIJobImpl
{
    private StringBuffer buffer;

    public static String getLowlevelName()
    {
        return "WPDepotUms";
    }

    public GVWPDepotUms(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRWPDepotUms());
        this.buffer=new StringBuffer();

        HBCIPassport passport=handler.getPassport();

        addConstraint("my.number","Depot.number",null, LogFilter.FILTER_IDS);
        addConstraint("my.subnumber","Depot.subnumber","", LogFilter.FILTER_MOST);

        addConstraint("my.country","Depot.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"), LogFilter.FILTER_NONE);
        addConstraint("my.blz","Depot.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"), LogFilter.FILTER_MOST);
        //addConstraint("my.curr","curr",passport.getUPD().getProperty("KInfo.cur",""), LogFilter.FILTER_NONE);
        addConstraint("quality","quality","", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        
        addConstraint("startdate","startdate","", LogFilter.FILTER_NONE);
        addConstraint("enddate","enddate","", LogFilter.FILTER_NONE);
        
        addConstraint("dummy","alldepots","N", LogFilter.FILTER_NONE);
    }

    private TypedValue parseTypedValue(String st) {
        String st_type=st.substring(7,11);
        String curr="";
        boolean withCurr = false;

        int saldo_type = -1;
        if (st_type.equals("FAMT")) { 
            saldo_type=TypedValue.TYPE_WERT;
        } else if (st_type.equals("ACTU")) { 
            saldo_type=TypedValue.TYPE_WERT;
            withCurr = true;
        } else if (st_type.equals("UNIT")) {
            saldo_type=TypedValue.TYPE_STCK;
        } else if (st_type.equals("PRCT")) {
            saldo_type=TypedValue.TYPE_PROZENT;
        }
        int pos1=12;
        boolean neg = (st.charAt(pos1)=='N');
        if (neg)
            pos1++;    
        if (withCurr) {
            curr = st.substring(pos1, pos1+3);
            pos1 += 3;
        } 
        return new TypedValue(
                            (neg?"-":"")+st.substring(pos1).replace(',','.'),
                            curr,
                            saldo_type);
    }


    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx) 
    {
        Properties result=msgstatus.getData();

        buffer.append(Swift.decodeUmlauts(result.getProperty(header + ".data536")));

        final SimpleDateFormat date_time_format = new SimpleDateFormat("yyyyMMdd hhmmss");
        final SimpleDateFormat date_only_format = new SimpleDateFormat("yyyyMMdd");

        while (buffer.length()!=0) {
            try {
                String onerecord=Swift.getOneBlock(buffer);

                GVRWPDepotUms.Entry entry=new GVRWPDepotUms.Entry();

                String st_timestamp=null;
                String st_date=null;
                String st_time=null;
                char option='C';
                int  i=0;

                while (true) {
                    //Parse allgemeine Informationen (Mandatory Sequence A General Information)
                    st_timestamp=Swift.getTagValue(onerecord,"98"+option,i++);
                    if (st_timestamp==null) {
                        if (option=='C') {
                            option='A';
                            i=0;
                        } else {
                            break;
                        }
                    } else {
                        if (st_timestamp.substring(1,5).equals("PREP")) {
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
                } else if (st_date != null) {
                    entry.timestamp=date_only_format.parse(st_date);
                }

                String st_depot=Swift.getTagValue(onerecord,"97A",0);
                int pos1=st_depot.indexOf("//");
                int pos2=st_depot.indexOf("/",pos1+2);
                if (pos2<0)
                    pos2=st_depot.length();
                entry.depot=new Konto();
                entry.depot.blz=st_depot.substring(pos1+2,pos2);
                if (pos2 < st_depot.length())
                    entry.depot.number=st_depot.substring(pos2+1);
                getMainPassport().fillAccountInfo(entry.depot);

                String st;
                i=0;
                // Parse einzelnes Finanzinstrument (Repetitive Optional Subsequence B1 Financial Instrument)
                st=Swift.getTagValue(onerecord,"17B",0);
                if (st.substring(st.indexOf("//")+2).equals("Y")) {
                    int fin_start=onerecord.indexOf(":16R:FIN");

                    while (true) {
                        int fin_end=onerecord.indexOf(":16S:FIN",fin_start);
                        if ((fin_end)==-1) {
                            break;
                        }

                        String oneinstrument=onerecord.substring(fin_start,fin_end+8);
                        fin_start+=oneinstrument.length();

                        FinancialInstrument instrument=new GVRWPDepotUms.Entry.FinancialInstrument();

                        int trans_start = oneinstrument.indexOf(":16R:TRAN\r\n");
                        String oneinstrument_header;
                        if (trans_start >= 0)
                            oneinstrument_header = oneinstrument.substring(0, trans_start+9);
                        else
                            oneinstrument_header = oneinstrument;
                        
                        st=Swift.getTagValue(oneinstrument_header,"35B",0);
                        boolean haveISIN=st.substring(0,5).equals("ISIN ");

                        if (haveISIN) {
                            pos1=st.indexOf("\r\n");
                            instrument.isin=st.substring(5,pos1);
                            if (pos1+2<st.length() && st.substring(pos1+2,pos1+6).equals("/DE/")) {
                                pos2=st.indexOf("\r\n",pos1+6);
                                if (pos2==-1) {
                                    pos2=st.length();
                                }
                                instrument.wkn=st.substring(pos1+6,pos2);
                                pos1=pos2;
                            }
                        } else {
                            pos1=st.indexOf("\r\n");
                            instrument.wkn=st.substring(4,pos1);
                        }

                        pos1+=2;
                        if (pos1<st.length())
                            instrument.name=st.substring(pos1);

                        if (instrument.name!=null) {
                            StringBuffer sb=new StringBuffer(instrument.name);
                            int p;
                            while ((p=sb.indexOf("\r\n"))!=-1) {
                                sb.replace(p,p+2," ");
                            }
                            instrument.name=sb.toString();
                        }
                        i=0;
                        while (true) {
                            st=Swift.getTagValue(oneinstrument_header,"93B",i++);
                            if (st==null)
                                break;
                            String qualifier = st.substring(1,5);

                            if ("FIOP".equals(qualifier) || (instrument.startSaldo == null && "INOP".equals(qualifier))) {
                                instrument.startSaldo = parseTypedValue(st);
                            } else if ("FICL".equals(qualifier) || (instrument.endSaldo == null && "INCL".equals(qualifier))) {
                                instrument.endSaldo   = parseTypedValue(st);
                            } else {
                                System.out.println("Unbekannter 93B: " + st);
                            }
                        }
                        
                        i=0;
                        while (true) {
                            st=Swift.getTagValue(oneinstrument_header,"98A",i++);
                            if (st==null)
                                break;
                            String qualifier = st.substring(1,5);

                            if ("PRIC".equals(qualifier)) {
                                instrument.preisdatum = date_only_format.parse(st.substring(7, 15));
                            } else {
                                System.out.println("Unbekannter 98A: " + st);
                            }
                        }

                        i=0;
                        while (true) {
                            st=Swift.getTagValue(oneinstrument_header,"90A",i++);
                            if (st==null)
                                break;
                            
                            instrument.preis = parseTypedValue(st);
                        }
                        
                        i=0;
                        while (true) {
                            st=Swift.getTagValue(oneinstrument_header,"90B",i++);
                            if (st==null)
                                break;
                            
                            instrument.preis = parseTypedValue(st);
                        }
                        
                        //Parse einzelne Transaktionen 
                        while (trans_start >= 0) {
                            int trans_end = oneinstrument.indexOf(":16S:TRAN\r\n", trans_start);
                            if (trans_end<0)
                                break;
                            String onetransaction = oneinstrument.substring(trans_start, trans_end+9);
                            trans_start=trans_end+9;

                            Transaction transaction = new Transaction();

                            int link_start = onetransaction.indexOf(":16R:LINK");
                            if (link_start >=0) {
                                int link_end = onetransaction.indexOf(":16S:LINK", link_start);
                                if (link_end >= 0) {
                                    String onelink = onetransaction.substring(link_start, link_end+8);
                                    String rela = Swift.getTagValue(onelink, "20C", 0);

                                    if (rela != null) {
                                        transaction.kundenreferenz = rela.substring(7);
                                    }
                                }
                            }

                            int detail_start = onetransaction.indexOf(":16R:TRANSDET");
                            if (detail_start >= 0) {
                                int detail_end = onetransaction.indexOf(":16S:TRANSDET", detail_start);
                                if (detail_end >= 0) {
                                    String onedetail = onetransaction.substring(detail_start, detail_end+12);

                                    String quantity = Swift.getTagValue(onedetail, "36B", 0);
                                    if (quantity != null)
                                        if (quantity.startsWith(":PSTA")) {
                                            transaction.anzahl = parseTypedValue(quantity);
                                        } else {
                                            System.out.println("Unbekannter 36B: " + quantity);
                                        }
                                    
                                    String t99a = Swift.getTagValue(onedetail, "99A", 0);
                                    if (t99a != null)
                                        if (t99a.startsWith(":DAAC")) {
                                            int neg = 0;
                                            if (t99a.charAt(7) == 'N')
                                                neg = 1;
                                            transaction.stueckzins_tage = Integer.parseInt(t99a.substring(7+neg));
                                            if (neg != 0)
                                                transaction.stueckzins_tage = -transaction.stueckzins_tage;
                                        } else {
                                            System.out.println("Unbekannter 99A: " + t99a);
                                        }
                                    
                                    int tagidx = 0;
                                    while (true) {
                                        String t19a = Swift.getTagValue(onedetail, "19A", tagidx++);
                                        if (t19a == null)
                                            break;

                                        if (t19a.startsWith(":PSTA")) {
                                            int off=7;
                                            if (t19a.charAt(off)=='N') 
                                                off++;
                                            transaction.betrag=new BigDecimalValue(
                                                            t19a.substring(off+3).replace(',','.'),
                                                            t19a.substring(off,off+3));
                                            if (off>7)
                                                transaction.betrag.setValue(transaction.betrag.getValue().negate());
                                        } else if (t19a.startsWith(":ACRU")) {
                                            int off=7;
                                            if (t19a.charAt(off)=='N') 
                                                off++;
                                            transaction.stueckzinsen=new BigDecimalValue(
                                                            t19a.substring(off+3).replace(',','.'),
                                                            t19a.substring(off,off+3));
                                            if (off>7)
                                                transaction.stueckzinsen.setValue(transaction.stueckzinsen.getValue().negate());
                                        } else {
                                            System.out.println("Unbekannter 19A: " + t19a);
                                        }
                                    }

                                    tagidx=0;
                                    while (true) {
                                        String t22f = Swift.getTagValue(onedetail, "22F", tagidx++);
                                        if (t22f == null)
                                            break;

                                        if (t22f.startsWith(":TRAN")) {
                                            if (t22f.endsWith("SETT")) {
                                                transaction.transaction_indicator = Transaction.INDICATOR_SETTLEMENT_CLEARING;
                                            } else if (t22f.endsWith("CORP")) {
                                                transaction.transaction_indicator = Transaction.INDICATOR_CORPORATE_ACTION;
                                            } else if (t22f.endsWith("BOLE")) {
                                                transaction.transaction_indicator = Transaction.INDICATOR_LEIHE;
                                            } else if (t22f.endsWith("COLL")) {
                                                transaction.transaction_indicator = Transaction.INDICATOR_SICHERHEITEN;
                                            } else {
                                                System.out.println("Unbekannter 22F->TRAN: " + t22f);
                                                transaction.transaction_indicator = -1;
                                            }
                                        } else if (t22f.startsWith(":CCPT")) {
                                            if (t22f.endsWith("YCCP")) {
                                                transaction.ccp_eligibility = true;
                                            } else {
                                                System.out.println("Unbekannter 22F->CCPT: " + t22f);
                                            }
                                        } else {
                                            System.out.println("Unbekannter 22F: " + t22f);
                                        }
                                    }

                                    tagidx=0;
                                    while (true) {
                                        String t22h = Swift.getTagValue(onedetail, "22H", tagidx++);
                                        if (t22h == null)
                                            break;

                                        if (t22h.startsWith(":REDE")) {
                                            if (t22h.endsWith("DELI")) {
                                                transaction.richtung = Transaction.RICHTUNG_LIEFERUNG;
                                            } else if (t22h.endsWith("RECE")) {
                                                transaction.richtung = Transaction.RICHTUNG_ERHALT;
                                            } else {
                                                System.out.println("Unbekannter 22H->REDE: " + t22h);
                                                transaction.richtung = -1;
                                            }
                                        } else if (t22h.startsWith(":PAYM")) {
                                            if (t22h.endsWith("APMT")) {
                                                transaction.bezahlung = Transaction.BEZAHLUNG_GEGEN_ZAHLUNG;
                                            } else if (t22h.endsWith("FREE")) {
                                                transaction.bezahlung = Transaction.BEZAHLUNG_FREI;
                                            } else {
                                                System.out.println("Unbekannter 22H->PAYM: " + t22h);
                                                transaction.bezahlung = -1;
                                            }
                                        } else {
                                            System.out.println("Unbekannter 22F: " + t22h);
                                        }
                                    }

                                    tagidx=0;
                                    while (true) {
                                        String t98a = Swift.getTagValue(onedetail, "98A", tagidx++);
                                        if (t98a == null)
                                            break;
                                        
                                        if (t98a.startsWith(":ESET")) {
                                            String datum = t98a.substring(7);
                                            transaction.datum = date_only_format.parse(datum);
                                        } else if (t98a.startsWith(":SETT")) {
                                            String datum = t98a.substring(7);
                                            transaction.datum_valuta = date_only_format.parse(datum);
                                        } else {
                                            System.out.println("Unbekannter 98A: " + t98a);
                                        }
                                    }
                                    
                                    String move = Swift.getTagValue(onedetail, "25D", 0);
                                    if (move != null) 
                                        if (move.startsWith(":MOVE")) {
                                            if (move.endsWith("REVE"))
                                                transaction.storno = true;
                                        } else  {
                                            System.out.println("Unbekannter 25D: " + move);
                                        }
                                    
                                    String freitext = Swift.getTagValue(onedetail, "70E", 0);
                                    if (freitext != null) 
                                        if (freitext.startsWith(":TRDE")) {
                                            transaction.freitext_details = freitext.substring(7);
                                        } else  {
                                            System.out.println("Unbekannter 70E: " + freitext);
                                        }
                                }
                            }

                            int party_start = onetransaction.indexOf(":16R:SETPRTY");
                            if (party_start >=0) {
                                int party_end = onetransaction.indexOf(":16S:SETPRTY", party_start);
                                if (party_end >= 0) {
                                    String oneparty = onetransaction.substring(party_start, party_end+10);
                                    String deag = Swift.getTagValue(oneparty, "95Q", 0);

                                    if (deag != null) {
                                        transaction.gegenpartei = deag.substring(7);
                                    }
                                }
                            }
                            
                            instrument.transactions.add(transaction);
                            trans_start = oneinstrument.indexOf(":16R:TRAN\r\n", trans_start);
                        }
                        entry.instruments.add(instrument);
                    }
                } 
                ((GVRWPDepotUms)jobResult).addEntry(entry);
                buffer.delete(0,onerecord.length());
            } catch (Exception e) {
                throw new HBCI_Exception("*** error while extracting data",e);
            }
        }

        ((GVRWPDepotUms)jobResult).rest=buffer.toString();                    
    }          

    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
