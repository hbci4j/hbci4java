
/*  $Id: GVRDauerList.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Ergebnisse der Abfrage von bestehenden Daueraufträgen. In diesem Objekt
    wird eine Liste von Dauerauftragsdaten gespeichert. Jedes dieser Dauerauftragsdaten-Objekte
    beschreibt dabei einen bestehenden Dauerauftrag. */ 
public final class GVRDauerList
    extends HBCIJobResultImpl
{
    /** Informationen zu einem einzelnen Dauerauftrag. */
    public static final class Dauer
    {
        /** Belastungskonto (Kundenkonto) */
        public Konto    my;
        /** Empfängerkonto */
        public Konto    other;
        /** Zu überweisender Betrag */
        public Value    value;
        /** Transaktionsschlüssel (bankintern) */
        public String   key;
        /** Zusätzlicher Transaktionsschlüssel (bankintern, optional) */
        public String   addkey;
        /** Verwendungszweckzeilen. Dieses Array ist niemals <code>null</code>,
            kann aber die Länge <code>0</code> haben. */
        public String[] usage;
        /** Datum der nächsten Ausführung (optional) */
        public Date     nextdate;
        /** Eindeutige Auftragsnummer, um diesen Dauerauftrag zu identifizieren (optional) */
        public String   orderid;

        /** Datum der ersten Ausführung */
        public Date     firstdate;
        /** Zeiteinheit der Wiederholung.
            <ul>
              <li>M - monatlich</li>
              <li>W - wöchentlich</li>
            </ul> */
        public String   timeunit;
        /** Wiederholen aller wieviel Zeiteinheiten */
        public int      turnus;
        /** Tag der Ausführun innerhalb der Zeineinheit
            <ul>
              <li>bei Zeiteinheit=W: 1-7 für Wochentag</li>
              <li>bei Zeiteinheit=M: 1-31 für Tag des Monats</li>
            </ul> */
        public int      execday;
        /** Datum, wann der Dauerauftrag zum letzten Mal ausgeführt werden soll (optional) */
        public Date     lastdate;

        /** Sind Daten zu einer geplanten Aussetzung vorhanden? */
        public boolean aus_available;
        /** Aussetzung jährlich wiederholen? (Nur gültig, wenn <code>aus_available</code> <code>true</code> ist) */
        public boolean  aus_annual;
        /** Tag der ersten Aussetzung (Nur gültig, wenn <code>aus_available</code> <code>true</code> ist) (optional) */
        public Date     aus_start;
        /** Tag der letzten Aussetzung (Nur gültig, wenn <code>aus_available</code> <code>true</code> ist) (optional) */
        public Date     aus_end;
        /** Anzahl der Aussetzungen (Nur gültig, wenn <code>aus_available</code> <code>true</code> ist) (optional) */
        public String   aus_breakcount;
        /** Geänderter Betrag während Aussetzung (Nur gültig, wenn <code>aus_available</code> <code>true</code> ist) (optional) */
        public Value    aus_newvalue;
        
        /** Auftrag kann geändert werden (optional) */
        public boolean can_change;
        
        /** Auftrag kann ausgesetzt werden (optional) */
        public boolean can_skip;
        
        /** Auftrag kann gelöscht werden (optional) */
        public boolean can_delete;
        
        /**
         * SEPA Payment-Information-ID.
         */
        public String pmtinfid;
        
        /**
         * SEPA Purpose-Code.
         */
        public String purposecode;
        

        public Dauer()
        {
            usage=new String[0];
        }

        public void addUsage(String line)
        {
            ArrayList<String> a=new ArrayList<String>(Arrays.asList(usage));
            a.add(line);
            usage=(a.toArray(usage));
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String linesep=System.getProperty("line.separator");
            
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("SRCACCOUNT")).append(": ").append(my.toString()).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("DSTACCOUNT")).append(": ").append(other.toString()).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("VALUE")).append(": ").append(value.toString()).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("KEY")).append(": ").append(key).append("/").append(addkey).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("USAGE")).append(":").append(linesep);
            for (int i=0;i<usage.length;i++) {
                ret.append("    ").append(usage[i]).append(linesep);
            }
            if (nextdate!=null)
                ret.append("  ").append(HBCIUtilsInternal.getLocMsg("NEXTEXECDATE")).append(": ").append(HBCIUtils.date2StringLocal(nextdate)).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("ORDERID")).append(": ").append(orderid).append(linesep);

            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("FIRSTLASTEXEC")).append(": ").append(HBCIUtils.date2StringLocal(firstdate)).append(" / ");
            ret.append((lastdate!=null?HBCIUtils.date2StringLocal(lastdate):"N/A")).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("EXECDAY")).append(": ").append(execday).append(linesep);
            ret.append("  ").append(HBCIUtilsInternal.getLocMsg("UNITTURNUS")).append(": ").append(timeunit).append(" / ").append(turnus).append(linesep);

            ret.append("  (Aussetzung not yet implemented)");

            return ret.toString().trim();
        }
    }

    private List<Dauer> entries;

    public GVRDauerList()
    {
        entries=new ArrayList<Dauer>();
    }

    public void addEntry(Dauer entry)
    {
        entries.add(entry);
    }
    
    /** Gibt ein Array mit Daten zu allen gefundenen Dauerauftragsdaten zurück
        @return Array mit Dauerauftrags-Informationen */
    public Dauer[] getEntries()
    {
        return entries.toArray(new Dauer[entries.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();

        for (Iterator<Dauer> i=entries.iterator();i.hasNext();) {
            ret.append(HBCIUtilsInternal.getLocMsg("STANDINGORDER")).append(" #").append(i).append(System.getProperty("line.separator"));
            ret.append(i.next()).append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
}
