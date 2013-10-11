
/*  $Id: DTAUS.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

package org.kapott.hbci.swift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.datatypes.SyntaxDTAUS;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** <p>Hilfsklasse zum Erzeugen von DTAUS-Datensätzen für die Verwendung in
    Sammelüberweisungen und Sammellastschriften. Diese Klasse kann verwendet
    werden, um den DTAUS-Datenstrom zu erzeugen, der für Sammellastschriften
    und -überweisungen als Job-Parameter angegeben werden muss.</p>
    <p>In einem DTAUS-Objekt werden ein oder mehrere Transaktionen gespeichert. 
    Dabei müssen alle Transaktionen entweder Lastschriften oder Überweisungen sein. 
    Außerdem wird für alle Transaktionen das gleiche "Auftraggeberkonto" 
    angenommen (bei Überweisungen also das Belastungskonto, bei Lastschriften 
    das Konto, auf das der Betrag gutgeschrieben wird).</p>
    <p>In der Regel wird zunächst ein <code>DTAUS</code>-Objekt erzeugt. Dazu
    wird der Konstruktor {@link #DTAUS(Konto,int)}
    verwendet, womit gleichzeit das zu verwendende Auftraggeberkonto und der
    Typ des Sammelauftrages (<code>TYPE_CREDIT</code> für Sammelüberweisungen,
    <code>TYPE_DEBIT</code> für Sammellastschriften) festgelegt wird.
    Anschließend können beliebig viele {@link DTAUS.Transaction}-Objekte
    erzeugt werden, welche jeweils eine Transaktion darstellen. Jedes so erzeugte
    Objekt kann mit {@link #addEntry(DTAUS.Transaction)}
    zum Sammelauftrag hinzugefügt werden. Die Methode {@link #toString()} 
    liefert schließlich den so erzeugten Sammelauftrag im DTAUS-Format.</p> */
// TODO: API ändern (Setter/Getter), damit wir sauber die LogFilter für
// kritische Daten setzen können
public class DTAUS 
{
    /** Daten einer einzelnen Transaktion, die in einen Sammelauftrag
        übernommen werden soll. Vor dem Hinzufügen dieser Transaktion zum
        Sammelauftrag müssen alle Felder dieses Transaktions-Objektes mit den
        jeweiligen Auftragsdaten gefüllt werden. */
    public class Transaction
    {
        /** <p>Konto des Zahlungsempfängers bzw. des Zahlungspflichtigen. Soll
            dieser Einzelauftrag in eine Sammelüberweisung eingestellt werden,
            so muss in diesem Feld die Kontoverbindung des Zahlungsempfängers
            eingestellt werden. Bei Sammellastschriften ist hier die 
            Kontoverbindung des Zahlungspflichtigen einzustellen.</p>
            <p>Von dem verwendeten {@link Konto}-Objekt müssen mindestens die
            Felder <code>blz</code>, <code>number</code> und <code>name</code>
            richtig belegt sein.</p> */
        public Konto otherAccount;
        
        /** interne Kunden-ID. Wie die verwendet wird weiß ich leider nicht
            genau, kann im Prinzip leer gelassen werden (ansonsten Maximallänge
            11 Zeichen). */
        public String internalCustomerId;
        
        /** Textschlüssel für den Auftrag. Bei Sammelüberweisungen ist dieses
            Feld mit '51' vorbelegt, bei Sammellastschriften mit '05'. Dieser
            Wert kann überschrieben werden, gültige Werte finden sich in den 
            Job-Restrictions 
            (siehe {@link org.kapott.hbci.GV.HBCIJob#getJobRestrictions()}). */
        public String key;
        
        /** Zusätzlicher Textschlüssel (wird i.d.R. bankintern verwendet).
            Dieser Wert muss aus drei Ziffern bestehen und ist mit '000'
            vorbelegt. Das manuelle Setzen dieses Wertes ist in den meisten
            Fällen nicht nötig (außer für Leute, die wissen was sie tun ;-) ). */
        public String addkey;
        
        /** Geldbetrag, der bei diesem Einzelauftrag überwiesen (Sammelüberweisungen)
            bzw. eingezogen (Sammellastschriften) werden soll */
        public Value value;
        
        private ArrayList<String> usage;
        
