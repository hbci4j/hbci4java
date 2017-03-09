
/*  $Id: GVRWPDepotList.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.structures.BigDecimalValue;
import org.kapott.hbci.structures.Konto;

/** Ergebnisdaten für die Abfrage einer Depotaufstellung.
    Diese Klasse enthält für jedes Depot ein separates
    Datenobjekt. Innerhalb eines Depots werden für jede
    in diesem Depot vorhandene Wertpapiergattung separate
    Datenobjekte gehalten. Für jede Wertpapiergattung wiederum
    gibt es u.U. mehrere Objekte, die Saldeninformationen
    enthalten. */
public final class GVRWPDepotList 
    extends HBCIJobResultImpl
{
    /** Ein Eintrag zu genau einem Depot */
    public static final class Entry
    {
        public static final int SALDO_TYPE_STCK=1;
        public static final int SALDO_TYPE_WERT=2;

        /** Enhält Informationen zu einer Wertpapiergattung */
        public static final class Gattung
        {
            /** Untersaldoinformationen, das heißt Informationen über die Zusammensetzung
                des Saldos einer Wertpapiergattung. */
            public static final class SubSaldo
            {
                /** Beschreibung des Saldowertes */
                public String  qualifier;
                /** Gibt den Typ des Saldos {@link #saldo} an (optional).
                    <ul>
                      <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry.Gattung#PRICE_TYPE_PRCT} - Saldo ist ein Prozentsatz</li>
                      <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry.Gattung#PRICE_TYPE_VALUE} - Saldo ist ein Geldbetrag</li>
                    </ul> */
                public int     saldo_type;
                /** Gibt an, ob das Papier für einen Verkauf zur Verfügung steht.
                    <code>true</code> gibt an, dass das Papier gesperrt ist und somit
                    nicht zur Verfügung steht, bei <code>false</code> kann es verkauft werden */
                public boolean locked;
                /** Saldobetrag. Das Währungsfeld <code>curr</code> ist hier zur Zeit
                    immer der leere String. */
                public BigDecimalValue   saldo;
                /** Lagerland der Depotstelle (optional). Der Ländercode ist
                    der ISO-3166-Ländercode (z.B. DE für Deutschland) */
                public String  country;
                /** Art der Verwahrung (optional).
                    <ul>
                      <li>0 - nicht gesetzt</li>
                      <li>1 - Girosammelverwahrung</li>
                      <li>2 - Streifbandverwahrung</li>
                      <li>3 - Haussammelverwahrung</li>
                      <li>4 - Wertpapierrechnung</li>
                      <li>9 - Sonstige</li>
                    </ul>*/
                public int     verwahrung;
                /** Beschreibung der Lagerstelle (optional) */
                public String  lager;
                /** Sperre bis zu diesem Datum (optional) */
                public Date    lockeduntil;
                /** Sperr- oder Zusatzvermerke der Bank (optional) */
                public String  comment;
                
                public String toString()
                {
                    StringBuffer ret=new StringBuffer();
                    String       linesep=System.getProperty("line.separator");
                    
                    ret.append(qualifier).append(": ");
                    ret.append(saldo.toString()).append(" (").append(((saldo_type==SALDO_TYPE_STCK)?"STCK":"WERT")).append(")").append(linesep);
                    
                    ret.append("Lagerland: ").append(country+linesep);
                    ret.append("Verwahrung Typ: ").append(verwahrung).append(linesep);
                    ret.append("Lager: ").append(lager).append(linesep);
                    
                    if (locked) {
                        ret.append("locked");
                        if (lockeduntil!=null) {
                            DateFormat df=DateFormat.getDateTimeInstance(
                                    DateFormat.SHORT, 
                                    DateFormat.SHORT, 
                                    HBCIUtils.getLocale());
                            ret.append(" until ").append(df.format(lockeduntil));
                        }
                    } else {
                        ret.append("not locked");
                    }
                    
                    if (comment!=null)
                        ret.append(linesep).append("Bemerkungen: ").append(comment);
                    
                    return ret.toString().trim();
                }
            }
            
            public final static int PRICE_TYPE_PRCT=1;
            public final static int PRICE_TYPE_VALUE=2;
            public final static int PRICE_QUALIF_MRKT=1;
            public final static int PRICE_QUALIF_HINT=2;
            public final static int SOURCE_LOC=1;
            public final static int SOURCE_THEOR=2;
            public final static int SOURCE_SELLER=3;
            
            /** ISIN des Wertpapiers (optional) */
            public String     isin;
            /** WKN des Wertpapiers (optional) */
            public String     wkn;
            /** Wertpapierbezeichnung */
            public String     name;
            /** Gibt den Typ des Preises {@link #price} an (optional).
                <ul>
                  <li>{@link #PRICE_TYPE_PRCT} - Preis ist ein Prozentsatz</li>
                  <li>{@link #PRICE_TYPE_VALUE} - Preis ist ein Geldbetrag</li>
                </ul> */
            public int        pricetype;
            /** Hinweise zum Preis {@link #price} (optional).
                <ul>
                  <li>{@link #PRICE_QUALIF_MRKT} - Marktpreis (z.B. aktueller Börsenkurs)</li>
                  <li>{@link #PRICE_QUALIF_HINT} - Hinweispreis (rechnerischer bzw. ermittelter Preis)</li>
               </ul>*/
            public int        pricequalifier;
            /** Preis pro Einheit (optional). Die Währung ist bei
                {@link #pricetype}={@link #PRICE_TYPE_PRCT} auf "%" gesetzt. */
            public BigDecimalValue      price;
            /** Herkunft von Preis/Kurs (optional).
                <ul>
                  <li>{@link #SOURCE_LOC} - lokale Börse</li>
                  <li>{@link #SOURCE_THEOR} - theoretischer Wert</li>
                  <li>{@link #SOURCE_SELLER} - Verkäufer als Quelle</li>
                </ul> */
            public int        source;
            /** Bemerkungen zur Herkunft von Preis/Kurs {@link #source} (optional).
                Bei {@link #source}={@link #SOURCE_LOC} kann der Name der Börse
                als MIC angegeben werden */
            public String     source_comment;
            /** Zeitpunkt, wann {@link #price} notiert wurde (optional). */
            public Date       timestamp_price;
            /** Typ des Gesamtsaldos.
                <ul>
                  <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry#SALDO_TYPE_STCK} - Saldo ist eine Stückzahl</li> 
                  <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry#SALDO_TYPE_WERT} - Saldo ist ein Betrag</li>
                </ul>*/ 
            public int        saldo_type;
            /** Gesamtsaldo dieser Gattung. Das Währungsfeld ist in jedem
                Fall ein leerer String! (TODO). */
            public BigDecimalValue      saldo;
            private ArrayList<SubSaldo> saldi;
            /** Anzahl der aufgelaufenen Tage (optional) */
            public int        days;
            /** Kurswert zum Gesamtsaldo {@link #saldo} (optional) */
            public BigDecimalValue      depotwert;
            /** Betrag der   Stückzinsen (optional) */
            public BigDecimalValue      stueckzinsbetrag;
            // TODO: dafuer muessen depotwert2 und stueckzinsbetrag2 eingefuehrt werden
            public String     xchg_cur1;
            public String     xchg_cur2;
            public double     xchg_kurs;
            /** Depotwährung (optional) */
            public String     curr;
            /** Wertpapierart gemäß WM GD 195 (optional) */
            public String     wptype;
            /** Branchenschlüssel gemäß WM GD 200 (optional) */
            public String     branche;
            /** Land des Emittenten (Country-Code wie in Kontodaten) (optional) */
            public String     countryEmittent;
            /** Kaufdatum (optional) */
            public Date       kauf;
            /** Fälligkeitsdatum (optional) */
            public Date       faellig;
            /** Einstandspreis/-kurs (optional). Die Währung ist "%", 
                wenn es sich um eine Prozentabgabe handelt */
            public BigDecimalValue      einstandspreis;
            /** Zinssatz als Prozentangabe bei verzinslichen Papieren (optional) */
            public long       zinssatz;
            // TODO: das ist noch nicht gemacht
            public int        kontrakttype; 
            public Date       kontraktverfall;
            public int        kontraktversion;
            public int        kontraktsize;
            public String     symbol;
            public String     underlyingwkn;
            public String     underlyingisin;
            public BigDecimalValue      kontraktbasispreis;
            
            public Gattung()
            {
                saldi=new ArrayList<SubSaldo>();
            }
            
            public void addSubSaldo(SubSaldo subsaldo)
            {
                saldi.add(subsaldo);
            }
            
            /** Gibt alle Unter-Saldoinformationen in einem Array zurück.
                Der Rückgabewert ist niemals <code>null</code>, das Array kann aber
                die Länge <code>0</code> haben.
                @return Array mit Untersaldoinformationen */
            public SubSaldo[] getEntries()
            {
                return saldi.toArray(new SubSaldo[saldi.size()]);
            }
            
            public String toString()
            {
                StringBuffer ret=new StringBuffer();
                String       linesep=System.getProperty("line.separator");
                
                ret.append("Gattung ").append(name).append("(ISIN:");
                ret.append(isin).append(" WKN:").append(wkn);
                ret.append(" CURR:").append(curr).append(")").append(linesep);
                if (price!=null) {
                    ret.append("Preis: ").append(price.toString()).append(" (").append((pricetype==PRICE_TYPE_PRCT?"Prozent":"Betrag"));
                    ret.append("; ").append((pricequalifier==PRICE_QUALIF_MRKT?"Marktpreis":"Hinweispreis")).append(")").append(linesep);
                }
                
                if (source!=0) {
                    ret.append("Quelle: ");
                    switch (source) {
                        case SOURCE_LOC:
                            ret.append("lokale Boerse");
                            break;
                        case SOURCE_THEOR:
                            ret.append("theoretischer Wert");
                            break;
                        case SOURCE_SELLER:
                            ret.append("Verkaeufer");
                            break;
                        default:
                            ret.append("(unknown)");
                    }
                    if (source_comment!=null)
                        ret.append(" (").append(source_comment).append(")");
                    ret.append(linesep);
                }
                
                if (timestamp_price!=null) {
                    DateFormat df=DateFormat.getDateTimeInstance(
                            DateFormat.SHORT,
                            DateFormat.SHORT,
                            HBCIUtils.getLocale());
                    ret.append("Zeitpunkt: ").append(df.format(timestamp_price)).append(linesep);
                }
                
                if (depotwert!=null)
                    ret.append("Depotwert: ").append(depotwert.toString()).append(linesep);
                if (stueckzinsbetrag!=null)
                    ret.append("Stueckzins: ").append(stueckzinsbetrag.toString()).append(linesep);
                if (einstandspreis!=null)
                    ret.append("Einstandspreis: ").append(einstandspreis.toString()).append(linesep);
                if (zinssatz!=0)
                    ret.append("Zinssatz: ").append(HBCIUtilsInternal.bigDecimal2String(new BigDecimal(zinssatz).divide(new BigDecimal("1000.0")))).append(linesep);
                ret.append("Typ:").append(wptype).append(" Branche:").append(branche).append(" Emittent:").append(countryEmittent).append(linesep);
                if (kauf!=null)
                    ret.append("Kauf: ").append(HBCIUtils.date2StringLocal(kauf)).append(linesep);
                if (faellig!=null)
                    ret.append("Faelligkeit: ").append(HBCIUtils.date2StringLocal(faellig)).append(linesep);
                if (days!=0)
                    ret.append("Anzahl abgelaufener Tage: ").append(days).append(linesep);
                ret.append("Saldo: ").append(saldo.toString());
                ret.append(" (").append(((saldo_type==SALDO_TYPE_STCK)?"STCK":"WERT"));
                ret.append(")").append(linesep);
                
                for (int i=0;i<saldi.size();i++) {
                    ret.append("SubSaldo:").append(linesep);
                    ret.append(saldi.get(i).toString());
                    if (i<saldi.size()-1) {
                        ret.append(linesep+linesep);
                    }
                }
                
                return ret.toString().trim();
            }
        }
        
        /** Zeitpunkt der Erstellung dieser Daten */
        public  Date      timestamp;
        /** Depotkonto, auf das sich der Eintrag bezieht. */
        public  Konto     depot;
        private ArrayList<Gattung> gattungen;
        /** Gesamtwert des Depots (optional!) */
        public  BigDecimalValue     total;
        
        /* TODO: Zusatzinformationen aus Feld 72 */
        
        public Entry()
        {
            gattungen=new ArrayList<Gattung>();
        }
        
        public void addEntry(Gattung gattung)
        {
            gattungen.add(gattung);
        }
        
        /** Gibt ein Array mit Informationen über alle Wertpapiergattungen
            zurück, die in diesem Depot gehalten werden. Der Rückgabewert ist
            niemals <code>null</code>, die Größe des Arrays kann aber 0 sein.
            @return Array mit Informationen über Wertpapiergattungen */
        public Gattung[] getEntries()
        {
            return gattungen.toArray(new Gattung[gattungen.size()]);
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            DateFormat df=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, HBCIUtils.getLocale());
            ret.append("Depot ").append(depot.toString()).append(" ").append(df.format(timestamp)).append(linesep);
            for (int i=0;i<gattungen.size();i++) {
                ret.append("Gattung:").append(linesep);
                ret.append(gattungen.get(i).toString()+linesep+linesep);
            }
            if (total!=null)
                ret.append("Total: ").append(total.toString());
            
            return ret.toString().trim();
        }
    }
    
    private List<Entry> entries;
    /** Dieses Feld enthält einen String, der den nicht-auswertbaren Teil der gelieferten Informationen
        enthält. Es dient nur zu Debugging-Zwecken und sollte eigentlich immer <code>null</code>
        bzw. einen leeren String enthalten. Wenn das nicht der Fall ist, dann konnten die 
        empfangenen Daten nicht richtig geparst werden, und dieser String enthält den
        "Schwanz" der Daten, bei dem das Parsing-Problem aufgetreten ist.*/
    public String rest;
    
    public GVRWPDepotList()
    {
        entries=new ArrayList<Entry>();
    }
    
    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }
    
    /** Gibt ein Array mit Depotdaten zurück, wobei jeder Eintrag
        Informationen zu genau einem Depot enthält.
        @return Array mit Depotinformationen */
    public Entry[] getEntries()
    {
        return entries.toArray(new Entry[entries.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
            
        for (int i=0;i<entries.size();i++) {
            Entry e=entries.get(i);
            ret.append("Entry #").append(i).append(":").append(linesep);
            ret.append(e.toString()+linesep+linesep);
        }
        
        ret.append("rest: ").append(rest);
            
        return ret.toString().trim();
    }
}
