
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.structures.BigDecimalValue;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.TypedValue;

/** Ergebnisdaten für die Abfrage von Depotumsätzen
    Diese Klasse enthält für jedes Depot ein separates
    Datenobjekt. Innerhalb eines Depots werden für jede
    in diesem Depot vorhandene Wertpapiergattung separate
    Datenobjekte gehalten. Für jede Wertpapiergattung wiederum
    gibt es u.U. mehrere Objekte, die Umsatzinformationen
    enthalten. */
public final class GVRWPDepotUms
extends HBCIJobResultImpl
{
    /** Ein Eintrag zu genau einem Depot */
    public static final class Entry
    {
        /** Zeitpunkt der Erstellung dieser Daten */
        public  Date      timestamp;
        /** Depotkonto, auf das sich der Eintrag bezieht. */
        public  Konto     depot;
        /** Liste der Wertpapiere mit Umsätzen */
        public final List<FinancialInstrument> instruments = new ArrayList<FinancialInstrument>();
        
        public static class FinancialInstrument {
            /** ISIN des Wertpapiers (optional) */
            public String     isin;
            /** WKN des Wertpapiers (optional) */
            public String     wkn;
            /** Wertpapierbezeichnung */
            public String     name;

            /** Startsaldo des Finanzinstruments **/
            public TypedValue startSaldo;
            /** Endsaldo des Finanzinstruments **/
            public TypedValue endSaldo;

            /** Endsaldo des Finanzinstruments **/
            public TypedValue preis;
            /** Preisdatum */
            public Date preisdatum;
            
            /** Liste der Transaktionen/Umsätze **/
            public final List<Transaction> transactions = new ArrayList<Transaction>();
            
            public static class Transaction {
                /** The transaction relates to settlement and clearing **/
                public static final int INDICATOR_SETTLEMENT_CLEARING = 1;
                /** The transaction relates to corporate action. **/
                public static final int INDICATOR_CORPORATE_ACTION = 2;
                @Deprecated
                public static final int INDICATOR_KAPITALMASSNAHME = INDICATOR_CORPORATE_ACTION;
                /** Transaktion im Zusammenhang mit Leihe **/
                public static final int INDICATOR_LEIHE = 3;
                /** Transaktion im Zusammenhang mit Sicherheiten **/
                public static final int INDICATOR_SICHERHEITEN = 4;
                
                /** The transaction is a delivery. **/
                public static final int RICHTUNG_LIEFERUNG = 1;
                /** The transaction is a receipt. **/
                public static final int RICHTUNG_ERHALT = 2;
                
                /** The transaction is versus payment. **/
                public static final int BEZAHLUNG_GEGEN_ZAHLUNG = 1;
                /** The transaction is free of payment. **/
                public static final int BEZAHLUNG_FREI = 2;
                
                /** Kundenreferenznummer **/
                public String kundenreferenz;
                /** Anzahl/Betrag der Papiere im Umsatz **/
                public TypedValue anzahl;
                /** Betrag des Umsatzes **/
                public BigDecimalValue betrag;
                /** Betrag der Stueckzinsen **/
                public BigDecimalValue stueckzinsen;
                /** Anzahl der aufgelaufenen Tage für Stückzinsen */
                public int stueckzins_tage;
                
                /** Art der Transaktion (siehe Konstanten INDICATOR*) */
                public int transaction_indicator;
                /** Lieferung oder Erhalt? (siehe Konstanten RICHTUNG*) */
                public int richtung;
                /** Gegen Bezahlung oder frei? (siehe Konstanten BEZAHLUNG*) */
                public int bezahlung;
                
                /** The transaction was initiated by CCP.A. */ 
                public boolean ccp_eligibility;
                /** Effektives Datum der Transaktion */
                public Date datum;
                /** Valutadatum der Transaktion */
                public Date datum_valuta;
                /** Transaktion ist ein Storno einer anderen Transaktion */
                public boolean storno;
                
                /** Gegenpartei der Transaktion */
                public String gegenpartei;
                
                /** Freitext mit Transaktionsdetails */
                public String freitext_details;
                
                @Override
                public String toString() {
                    StringBuilder rv = new StringBuilder();
                    String sep = System.getProperty("line.separator");
                    rv.append("kundenreferenz: ").append(kundenreferenz).append(sep);
                    rv.append("anzahl: ").append(anzahl).append(sep);
                    rv.append("betrag: ").append(betrag).append(sep);
                    rv.append("stueckzinsen: ").append(stueckzinsen).append(sep);
                    rv.append("stueckzins_tage: ").append(stueckzins_tage).append(sep);
                    rv.append("transaction_indicator: ").append(transaction_indicator).append(": ");
                    switch (transaction_indicator) {
                    case (INDICATOR_CORPORATE_ACTION):
                        rv.append("Corporate Action");
                        break;
                    case (INDICATOR_SETTLEMENT_CLEARING):
                        rv.append("Settlement/Clearing");
                        break;
                    case (INDICATOR_LEIHE):
                        rv.append("Leihe");
                        break;
                    case (INDICATOR_SICHERHEITEN):
                        rv.append("Sicherheiten");
                        break;
                    default:
                        rv.append("Unbekannt");
                        break;
                    }
                    rv.append(sep);
                    
                    rv.append("richtung: ").append(richtung).append(": ");
                    switch (richtung) {
                    case (RICHTUNG_ERHALT):
                        rv.append("Erhalt");
                        break;
                    case (RICHTUNG_LIEFERUNG):
                        rv.append("Lieferung");
                        break;
                    default:
                        rv.append("Unbekannt");
                        break;
                    }
                    rv.append(sep);
                    
                    rv.append("bezahlung: ").append(bezahlung).append(": ");
                    switch (bezahlung) {
                    case (BEZAHLUNG_FREI):
                        rv.append("frei");
                        break;
                    case (BEZAHLUNG_GEGEN_ZAHLUNG):
                        rv.append("gegen Zahlung");
                        break;
                    default:
                        rv.append("Unbekannt");
                        break;
                    }
                    rv.append(sep);
                    
                    rv.append("ccp_eligibility: ").append(ccp_eligibility).append(sep);
                    rv.append("datum: ").append(datum).append(sep);
                    rv.append("datum_valuta: ").append(datum_valuta).append(sep);
                    rv.append("storno: ").append(storno).append(sep);
                    rv.append("gegenpartei: ").append(gegenpartei).append(sep);
                    rv.append("freitext_details: ").append(freitext_details).append(sep);
                    
                    return rv.toString();
                }
            }
            
            @Override
            public String toString() {
                StringBuilder rv = new StringBuilder();
                String sep = System.getProperty("line.separator");
                rv.append("isin: ").append(isin).append(sep);
                rv.append("wkn: ").append(wkn).append(sep);
                rv.append("name: ").append(name).append(sep);
                rv.append("startSaldo: ").append(startSaldo).append(sep);
                rv.append("endSaldo: ").append(endSaldo).append(sep);
                rv.append("preis: ").append(preis).append(sep);
                rv.append("preisdatum: ").append(preisdatum).append(sep);
                
                for (int i=0; i<transactions.size(); i++) {
                    rv.append("--> Transaction ").append(i).append(":").append(sep);
                    rv.append(transactions.get(i));
                    rv.append("<--").append(sep);
                }
                
                return rv.toString();
            }
        }
        @Override
        public String toString() {
            StringBuilder rv = new StringBuilder();
            String sep = System.getProperty("line.separator");
            rv.append("timestamp: ").append(this.timestamp).append(sep);
            rv.append("depot: ").append(depot).append(sep);
            
            for (int i=0; i<instruments.size(); i++) {
                rv.append("====> Instrument ").append(i).append(":").append(sep);
                rv.append(instruments.get(i));
                rv.append("<====").append(sep);
            }
            
            return rv.toString();
        }
    }

    private List<Entry> entries;
    /** Dieses Feld enthält einen String, der den nicht-auswertbaren Teil der gelieferten Informationen
        enthält. Es dient nur zu Debugging-Zwecken und sollte eigentlich immer <code>null</code>
        bzw. einen leeren String enthalten. Wenn das nicht der Fall ist, dann konnten die 
        empfangenen Daten nicht richtig geparst werden, und dieser String enthält den
        "Schwanz" der Daten, bei dem das Parsing-Problem aufgetreten ist.*/
    public String rest;

    public GVRWPDepotUms()
    {
        entries=new ArrayList<Entry>();
    }

    public void addEntry(Entry ums)
    {
        entries.add(ums);
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
