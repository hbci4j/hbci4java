
/*  $Id: GVRTermUebList.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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
import java.util.List;

import org.kapott.hbci.GV_Result.GVRTermUebList.Entry;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Ergebnisse der Abfrage noch anstehender terminierter Überweisungen. Jeder
    noch anstehende terminierte Überweisungsauftrag wird in einem separaten Objekt gespeichert. */
public final class GVRTermUebList 
    extends HBCIJobResultImpl
{
    /** Informationen zu einem einzelnen terminierten Überweisungsauftrag */
    public static class Entry
    {
        /** Belastungskonto (Kundenkonto) */
        public Konto    my;
        /** Empfängerkonto */
        public Konto    other;
        /** Zu überweisender Betrag */
        public Value    value;
        /** Typ der Überweisung (bankintern) */
        public String   key;
        /** weitere Informationen zum Typ der Überweisung (bankintern, optional) */
        public String   addkey;
        /** Verwendungszweckzeilen. Dieses Array ist niemals <code>null</code>,
            kann aber die Länge <code>0</code> haben. */
        public String[] usage;
        /** Datum der geplanten Ausführung */
        public Date     date;
        /** Auftrags-Identifikationsnummer (optional) */
        public String   orderid;
        
        /** Auftrag kann geändert werden (optional) */
        public boolean can_change;
        
        /** Auftrag kann gelöscht werden (optional) */
        public boolean can_delete;
        
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
            ret.append(" --> ");
            ret.append("KTO {");
            ret.append(other.toString());
            ret.append("}");
            ret.append(": ");
            ret.append(value.toString());
            ret.append(linesep);
            
            ret.append("ID: ");
            ret.append(orderid);
            ret.append(linesep);
            
            ret.append(key);
            ret.append("/");
            ret.append(addkey);
            ret.append(linesep);
            
            for (int i=0;i<usage.length;i++) {
                ret.append(usage[i]);
                ret.append(linesep);
            }
            
            return ret.toString().trim();
        }
    }
    
    private List<Entry> list;
    
    public GVRTermUebList()
    {
        list=new ArrayList<Entry>();
    }
    
    public void addEntry(Entry e)
    {
        list.add(e);
    }
    
    /** Gibt ein Array mit gefundenen noch anstehenden Terminüberweisungen zurück
        @return Array, wobei jedes Element Daten über eine einzelne Terminüberweisung enthält */
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
