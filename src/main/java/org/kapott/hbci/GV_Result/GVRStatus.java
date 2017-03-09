
/*  $Id: GVRStatus.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.status.HBCIRetVal;

/** <p>Ergebnisse einer Statusprotokoll-Abfrage.
    Ein Statusprotokoll enthält zu allen eingereichten Aufträgen
    den Bearbeitungsstatus. Die einzelnen Aufträge werden durch
    die HBCI-Daten identifiziert, mit denen sie eingereicht wurden
    (Dialog-ID, Nachrichtennummer, Segmentnummer). Um diese Daten
    nicht manuell verwalten zu müssen, werden sie in der sogenannten
    Job-ID (siehe {@link org.kapott.hbci.GV_Result.HBCIJobResultImpl#getJobId()})
    zusammengefasst. </p>
    <p>In dieser Klasse werden die Antwortdaten für eine Statusprotokollabfrage
    gespeichert. Dabei handelt es sich in der Regel um mehr als einen
    Protokolleintrag. Es kann der Protokolleintrag für eine gegebene Job-ID
    extrahiert werden.</p> */
public final class GVRStatus
    extends HBCIJobResultImpl
{
    /** Daten für einen einzelnen Eintrag im Statusprotokoll. Ein Eintrag enthält
        Informationen zu genau einem eingereichten Auftrag */
    public static class Entry
    {
        /** Dialog-ID, mit der der Auftrag eingereicht wurde */
        public String     dialogid;
        /** Nachrichtennummer innerhalb des Dialoges, in dem der Auftrag eingereicht wurde */
        public String     msgnum;
        /** Zeitpunkt der Einreichung */
        public Date       timestamp;
        /** Status (ein HBCI-Returncode) des Auftrages */
        public HBCIRetVal retval;
        
        /** Gibt die Job-ID des Jobs zurück, zu dem dieser Statusprotokolleintrag gehört. 
            @return Job-ID */
        public String getJobId()
        {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
            return format.format(timestamp)+"/"+dialogid+"/"+msgnum+"/"+retval.segref;
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            
            ret.append(HBCIUtils.datetime2StringLocal(timestamp));
            ret.append(" ");
            ret.append(dialogid);
            ret.append("/");
            ret.append(msgnum);
            ret.append("/");
            ret.append(retval.toString());
            
            return ret.toString();
        }
    }
    
    private List<Entry> entries;
    
    public GVRStatus()
    {
        entries=new ArrayList<Entry>();
    }
    
    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (Iterator<Entry> i=entries.iterator();i.hasNext();) {
            Entry e=i.next();
            ret.append(e.toString());
            ret.append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
    
    /** Gibt alle Einträge des Statusprotokolls in einem Array zurück.
        @return Array mit Statusprotokolleinträgen */
    public Entry[] getStatusData()
    {
        return entries.toArray(new Entry[entries.size()]);
    }
    
    /** Gibt den Protokoll-Eintrag zu einem bestimmten Job zurück.
        Liefert <code>null</code>, wenn der Eintrag für die angegebene Job-ID
        nicht im Statusprotokoll vorhanden ist. 
        @param jobId die Job-ID, für die Informationen zurückgegeben werden sollen
        @return Eintrag im Statusprotokoll, der zu dem entsprechenden Auftrag gehört;
                <code>null</code>, wenn kein solcher Auftrag gefunden wurde */
    public Entry getJobEntry(String jobId)
    {
        Entry ret=null;
        
        for (Iterator<Entry> i=entries.iterator();i.hasNext();) {
            Entry entry=i.next();
            
            if (entry.getJobId().equals(jobId)) {
                ret=entry;
                break;
            }
        }
        
        return ret;
    }
}
