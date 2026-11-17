/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.kapott.hbci.GV_Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Ergebnisse der Abfrage noch anstehender terminierter SEPA-Einzellastschriften. Jeder
    noch anstehende Lastschriftauftrag wird in einem separaten Objekt gespeichert. */
public final class GVRTermLastSEPAList
    extends HBCIJobResultImpl
{
    /** Informationen zu einer einzelnen terminierten SEPA-Einzellastschrift */
    public static class Entry
    {
        /** Gutschriftkonto (Kundenkonto) */
        public Konto    my;
        /** Konto des Zahlungspflichtigen */
        public Konto    other;
        /** Einzuziehender Betrag */
        public Value    value;
        /** Verwendungszweckzeilen. Dieses Array ist niemals <code>null</code>,
            kann aber die Länge <code>0</code> haben. */
        public String[] usage;
        /** Fälligkeitsdatum */
        public Date     date;
        /** Auftrags-Identifikationsnummer (optional) */
        public String   orderid;
        /** SEPA-Descriptor der pain-Nachricht. Bei Änderung und Löschung muss
            dasselbe Schema verwendet werden (Job-Parameter <code>_sepadescriptor</code>). */
        public String   sepadescr;
        /** Typ der Lastschrift (CORE oder COR1) */
        public String   type;
        /** Sequenztyp der Lastschrift (FRST, RCUR, OOFF oder FNAL) */
        public String   sequencetype;
        /** Gläubiger-Identifikationsnummer */
        public String   creditorid;
        /** Mandatsreferenz */
        public String   mandateid;
        /** Datum der Unterschrift des Mandats */
        public Date     manddateofsig;
        /** End-to-End-ID (optional) */
        public String   endtoendid;
        /** Payment-Information-ID (optional) */
        public String   pmtinfid;

        /** Auftrag kann geändert werden (optional) */
        public boolean can_change;

        /** Auftrag kann gelöscht werden (optional, nur in Segmentversion 1) */
        public boolean can_delete;

        /** SEPA-C-Code (optional, nur in Segmentversion 2).
            <ul>
              <li>1 - Reversal</li>
              <li>2 - Revocation</li>
              <li>3 - Delete</li>
            </ul> */
        public String   ccode;

        /** Status des SEPA-Auftrags (optional, nur in Segmentversion 2).
            <ul>
              <li>1 - in Terminierung</li>
              <li>2 - abgelehnt von erster Inkassostelle</li>
              <li>3 - in Bearbeitung</li>
              <li>4 - Creditoren-seitig verarbeitet, Buchung veranlasst</li>
              <li>5 - R-Transaktion wurde veranlasst</li>
            </ul> */
        public String   orderstatus;

        public Entry()
        {
            usage=new String[0];
        }

        public void addUsage(String st)
        {
            ArrayList<String> l=new ArrayList<String>(Arrays.asList(usage));
            l.add(st);
            usage=l.toArray(usage);
        }

        public String toString()
        {
            StringBuffer     ret=new StringBuffer();
            String           linesep=System.getProperty("line.separator");

            ret.append(HBCIUtils.date2StringLocal(date));
            ret.append(": ");
            ret.append("KTO {");
            ret.append(my.toString());
            ret.append("}");
            ret.append(" <-- ");
            ret.append("KTO {");
            ret.append(other.toString());
            ret.append("}");
            ret.append(": ");
            ret.append(value.toString());
            ret.append(linesep);

            ret.append("ID: ");
            ret.append(orderid);
            ret.append(linesep);

            ret.append(type);
            ret.append("/");
            ret.append(sequencetype);
            ret.append(linesep);

            for (int i=0;i<usage.length;i++) {
                ret.append(usage[i]);
                ret.append(linesep);
            }

            return ret.toString().trim();
        }
    }

    private List<Entry> list;

    public GVRTermLastSEPAList()
    {
        list=new ArrayList<Entry>();
    }

    public void addEntry(Entry e)
    {
        list.add(e);
    }

    /** Gibt ein Array mit gefundenen noch anstehenden terminierten SEPA-Einzellastschriften zurück
        @return Array, wobei jedes Element Daten über eine einzelne terminierte Lastschrift enthält */
    public Entry[] getEntries()
    {
        return list.toArray(new Entry[list.size()]);
    }

    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");

        for (int i=0;i<list.size();i++) {
            Entry e=list.get(i);

            ret.append("#").append(i);
            ret.append(linesep);
            ret.append(e.toString());
            ret.append(linesep);
            ret.append(linesep);
        }

        return ret.toString().trim();
    }
}