        /** Erzeugen eine neuen Objektes für die Aufnahme von Daten für eine
            Transaktion */
        public Transaction()
        {
            addkey="000";
            key=(type==TYPE_CREDIT?"51":"05");
            usage=new ArrayList<String>();
        }
        
        /** Hinzufügen einer Verwendungszweckzeile zu diesem Auftrag. */
        public void addUsage(String st)
        {
        	LogFilter.getInstance().addSecretData(st,"X",LogFilter.FILTER_MOST);
            usage.add(st);
        }
        
        /** Gibt eine Liste der Verwendungszweckzeilen (String) zurück. */
        public List<String> getUsage()
        {
            return usage;
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            
            try {
                ret.append("0000C");
                ret.append(expand(myAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
                ret.append(expand(otherAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
                ret.append(expand(otherAccount.number,10,(byte)0x30,ALIGN_RIGHT));

                sumBLZ+=Long.parseLong(otherAccount.blz);
                sumNumber+=Long.parseLong(otherAccount.number);

                if (internalCustomerId==null) {
                    ret.append(expand("",13,(byte)0x30,ALIGN_LEFT));
                } else {
                    ret.append((char)0);
                    ret.append(expand(SyntaxDTAUS.check(internalCustomerId),11,(byte)0x30,ALIGN_LEFT));
                    ret.append((char)0);
                }

                ret.append(expand(key,2,(byte)0x30,ALIGN_RIGHT));
                ret.append(expand(addkey,3,(byte)0x30,ALIGN_RIGHT));
                ret.append((char)0x20);
                ret.append(expand(Long.toString(value.getCurr().equals("DEM")?value.getLongValue():0),11,(byte)0x30,ALIGN_RIGHT));
                ret.append(expand(myAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
                ret.append(expand(myAccount.number,10,(byte)0x30,ALIGN_RIGHT));
                ret.append(expand(Long.toString(value.getCurr().equals("EUR")?value.getLongValue():0),11,(byte)0x30,ALIGN_RIGHT));
                ret.append(expand("",3,(byte)0x20,ALIGN_LEFT));
                ret.append(expand(SyntaxDTAUS.check(otherAccount.name),27,(byte)0x20,ALIGN_LEFT));
                ret.append(expand("",8,(byte)0x20,ALIGN_LEFT));

                if (value.getCurr().equals("EUR"))
                    sumEUR+=value.getLongValue();
                else if (value.getCurr().equals("DEM"))
                    sumDM+=value.getLongValue(); 

                ret.append(expand(SyntaxDTAUS.check(myAccount.name),27,(byte)0x20,ALIGN_LEFT));

                String st="";
                if (usage.size()!=0)
                    st=SyntaxDTAUS.check(usage.get(0));
                ret.append(expand(st,27,(byte)0x20,ALIGN_LEFT));

                ret.append((char)curr);
                ret.append(expand("",2,(byte)0x20,ALIGN_LEFT));

                int posForNumOfExt=ret.length();
                ret.append("00");

                int basicLenOfCSet=128+27+27+5;
                int realLenOfCSet=basicLenOfCSet;
                int numOfExt=0;

                // erweiterungsteile
                // TODO: name2 für myAccount und otherAccount vorerst weggelassen

                for (int i=1;i<usage.size();i++) {
                    st=SyntaxDTAUS.check(usage.get(i));

                    if (((realLenOfCSet%128)+29)>128) {
                        int diff=128-(realLenOfCSet%128);
                        ret.append(expand("",diff,(byte)0x20,ALIGN_LEFT));
                        realLenOfCSet+=diff;
                    }

                    ret.append("02");
                    ret.append(expand(st,27,(byte)0x20,ALIGN_LEFT));
                    realLenOfCSet+=29;
                    numOfExt++;
                }

                if ((realLenOfCSet%128)!=0) {
                    int diff=128-(realLenOfCSet%128);
                    ret.append(expand("",diff,(byte)0x20,ALIGN_LEFT));
                    realLenOfCSet+=diff;
                }

                ret.replace(posForNumOfExt,posForNumOfExt+2,expand(Integer.toString(numOfExt),2,(byte)0x30,ALIGN_RIGHT));
                ret.replace(0,4,expand(Integer.toString(basicLenOfCSet+29*numOfExt),4,(byte)0x30,ALIGN_RIGHT));
            } catch (NullPointerException e) {
                throw new HBCI_Exception("probably one or more DTAUS values which MUST be set are null - please refer the API doc", e);
            }
            
            return ret.toString();
        }
    }
    
    /** Typ des Sammelauftrages: Sammelüberweisung */
    public static final int TYPE_CREDIT=1;
    /** Typ des Sammelauftrages: Sammellastschrift */
    public static final int TYPE_DEBIT=2;
    
    /** TODO: doku fehlt */
    public static final byte CURR_DM=0x20;  
    /** TODO: doku fehlt */
    public static final byte CURR_EUR=0x31;
    
    private static final byte ALIGN_LEFT=1;
    private static final byte ALIGN_RIGHT=2;
    
    private Konto     myAccount;
    private int       type;
    private Date      execdate;
    private byte      curr;
    private String    referenceId;
    private ArrayList<Transaction> entries;
    
    private long sumDM;
    private long sumEUR;
    private long sumBLZ;
    private long sumNumber;
    
    /** Entspricht {@link #DTAUS(Konto, int, Date) DTAUS(myAccount,type,null)} */
    public DTAUS(Konto myAccount,int type)
    {
        this(myAccount,type,null);
    }
    
    /** Erzeugen eines neuen Objektes für die Aufnahme von
     Sammelaufträgen. <code>myAccount</code> ist dabei das "eigene" Konto, 
     welches bei Sammelüberweisungen als Belastungskonto und bei 
     Sammellastschriften als Gutschriftkonto verwendet wird. Von dem
     {@link Konto}-Objekt müssen mindestens die Felder <code>blz</code>,
     <code>number</code>, <code>curr</code> und <code>name</code> richtig
     gesetzt sein.  <br/>
     <code>execdate</code> gibt das Datum an, wann dieser Sammelauftrag 
     ausgeführt werden soll. ACHTUNG: <code>execdate</code> wird zur Zeit noch
     nicht ausgewertet! 
     @param myAccount Gegenkonto für die enthaltenen Aufträge 
     @param type <ul><li><code>TYPE_CREDIT</code> für Sammelüberweisungen,</li>
     <li><code>TYPE_DEBIT</code> für Sammellastschriften</li></ul>
     @param execdate Ausführungsdatum für diesen Sammelauftrag; <code>null</code>,
     wenn kein Ausführungsdatum gesetzt werden soll (sofortige Ausführung) */
    public DTAUS(Konto myAccount,int type,Date execdate)
    {
    	LogFilter.getInstance().addSecretData(myAccount.blz,"X",LogFilter.FILTER_MOST);
    	LogFilter.getInstance().addSecretData(myAccount.customerid,"X",LogFilter.FILTER_IDS);
    	LogFilter.getInstance().addSecretData(myAccount.name,"X",LogFilter.FILTER_IDS);
    	LogFilter.getInstance().addSecretData(myAccount.name2,"X",LogFilter.FILTER_IDS);
    	LogFilter.getInstance().addSecretData(myAccount.number,"X",LogFilter.FILTER_IDS);
    	LogFilter.getInstance().addSecretData(myAccount.subnumber,"X",LogFilter.FILTER_MOST);

    	this.myAccount=myAccount;
        this.type=type;
        this.execdate=execdate;
        
        entries=new ArrayList<Transaction>();
        
        if (myAccount.curr.equals("EUR"))
            this.curr=CURR_EUR;
        else if (myAccount.curr.equals("DEM"))
            this.curr=CURR_DM;
        else
            throw new InvalidUserDataException("*** invalid currency of this account: "+myAccount.curr);
    }
    
    /** TODO: doku fehlt */
    public DTAUS(String dtaus)
    {
        entries=new ArrayList<Transaction>();
        parseDTAUS(dtaus);
    }
    
    /** Hinzufügen eines einzelnen Auftrages zu diesem Sammelauftrag. Das
        {@link DTAUS.Transaction}-Objekt, welches hier als Argument benötigt wird,
        muss mit '<code>dtaus.new&nbsp;Transaction()</code>' erzeugt werden 
        ('<code>dtaus</code>' ist dabei das aktuelle <code>DTAUS</code>-Objekt).
        @param entry Hinzuzufügender Einzelauftrag */ 
    public void addEntry(Transaction entry)
    {
        entries.add(entry);
    }
    
    /** TODO: doku fehlt */
    public byte getCurr()
    {
        return curr;
    }

    /** TODO: doku fehlt */
    public ArrayList<Transaction> getEntries()
    {
        return entries;
    }

    /** TODO: doku fehlt */
    public Date getExecdate()
    {
        return execdate;
    }

    /** TODO: doku fehlt */
    public Konto getMyAccount()
    {
        return myAccount;
    }

    /** TODO: doku fehlt */
    public int getType()
    {
        return type;
    }
    
    /** Setzt das Feld Nr 10 ("Referennummer des Einreichers") */
    public void setReferenceId(String referenceId)
    {
        this.referenceId = referenceId;
    }
    
    /** Gibt den Wert von Feld Nr 10 ("Referenznummer des Einreichers") zurück */
    public String getReferenceId()
    {
        return (this.referenceId!=null)?this.referenceId:"";
    }

    /** Rückgabe des Sammelauftrages im DTAUS-Format. Der Rückgabewert dieser
        Methode kann direkt als Parameterwert für den Parameter '<code>data</code>'
        bei Sammelaufträgen verwendet werden (für eine Parameterbeschreibung
        siehe Paketbeschreibung des Paketes <code>org.kapott.hbci.GV</code>).
        @return DTAUS-Datenstrom für diesen Sammelauftrag */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        sumBLZ=0;
        sumNumber=0;
        sumDM=0;
        sumEUR=0;
        
        // A-set
        ret.append("0128A");
        switch (type) {
            case TYPE_CREDIT:
                ret.append("GK");
                break;
            case TYPE_DEBIT:
                ret.append("LK");
                break;
            default:
                throw new InvalidUserDataException("*** type of DTAUS order not set (DEBIT/CREDIT)");
        }
        
        ret.append(expand(myAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
        ret.append(expand("",8,(byte)0x30,ALIGN_LEFT));
        ret.append(expand(SyntaxDTAUS.check(myAccount.name),27,(byte)0x20,ALIGN_LEFT));
        
        SimpleDateFormat form=new SimpleDateFormat("ddMMyy");
        ret.append(form.format(new Date()));
        
        ret.append(expand("", 4, (byte)0x20, ALIGN_LEFT));
        ret.append(expand(myAccount.number, 10, (byte)0x30, ALIGN_RIGHT));
        ret.append(expand(getReferenceId(), 10, (byte)0x30, ALIGN_RIGHT));
        ret.append(expand("", 15, (byte)0x20, ALIGN_LEFT));
        
        if (execdate==null) {
            ret.append(expand("",8,(byte)0x20,ALIGN_LEFT));
        } else {
            form=new SimpleDateFormat("ddMMyyyy");
            ret.append(form.format(execdate));
        }
        
        ret.append(expand("",24,(byte)0x20,ALIGN_LEFT));
        ret.append((char)curr);
        
        // C-sets
        for (Iterator<Transaction> i=entries.iterator();i.hasNext();) {
            Transaction entry= i.next();
            ret.append(entry.toString());
        }
        
        // E-set
        ret.append("0128E");
        ret.append(expand("",5,(byte)0x20,ALIGN_LEFT));
        ret.append(expand(Integer.toString(entries.size()),7,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(curr==CURR_DM?sumDM:0),
                        13,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(sumNumber),17,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(sumBLZ),17,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(curr==CURR_EUR?sumEUR:0),
                        13,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand("",51,(byte)0x20,ALIGN_LEFT));
        
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
    
    private void parseDTAUS(String dtaus)
    {
        HBCIUtils.log("parsing DTAUS data",HBCIUtils.LOG_DEBUG);
        
        // satz A
        String header=dtaus.substring(0,5);
        if (!header.equals("0128A")) {
            throw new HBCI_Exception("*** DTAUS stream does not start with '0128A'");
        }
        
        char typ=dtaus.charAt(5);
        if (typ=='G') {
            this.type=TYPE_CREDIT;
        } else if (typ=='L') {
            this.type=TYPE_DEBIT;
        } else {
            throw new HBCI_Exception("*** Invalid type: "+typ);
        }
        
        setReferenceId(dtaus.substring(70, 80).trim());
        
        String myBLZ=dtaus.substring(7,15).trim();
        String myName=dtaus.substring(23,50).trim();
        String myNumber=dtaus.substring(60,70).trim();
        
        try {
            SimpleDateFormat format=new SimpleDateFormat("ddMMyyyy");
            this.execdate=format.parse(dtaus.substring(95,103).trim());
        } catch (ParseException e) {
            this.execdate=null;
        }
        
        this.curr=(byte)dtaus.charAt(127);
        if (this.curr!=CURR_DM && this.curr!=CURR_EUR) {
            throw new HBCI_Exception("*** Invalid currency: "+this.curr);
        }

        this.myAccount=new Konto("DE",myBLZ,myNumber);
        this.myAccount.curr=(this.curr==CURR_EUR)?"EUR":"DEM";
        this.myAccount.name=myName;
        
        // satz C beginn
        int posi=128;
        
        // schleife für einzelne aufträge (c-sets)
        while (true) {
            Transaction entry=new Transaction();
            
            if (dtaus.charAt(posi+4)!='C') {
                // gefundener abschnitt ist kein c-set
                break;
            }
            
            int setCLen=Integer.parseInt(dtaus.substring(posi,posi+4));
            posi+=4;
            HBCIUtils.log("SetCLen = "+setCLen+" data bytes (--> "+((setCLen-187)/29.0)+" extensions)", HBCIUtils.LOG_DEBUG);
            
            // "C" überspringen
            posi++;
            
            // skip myBLZ
            posi+=8;
            
            String otherBLZ=dtaus.substring(posi,posi+8).trim();
            posi+=8;
            
            String otherNumber=dtaus.substring(posi,posi+10).trim();
            posi+=10;
           
            entry.internalCustomerId=dtaus.substring(posi+1,posi+1+11).trim();
            posi+=13;
            
            entry.key=dtaus.substring(posi,posi+2).trim();
            posi+=2;
            
            entry.addkey=dtaus.substring(posi,posi+3).trim();
            posi+=3;
            
            // skip bankintern
            posi++;
            
            String value_st=null;
            if (this.curr==CURR_EUR) {
                value_st=dtaus.substring(posi+29,posi+29+11).trim();
            } else {
                value_st=dtaus.substring(posi,posi+11).trim();
            }
            posi+=40;
            
            // skip reserve
            posi+=3;
            
            String otherName=dtaus.substring(posi,posi+27).trim();
            posi+=27;
            
            // skip fillbytes
            posi+=8;
            
            // skip myName
            posi+=27;
            
            entry.addUsage(dtaus.substring(posi,posi+27).trim());
            posi+=27;
            
            // skip währung
            // TODO: hier konsistenz überprüfen
            posi++;
            
            // skip reserve
            posi+=2;
            
            int nofExtensions=Integer.parseInt(dtaus.substring(posi,posi+2));
            posi+=2;
            HBCIUtils.log("field 'nofExtensions' = "+nofExtensions,HBCIUtils.LOG_DEBUG);
            
            String otherName2=null;
            for (int i=0;i<nofExtensions;i++) {
                if ((posi%128)+29 > 128) {
                    posi=((posi/128)+1)*128;
                }
                
                String code=dtaus.substring(posi,posi+2).trim();
                posi+=2;
                
                String data=dtaus.substring(posi,posi+27).trim();
                posi+=27;
                
                if (code.equals("01")) {
                    otherName2=data;
                } else if (code.equals("02")) {
                    entry.addUsage(data);
                } else if (code.equals("03")) {
                    this.myAccount.name2=data;
                }
            }
            posi=((posi/128)+1)*128;
            
            entry.otherAccount=new Konto("DE",otherBLZ,otherNumber);
            entry.otherAccount.curr=(this.curr==CURR_EUR)?"EUR":"DEM";
            entry.otherAccount.name=otherName;
            entry.otherAccount.name2=otherName2;
            entry.value=new Value(Long.parseLong(value_st), (this.curr==CURR_EUR)?"EUR":"DEM");
            
            addEntry(entry);
        }
        
        // e-satz
        if (!dtaus.substring(posi,posi+5).equals("0128E")) {
            throw new HBCI_Exception("*** e-set does not start with 0128E");
        }
        posi+=5;
        
        // skip reserve
        posi+=5;
        
        int x=Integer.parseInt(dtaus.substring(posi,posi+7));
        if (x!=entries.size()) {
            throw new HBCI_Exception("*** there were "+entries.size()+" c-sets, but e-set says "+x);
        }
        
        // TODO: restliche konsistenzchecks machen
        
        HBCIUtils.log("parsinng of DTAUS data finished", HBCIUtils.LOG_DEBUG);
    }
}
