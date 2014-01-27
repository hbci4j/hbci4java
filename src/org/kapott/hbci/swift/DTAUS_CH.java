
/*  $Id: DTAUS_CH.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2008  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.swift;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public class DTAUS_CH
{
    public class Transaction
    {
        public Konto other;
        public Value value;
        public List<String>  usage;
        
        public int idx;
        
        public Transaction(Konto other,Value value)
        {
            this.other=other;
            this.value=value;
            this.usage=new ArrayList<String>();
        }
        
        public void addUsage(String st)
        {
            usage.add(st);
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            
            ret.append("01");
            ret.append(new SimpleDateFormat("yyMMdd").format(now));
            ret.append(expand(other.blz,12,(byte)0x20,ALIGN_LEFT));
            ret.append(expand("",5,(byte)0x30,ALIGN_LEFT));
            ret.append(new SimpleDateFormat("yyMMdd").format(now));
            ret.append(expand(myAccount.blz,7,(byte)0x20,ALIGN_LEFT));
            ret.append(expand("",5,(byte)0x20,ALIGN_LEFT));
            ret.append(expand(Integer.toString(idx),5,(byte)0x30,ALIGN_RIGHT));
            ret.append("827");
            ret.append("00");
            
            ret.append(expand("",5,(byte)0x20,ALIGN_LEFT));
            ret.append(expand("TAN"+Integer.toString(idx),11,(byte)0x20,ALIGN_LEFT));
            
            ret.append(expand(myAccount.number,24,(byte)0x20,ALIGN_LEFT));
            
            ret.append(expand("",6,(byte)0x20,ALIGN_LEFT));
            ret.append(value.getCurr());
            ret.append(expand(new DecimalFormat("0.00").format(value.getBigDecimalValue()).replace('.',','),12,(byte)0x20,ALIGN_LEFT));
            total+=value.getLongValue();
            
            ret.append(expand("",14,(byte)0x20,ALIGN_LEFT));
            
            ret.append("02");
            ret.append(expand(myAccount.name,24,(byte)0x20,ALIGN_LEFT));
            for (int i=0;i<3;i++) {
                ret.append(expand((i<myAddress.length?myAddress[i]:""),
                        24,(byte)0x20,ALIGN_LEFT));
            }
            ret.append(expand("",30,(byte)0x20,ALIGN_LEFT));
            
            ret.append("03");
            ret.append("/C/");
            ret.append(expand(other.number,27,(byte)0x20,ALIGN_LEFT));
            ret.append(expand(other.name,24,(byte)0x20,ALIGN_LEFT));
            for (int i=0;i<3;i++) {
                ret.append(expand("",24,(byte)0x20,ALIGN_LEFT));
            }
            
            ret.append("04");
            for (int i=0;i<4;i++) {
                ret.append(expand((i<usage.size()?(String)usage.get(i):""),
                        28,(byte)0x20,ALIGN_LEFT));
            }
            ret.append(expand("",14,(byte)0x20,ALIGN_LEFT));
            
            return ret.toString();
        }
    }
    
    private List<Transaction>     entries;
    private Konto    myAccount;
    private String[] myAddress;
    private int      counter;
    
    private Date now;
    private long total;
    
    private static final byte ALIGN_LEFT=1;
    private static final byte ALIGN_RIGHT=2;
    
    public DTAUS_CH(Konto myAccount,String[] myAddress)
    {
        if (!myAccount.country.equals("CH"))
            throw new InvalidArgumentException("*** can only be used with swiss accounts");
        
        this.entries=new ArrayList<Transaction>();
        this.myAccount=myAccount;
        this.myAddress=((myAddress!=null)?myAddress:new String[0]);
        this.counter=0;
    }
    
    public void addEntry(Transaction entry)
    {
        entry.idx=++counter;
        entries.add(entry);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        now=new Date();
        total=0;
        
        for (Iterator<Transaction> i=entries.iterator();i.hasNext();) {
            Transaction trans= i.next();
            ret.append(trans.toString());
        }
        
        ret.append("01");
        ret.append(expand("",6,(byte)0x30,ALIGN_LEFT));
        ret.append(expand("",12,(byte)0x20,ALIGN_LEFT));
        ret.append(expand("",5,(byte)0x30,ALIGN_LEFT));
        ret.append(new SimpleDateFormat("yyMMdd").format(now));
        ret.append(expand("",7,(byte)0x20,ALIGN_LEFT));
        ret.append(expand("",5,(byte)0x20,ALIGN_LEFT));
        ret.append(expand(Integer.toString(counter+1),5,(byte)0x30,ALIGN_RIGHT));
        ret.append("890");
        ret.append("00");
        
        ret.append(expand(new DecimalFormat("0.000").format(total/100.0).replace('.',','),16,(byte)0x20,ALIGN_LEFT));
        ret.append(expand("",59,(byte)0x20,ALIGN_LEFT));
        
        return ret.toString();
    }
    
    private String expand(String st,int len,byte filler,int align)
    {
        if (st.length()<len) {
            try {
                byte[] fill=new byte[len-st.length()];
                Arrays.fill(fill,filler);
                String fillst=new String(fill,"ISO-8859-1");
                
                if (align==ALIGN_LEFT)
                    st=st+fillst;
                else if (align==ALIGN_RIGHT)
                    st=fillst+st;
                else
                    throw new HBCI_Exception("*** invalid align type: "+align);
            } catch (Exception e) {
                throw new HBCI_Exception(e);
            }
        } else if (st.length()>len) {
            throw new InvalidArgumentException("*** string too long: \""+st+"\" has "+st.length()+" chars, but max is "+len);
        }
        
        return st;
    }
}
