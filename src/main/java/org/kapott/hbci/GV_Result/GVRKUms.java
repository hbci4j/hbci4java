
/*  $Id: GVRKUms.java,v 1.3 2012/01/27 22:52:25 willuhn Exp $

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

package org.kapott.hbci.GV_Result;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci.swift.Swift;

/** <p>Ergebnisse der Abfrage von Kontoumsatzinformationen.
    Ein Objekt dieser Klasse entspricht einen Kontoauszug.
    Ein Kontoauszug ist in einzelne Buchungstage unterteilt.
    Für jeden einzelnen Buchungstag wiederum gibt es eine Anzahl von Umsatzzeilen
    (das entspricht je einem Eintrag auf dem "normalen" Kontoauszug auf Papier).
    Jede einzelne Umsatzzeile wiederum enthält die einzelnen Informationen zu genau
    einer Transaktion. </p>
    <p>Es können auch alle Umsatzzeilen in einer einzigen Liste abgefragt werden (also nicht
    in Buchungstage unterteilt .</p>*/
public class GVRKUms
    extends HBCIJobResultImpl
{
    /** Eine "Zeile" des Kontoauszuges (enthält Daten einer Transaktion) */
    public static class UmsLine
        implements Serializable
    {
        /** Datum der Wertstellung */
        public Date   valuta;
        /** Buchungsdatum */
        public Date   bdate;

        /** Gebuchter Betrag */
        public Value  value;
        /** Handelt es sich um eine Storno-Buchung? */
        public boolean isStorno;
        /** Der Saldo <em>nach</em> dem Buchen des Betrages <code>value</code> */
        public Saldo  saldo;
        /** Kundenreferenz */
        public String customerref;
        /** Kreditinstituts-Referenz */
        public String instref;
        /** Ursprünglicher Betrag (bei ausländischen Buchungen; optional) */
        public Value  orig_value;
        /** Betrag für Gebühren des Geldverkehrs (optional) */
        public Value  charge_value;

        /** Art der Buchung (bankinterner Code). Nur wenn hier ein Wert ungleich
         * <code>999</code> drinsteht, enthalten die Attribute <code>text</code>,
         * <code>primanota</code>, <code>usage</code>, <code>other</code> und
         * <code>addkey</code> sinnvolle Werte. Andernfalls sind all diese
         * Informationen möglicherweise im Feld <code>additional</code> enthalten,
         * allerdings in einem nicht definierten Format (siehe auch
         * <code>additional</code>). */
        public String   gvcode;

        /** <p>Zusatzinformationen im Rohformat. Wenn Zusatzinformationen zu dieser
            Transaktion in einem unbekannten Format vorliegen, dann enthält dieser
            String diese Daten (u.U. ist dieser String leer, aber nicht <code>null</code>).
        Das ist genau dann der Fall, wenn der Wert von  <code>gvcode</code> gleich <code>999</code> ist.</p>
            <p>Wenn die Zusatzinformationen aber ausgewertet werden können (und <code>gvcode!=999</code>),
        so ist dieser String <code>null</code>, und die Felder <code>text</code>, <code>primanota</code>,
            <code>usage</code>, <code>other</code> und <code>addkey</code>
            enthalten die entsprechenden Werte (siehe auch <code>gvcode</code>)</p> */
        public String   additional;

        /** Beschreibung der Art der Buchung (optional).
         * Nur wenn <code>gvcode!=999</code>! (siehe auch <code>additional</code>
         * und <code>gvcode</code>)*/
        public String   text;
        /** Primanotakennzeichen (optional).
         * Nur wenn <code>gvcode!=999</code>! (siehe auch <code>additional</code>
         * und <code>gvcode</code>) */
        public String   primanota;
        /** Liste von Strings mit den Verwendungszweckzeilen.
         * Nur wenn <code>gvcode!=999</code>! (siehe auch <code>additional</code>
         * und <code>gvcode</code>)*/
        public List<String> usage;
        /** Gegenkonto der Buchung (optional).
         * Nur wenn <code>gvcode!=999</code>! (siehe auch <code>additional</code>
         * und <code>gvcode</code>) */
        public Konto    other;
        /** Erweiterte Informationen zur Art der Buchung (bankintern, optional).
         * Nur wenn <code>gvcode!=999</code>! (siehe auch <code>additional</code>
         * und <code>gvcode</code>) */
        public String   addkey;
        
        /** Gibt an, ob ein Umsatz ein SEPA-Umsatz ist **/
        public boolean isSepa;
        
        /**
         * Gibt an, ob ein Umsatz per CAMT abgerufen wurde.
         */
        public boolean isCamt;
        
        /**
         * NUR BEI CAMT: Eindeutiger Identifier der Buchung. Erst seit SEPA mit Abruf per CAMT
         * verfuegbar. Bei MT940-Abruf ist der Wert leer.
         */
        public String id;
        
        /**
         * NUR BEI CAMT: Der Purpose-Code der Buchung.
         */
        public String purposecode;

        public UmsLine()
        {
            usage=new ArrayList<String>();
            isSepa=false;
        }

        public void addUsage(String st)
        {
            if (st!=null) {
                usage.add(st);
            }
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String linesep=System.getProperty("line.separator");

            ret.append(HBCIUtils.date2StringLocal(valuta)).append(" ").append(HBCIUtils.date2StringLocal(bdate)).append(" ");
            ret.append(customerref).append(":").append(instref).append(" ");
            ret.append(value.toString());
            ret.append(isStorno?" (Storno)":"");
            if (orig_value!=null)
                ret.append(" (orig ").append(orig_value.toString()).append(")");
            if (charge_value!=null)
                ret.append(" (charge ").append(charge_value.toString()).append(")");
            ret.append(linesep);

            ret.append("    saldo: ").append(saldo.toString()).append(linesep);

            ret.append("    code ").append(gvcode).append(linesep);
            if (additional==null) {
                ret.append("    text:").append(text).append(linesep);
                ret.append("    primanota:").append(primanota).append(linesep);
                for (Iterator<String> i=usage.iterator(); i.hasNext(); ) {
                    ret.append("    usage:").append(i.next()).append(linesep);
                }
                if (other!=null)
                    ret.append("    konto:").append(other.toString()).append(linesep);
                ret.append("    addkey:").append(addkey);
            }
            else ret.append("    ").append(additional);

            return ret.toString().trim();
        }
    }

    /** Enthält alle Transaktionen eines einzelnen Buchungstages. Dazu gehören
        das Datum des jeweiligen Tages, der Anfangs- und Endsaldo sowie die
        Menge aller dazugehörigen Umsatzeilen */
    public static class BTag
        implements Serializable
    {
        /** <p>Konto, auf das sich die Umsatzdaten beziehen (Kundenkonto). Einige
            Kreditinstitute geben fehlerhafte Kontoauszüge zurück, was zur Folge
            haben kann, dass dieses Feld nicht richtig belegt ist. Tritt ein solcher
            Fall ein, so kann es vorkommen, dass von dem <code>Konto</code>-Objekt
            nur das Feld <code>number</code> gefüllt ist, und zwar mit den
            Informationen, die das Kreditinstitut zur Identifizierung dieses Kontos
            zurückgibt.</p>
            <p>Normalerweise bestehen diese Informationen aus BLZ und
            Kontonummer, die dann auch korrekt in das <code>Konto</code>-Objekt
            eingetragen werden. Liegen diese Informationen aber gar nicht oder in
            einem falschen bzw. unbekannten Format vor, so werden diese Daten
            komplett in das <code>number</code>-Feld geschrieben.</p> */
        public Konto     my;
        /** Nummer des Kontoauszuges (optional) */
        public String    counter;
        /** Saldo zu Beginn des Buchungstages */
        public Saldo     start;
        /** Art des Saldos. <code>M</code> = Anfangssaldo; <code>F</code> = Zwischensaldo */
        public char      starttype;
        /** Liste der einzelnen Buchungen dieses Tages (Instanzen von {@link GVRKUms.UmsLine}) */
        public List<UmsLine>     lines;
        /** Saldo am Ende des Buchungstages */
        public Saldo     end;
        /** Art des Endsaldos (siehe {@link #starttype}) */
        public char      endtype;

        public BTag()
        {
            lines=new ArrayList<UmsLine>();
        }

        public void addLine(UmsLine line)
        {
            lines.add(line);
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String linesep=System.getProperty("line.separator");

            ret.append("Konto ").append(my.toString()).append(" - Auszugsnummer ").append(counter).append(linesep);
            ret.append("  ").append((starttype=='F'?"Anfangs":"Zwischen")).append("saldo: ").append(start.toString()).append(linesep);

            for (Iterator<UmsLine> i=lines.iterator(); i.hasNext(); ) {
                ret.append("  ").append(i.next().toString()).append(linesep);
            }

            ret.append("  ").append((endtype=='F'?"Schluss":"Zwischen")).append("saldo: ").append(end.toString());
            return ret.toString().trim();
        }
    }

    private StringBuffer bufferMT940;
    private StringBuffer bufferMT942;

    private List<BTag> tageMT940;
    private List<BTag> tageMT942;

    private boolean parsed;

    /** Dieses Feld enthält einen String, der den nicht-auswertbaren Teil der Kontoauszüge
     * enthält. Es dient nur zu Debugging-Zwecken und sollte eigentlich immer <code>null</code>
     * bzw. einen leeren String enthalten. Wenn das nicht der Fall ist, dann konnten die
     * empfangenen Kontoauszüge nicht richtig geparst werden, und dieser String enthält den
     * "Schwanz" der Kontoauszugsdaten, bei dem das Parsing-Problem aufgetreten ist. */
    public StringBuffer restMT940;

    /** Wie restMT940, allerdings für die Daten der *vorgemerkten* Umsätze. */
    public StringBuffer restMT942;


    public GVRKUms()
    {
        bufferMT940=new StringBuffer();
        bufferMT942=new StringBuffer();

        tageMT940=new ArrayList<BTag>();
        tageMT942=new ArrayList<BTag>();

        restMT940=new StringBuffer();
        restMT942=new StringBuffer();

        parsed=false;
    }

    public void appendMT940Data(String data)
    {
        this.bufferMT940.append(data);
    }

    public void appendMT942Data(String data)
    {
        this.bufferMT942.append(data);
    }

    /**
     * Gibt die Umsatzinformationen gruppiert nach Buchungstagen zurück.
     * @return Liste mit Informationen zu einzelnen Buchungstagen ({@link GVRKUms.BTag})
     **/
    public List<BTag> getDataPerDay()
    {
        verifyMT94xParsing("getDataPerDay()");
        return tageMT940;
    }
    
    /**
     * Gibt die vorgemerkten Umsaetze gruppiert nach Buchungstagen zurueck.
     * @return Liste mit Informationen zu einzelnen Buchungstagen der Vormerkbuchungen ({@link GVRKUms.BTag})
     **/
    public List<BTag> getDataPerDayUnbooked()
    {
        verifyMT94xParsing("getDataPerDayUnbooked()");
        return tageMT942;
    }

    
    /** Gibt alle Transaktionsdatensätze in einer "flachen" Struktur zurück.
        D.h. nicht in einzelne Buchungstage unterteilt, sondern in einer Liste
        analog zu einem "normalen" Kontoauszug.
        @return Liste mit Transaktionsdaten ({@link GVRKUms.UmsLine}) */
    public List<UmsLine> getFlatData()
    {
        verifyMT94xParsing("getFlatData()");

        List<UmsLine> result=new ArrayList<UmsLine>();
        for (Iterator<BTag> i=tageMT940.iterator(); i.hasNext(); ) {
            BTag tag= i.next();
            result.addAll(tag.lines);
        }

        return result;
    }

    /** Gibt eine Liste aller vorgemerkten Umsätze zurück
     * @return Liste von {@link GVRKUms.UmsLine}-Objekten der vorgemerkten Umsätze */
    public List<UmsLine> getFlatDataUnbooked()
    {
        verifyMT94xParsing("getFlatDataUnbooked()");

        List<UmsLine> result=new ArrayList<UmsLine>();
        for (Iterator<BTag> i=tageMT942.iterator(); i.hasNext(); ) {
            BTag tag= i.next();
            result.addAll(tag.lines);
        }

        return result;
    }

    public String toString()
    {
        verifyMT94xParsing("toString()");

        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");

        // mt940
        for (Iterator<UmsLine> i=getFlatData().iterator(); i.hasNext(); ) {
            ret.append(i.next().toString()).append(linesep);
        }
        ret.append("rest: ").append(restMT940).append(linesep).append(linesep);

        // mt942
        ret.append("not yet booked:").append(linesep);
        for (Iterator<UmsLine> i=getFlatDataUnbooked().iterator(); i.hasNext(); ) {
            ret.append(i.next().toString()).append(linesep);
        }
        ret.append("rest: ").append(restMT942);

        return ret.toString().trim();
    }

    private void verifyMT94xParsing(String where)
    {
        if (!parsed) {
            parseMT94x(bufferMT940, tageMT940, restMT940);
            parseMT94x(bufferMT942, tageMT942, restMT942);
        }

        if (restMT940!=null && restMT940.length()!=0) {
            HBCIUtils.log(
                where+
                ": mt940 has not been parsed successfully " +
                "- probably returned data will be incomplete. "+
                "check variable 'restMT940' (or set logging level to 4 (=DEBUG)) "+
                "to see the data that could not be parsed.",
                HBCIUtils.LOG_WARN);
            HBCIUtils.log("restMT940: "+restMT940, HBCIUtils.LOG_DEBUG);
        }

        if (restMT942!=null && restMT942.length()!=0) {
            HBCIUtils.log(
                where+
                ": mt942 has not been parsed successfully " +
                "- probably returned data will be incomplete. "+
                "check variable 'restMT942' (or set logging level to 4 (=DEBUG)) "+
                "to see the data that could not be parsed.",
                HBCIUtils.LOG_WARN);
            HBCIUtils.log("restMT942: "+restMT942, HBCIUtils.LOG_DEBUG);
        }
    }

    private void parseMT94x(StringBuffer buffer, List<BTag> tage, StringBuffer rest)
    {
        parsed = true;
        
        // Verwenden wir bei CAMT-Umsaetzen.
        if (buffer == null || buffer.length() == 0)
            return;
        
        HBCIUtils.log("now parsing MT94x data", HBCIUtils.LOG_DEBUG);

        try {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd");
            HBCIPassport     passport=getPassport();

            // split into "buchungstage"
            while (buffer.length()!=0) {
                String st_tag=Swift.getOneBlock(buffer);
                if (st_tag==null) {
                    break;
                }

                GVRKUms.BTag btag=new GVRKUms.BTag();

                // extract konto data
                String konto_info=Swift.getTagValue(st_tag,"25",0);
                int pos=konto_info.indexOf("/");
                String blz;
                String number;
                String iban;
                String curr;

                if (pos!=-1) {
                    blz=konto_info.substring(0,pos);
                    number=konto_info.substring(pos+1);
                    iban="";
                    curr="";

                    for (pos=number.length();pos>0;pos--) {
                        char ch=number.charAt(pos-1);

                        if (ch>='0' && ch<='9')
                            break;
                    }

                    if (pos<number.length()) {
                        curr=number.substring(pos);
                        number=number.substring(0,pos);
                    }
                } else {
                    blz="";
                    number="";
                    iban=konto_info;
                    curr="";
                }

                btag.my=new Konto();
                btag.my.blz=blz;
                btag.my.number=number;
                btag.my.iban=iban;
                btag.my.curr=curr;
                if (passport!=null) {
                    passport.fillAccountInfo(btag.my);
                }

                // extract "auszugsnummer"
                btag.counter=Swift.getTagValue(st_tag,"28C",0);

                // extract "anfangssaldo"
                String st_start=Swift.getTagValue(st_tag,"60F",0);
                char   starttype='F';
                if (st_start==null) {
                    st_start=Swift.getTagValue(st_tag,"60M",0);
                    starttype='M';
                }
                if (st_start!=null) {
                    // Tag 60 (Anfangssaldo) gibt es in MT942 nicht,
                    // darum wird btag.start nur in MT940 gefüllt

                    btag.start=new Saldo();
                    btag.starttype=starttype;

                    String cd=st_start.substring(0,1);

                    try {
                        btag.start.timestamp=dateFormat.parse(st_start.substring(1,7));
                    } catch (Exception e) {
                        btag.start.timestamp=null;
                    }

                    // hier aus dem CD-Indikator und dem absoluten Saldo-Betrag
                    // einen String für den Saldo-Betrag zusamennbauen
                    btag.start.value=new Value(
                        (cd.equals("D")?"-":"")+st_start.substring(10).replace(',','.'),
                        st_start.substring(7,10));
                }

                // looping to get all "umsaetze"

                // TODO: beim MT942 (btag.start==null) müsste als Initialwert
                // fuer den Saldo hier eigentlich der Abschluss-Saldo aus den
                // gebuchten Umsätzen verwendet werden (den habe ich an dieser
                // Stelle aber nicht so ohne weiteres)
                long saldo = (btag.start!=null)?btag.start.value.getLongValue():0;
                int  ums_counter=0;

                while (true) {
                    String st_ums=Swift.getTagValue(st_tag,"61",ums_counter);
                    if (st_ums==null)
                        break;

                    GVRKUms.UmsLine line=new GVRKUms.UmsLine();

                    // extract valuta
                    line.valuta=dateFormat.parse(st_ums.substring(0,6));

                    // extract bdate
                    int next=0;
                    if (st_ums.charAt(6)>'9') {
                        // [2012-01-27 - Patch von Frank/Pecunia]
                        // beim :61er Tag ist das Buchungsdatum optional. Wenn es nicht gesetzt ist, muss das Buchungsdatum des
                        // Umsatzes z.B. aus :60F kommen
                        if (btag.start !=  null && btag.start.timestamp != null) line.bdate = btag.start.timestamp;
                        else line.bdate=line.valuta;

                        next=6;

                    } else {
                        line.bdate=dateFormat.parse(st_ums.substring(0,2)+
                            st_ums.substring(6,10));

                        // wenn bdate und valuta um mehr als einen monat voneinander
                        // abweichen, dann ist das jahr des bdate falsch (1.1.2005 vs. 31.12.2004)
                        // korrektur des bdate-jahres in die richtige richtung notwendig
                        // FE: ein Monat reicht nicht, es sollte schon ein halbes Jahr sein - es gab verschiedene Probleme mit Umsaetzen im falschen Jahr!!
                        // http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=75348
                        if (Math.abs(line.bdate.getTime()-line.valuta.getTime())>180L*24*3600*1000) {
                            int diff;

                            if (line.bdate.before(line.valuta)) {
                                diff=+1;
                            } else {
                                diff=-1;
                            }
                            Calendar cal=Calendar.getInstance();
                            cal.setTime(line.bdate);
                            cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)+diff);
                            line.bdate=cal.getTime();
                        }

                        next=10;
                    }

                    // extract credit/debit
                    String cd;
                    if (st_ums.charAt(next)=='C' || st_ums.charAt(next)=='D') {
                        line.isStorno=false;
                        cd=st_ums.substring(next,next+1);
                        next++;
                    } else {
                        line.isStorno=true;
                        cd=st_ums.substring(next+1,next+2);
                        next+=2;
                    }

                    // skip part of currency
                    char currpart=st_ums.charAt(next);
                    if (currpart>'9')
                        next++;

                    line.value=new Value();

                    // TODO: bei einem MT942 wird die waehrung hier automatisch auf EUR
                    // gesetzt, weil die auto-erkennung (anhand des anfangssaldos) hier nicht
                    // funktioniert, weil es im MT942 keinen anfangssaldo gibt
                    line.value.setCurr((btag.start!=null)?btag.start.value.getCurr():"EUR");

                    // extract value and skip code
                    int    npos=st_ums.indexOf("N",next);
                    // welcher Code (C/D) zeigt einen negativen Buchungsbetrag
                    // an? Bei einer "normalen" Buchung ist das D(ebit). Bei
                    // einer Storno-Buchung ist der Betrag allerdings negativ,
                    // wenn eine ehemalige Gutschrift (Credit) storniert wird,
                    // in dem Fall wäre als "C" der Indikator für den negativen
                    // Buchungsbetrag
                    String negValueIndikator=line.isStorno?"C":"D";
                    line.value.setValue(
                        HBCIUtilsInternal.string2Long(
                            (cd.equals(negValueIndikator)?"-":"") + st_ums.substring(next,npos).replace(',','.'),
                            100));
                    next=npos+4;

                    // update saldo
                    saldo+=line.value.getLongValue();

                    line.saldo=new Saldo();
                    line.saldo.timestamp=line.bdate;
                    // TODO: bei einem MT942 wird die waehrung hier automatisch auf EUR
                    // gesetzt, weil die auto-erkennung (anhand des anfangssaldos) hier nicht
                    // funktioniert, weil es im MT942 keinen anfangssaldo gibt
                    line.saldo.value=new Value(saldo, (btag.start!=null)?btag.start.value.getCurr():"EUR");

                    // extract customerref
                    npos=st_ums.indexOf("//",next);
                    if (npos==-1)
                        npos=st_ums.indexOf("\r\n",next);
                    if (npos==-1)
                        npos=st_ums.length();
                    line.customerref=st_ums.substring(next,npos);
                    next=npos;

                    // check for instref
                    if (next<st_ums.length() && st_ums.substring(next,next+2).equals("//")) {
                        // extract instref
                        next+=2;
                        npos=st_ums.indexOf("\r\n",next);
                        if (npos==-1)
                            npos=st_ums.length();
                        line.instref=st_ums.substring(next,npos);
                        next=npos+2;
                    }
                    if (line.instref==null)
                        line.instref="";

                    // check for additional information
                    if (next<st_ums.length() && st_ums.charAt(next)=='\r') {
                        next+=2;

                        // extract orig Value
                        pos=st_ums.indexOf("/OCMT/",next);
                        if (pos!=-1) {
                            int slashpos=st_ums.indexOf("/",pos+9);
                            if (slashpos==-1)
                                slashpos=st_ums.length();

                            try
                            {
                              line.orig_value=new Value(
                                  st_ums.substring(pos+9,slashpos).replace(',','.'),
                                  st_ums.substring(pos+6,pos+9));
                            }
                            catch (NumberFormatException nfe)
                            {
                              // Der Betrag darf fehlen. Tolerieren wir
                            }
                        }

                        // extract charge Value
                        pos=st_ums.indexOf("/CHGS/",next);
                        if (pos!=-1) {
                            int slashpos=st_ums.indexOf("/",pos+9);
                            if (slashpos==-1)
                                slashpos=st_ums.length();

                            try
                            {
                              line.charge_value=new Value(
                                  st_ums.substring(pos+9,slashpos).replace(',','.'),
                                  st_ums.substring(pos+6,pos+9));
                            }
                            catch (NumberFormatException nfe)
                            {
                              // Der Betrag darf fehlen. Tolerieren wir
                            }
                        }
                    }

                    String st_multi=Swift.getTagValue(st_tag,"86",ums_counter);
                    if (st_multi!=null) {
                        line.gvcode=st_multi.substring(0,3);
                        st_multi=Swift.packMulti(st_multi.substring(3));

                        if (!line.gvcode.equals("999")) {
                            line.isSepa = line.gvcode.startsWith("1");
                            line.text=Swift.getMultiTagValue(st_multi,"00");
                            line.primanota=Swift.getMultiTagValue(st_multi,"10");
                            for (int i=0;i<10;i++) {
                                line.addUsage(Swift.getMultiTagValue(st_multi,Integer.toString(20+i)));
                            }

                            Konto acc=new Konto();
                            acc.blz=Swift.getMultiTagValue(st_multi,"30");
                            acc.number=Swift.getMultiTagValue(st_multi,"31");
                            
                            // fuer den Fall, dass in der BLZ sowas hier drin steht: "GENODEF1S06 SVWZ+ ja"
                            // Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?t=16182
                            if (acc.blz != null)
                            {
                                int space = acc.blz.indexOf(" ");
                                if (space != -1)
                                {
                                    HBCIUtils.log("blz/bic \"" + acc.blz + "\" contains invalid chars, trimming after first space", HBCIUtils.LOG_DEBUG);
                                    acc.blz = acc.blz.substring(0,space);
                                }
                            }

                            if (line.isSepa)
                            {
                              acc.bic = acc.blz;
                              acc.iban = acc.number;
                            }

                            acc.name=Swift.getMultiTagValue(st_multi,"32");
                            acc.name2=Swift.getMultiTagValue(st_multi,"33");
                            if (acc.blz!=null ||
                                    acc.number!=null ||
                                    acc.name!=null ||
                                    acc.name2!=null) {

                                if (acc.blz==null)
                                    acc.blz="";
                                if (acc.number==null)
                                    acc.number="";
                                if (acc.name==null)
                                    acc.name="";
                                line.other=acc;
                            }

                            line.addkey=Swift.getMultiTagValue(st_multi,"34");
                            for (int i=0;i<4;i++) {
                                line.addUsage(Swift.getMultiTagValue(st_multi,Integer.toString(60+i)));
                            }
                        } else {
                            line.additional=st_multi;
                        }
                    }

                    btag.addLine(line);
                    ums_counter++;
                }

                // extract "schlusssaldo"

                String st_end=Swift.getTagValue(st_tag,"62F",0);
                char   endtype='F';
                btag.endtype='F';
                if (st_end==null) {
                    st_end=Swift.getTagValue(st_tag,"62M",0);
                    endtype='M';
                }
                if (st_end!=null) {
                    // Tag 62 (Schlusssaldo) gibt es in MT942 nicht,
                    // darum wird btag.end nur in MT940 gefüllt

                    btag.end=new Saldo();
                    btag.endtype=endtype;

                    String cd=st_end.substring(0,1);

                    try {
                        btag.end.timestamp=dateFormat.parse(st_end.substring(1,7));
                    } catch (Exception e) {
                        btag.end.timestamp=null;
                    }

                    // set default values for optional non-given bdates
                    if (btag.start != null && btag.start.timestamp==null) {
                        btag.start.timestamp=btag.end.timestamp;
                    }
                    for (Iterator<UmsLine> j=btag.lines.iterator(); j.hasNext(); ) {
                        UmsLine line= j.next();
                        if (line.bdate==null) {
                            line.bdate=btag.end.timestamp;
                        }
                    }

                    btag.end.value=new Value(
                            (cd.equals("D")?"-":"")+st_end.substring(10).replaceAll("\\s","").replace(',','.'),
                            st_end.substring(7,10));
                }

                // Now check if the end balance (Schlusssaldo) equals balance of last statement. If not, the bank sent a wrong start balance
                // and we have to re-calculate the balances for each statement
                int numLines = btag.lines.size();
                if(numLines > 0 && btag.end != null) {
                    UmsLine lastLine = btag.lines.get(numLines-1);
                    saldo = btag.end.value.getLongValue();
                    if(lastLine.saldo.value.getLongValue() != saldo) {
                        for(int i=numLines-1; i>=0; i--) {
                            lastLine = btag.lines.get(i);
                            lastLine.saldo.value = new Value(saldo, btag.end.value.getCurr());
                            saldo -= lastLine.value.getLongValue();
                        }
                    }
                }

                tage.add(btag);
                buffer.delete(0,st_tag.length());
            }

            // remove this debugging output
            // HBCIUtils.log("Parsing of MT940 ok until now; unparsed data: "+buffer,HBCIUtils.LOG_DEBUG2);
        } catch (Exception e) {
            HBCIUtils.log("There is unparsed MT94x data - an exception occured while parsing",HBCIUtils.LOG_ERR);
            HBCIUtils.log("current MT94x buffer: "+buffer,HBCIUtils.LOG_DEBUG2);
            throw new HBCI_Exception(e);
        } finally {
            rest.setLength(0);
            rest.append(buffer.toString());
        }
    }
}
