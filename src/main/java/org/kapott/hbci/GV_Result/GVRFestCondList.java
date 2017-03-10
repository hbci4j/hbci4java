
/*  $Id: GVRFestCondList.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.structures.Value;

/** Rückgabedaten für die Abfrage von Festgeld-Konditionen. Es wird eine Liste
    mit empfangenen Konditionen bereitgestellt. Für jeden Datensatz 
    wird ein separates Objekt mit den entsprechenden Informationen bereitgestellt. */
public final class GVRFestCondList
    extends HBCIJobResultImpl
{
    /** Informationen über eine mögliche Kondition für Festgeldanlagen */
    public static final class Cond
    {
        /** Zinsmethode - Berechnung mit Monat je 30 und Jahr je 360 Tage */   
        public static final int METHOD_30_360=0;
        /** Zinsmethode - Berechnung mit Monat je 28 bzw 31 und Jahr je 360 Tage */   
        public static final int METHOD_2831_360=1;
        /** Zinsmethode - Berechnung mit Monat je 28 bzw 31 und Jahr je 365 bzw 366 Tage */   
        public static final int METHOD_2831_365366=2;
        /** Zinsmethode - Berechnung mit Monat je 30 und Jahr je 365 bzw 366 Tage */   
        public static final int METHOD_30_365366=3;
        /** Zinsmethode - Berechnung mit Monat je 28 bzw 31 und Jahr je 365 Tage */   
        public static final int METHOD_2831_365=4;
        /** Zinsmethode - Berechnung mit Monat je 30 und Jahr je 365 Tage */   
        public static final int METHOD_30_365=5;
        
        /** Beginn der Laufzeit der Anlage */
        public Date   anlagedatum;
        /** Ende der Laufzeit der Anlage */
        public Date   ablaufdatum;
        /** Zinssatz für die Anlage mal 1000 */
        public long   zinssatz;
        /** Zinsmethode zur Berechnung des Zinsbetrages */
        public int    zinsmethode;
        /** Mindestanlagebetrag */
        public Value  minbetrag;
        /** Höchstanlagebetrag (optional) */
        public Value  maxbetrag;
        /** Identifikations-String für diese Kondition (optional) */
        public String id;
        /** Beschreibender Name für diese Kondition (optional) */
        public String name;
        /** Versionsnummer dieser Kondition (optional) */
        public String version;
        /** Datum, wann diese Konditionsinformationen bereitgestellt wurden (optional) */
        public Date   date;
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            ret.append("Konditionen '").append(name).append("'");
            if (version!=null)
                ret.append(" V").append(version).append(" ").append(HBCIUtils.datetime2StringLocal(date));
            ret.append(linesep);
            
            ret.append("  Zeitraum: ");
            ret.append(HBCIUtils.date2StringLocal(anlagedatum));
            ret.append(" bis ");
            ret.append(HBCIUtils.date2StringLocal(ablaufdatum));
            ret.append(linesep);
            
            ret.append("  Betragsintervall: ");
            ret.append(minbetrag.toString());
            ret.append(" bis ");
            if (maxbetrag!=null)
                ret.append(maxbetrag.toString());
            else
                ret.append("(unbegrenzt)");
            ret.append(linesep);
            
            ret.append("  Zinssatz: ");
            ret.append(HBCIUtilsInternal.bigDecimal2String(new BigDecimal(zinssatz).divide(new BigDecimal("1000.0"))));
            ret.append("% (");
            switch (zinsmethode) {
                case METHOD_2831_360:    ret.append("28/31 Tage bzw. 360 Tage"); break;
                case METHOD_2831_365:    ret.append("28/31 Tage bzw. 365 Tage"); break;
                case METHOD_2831_365366: ret.append("28/31 Tage bzw. 365/366 Tage"); break;
                case METHOD_30_360:      ret.append("30 Tage bzw. 360 Tage"); break;
                case METHOD_30_365:      ret.append("30 Tage bzw. 365 Tage"); break;
                case METHOD_30_365366:   ret.append("30 Tage bzw. 365/366 Tage"); break;
                default:                 ret.append("(unknown)");
            }
            ret.append(")");

            return ret.toString().trim();
        }
    }
    
    private List<Cond> entries;
    
    public GVRFestCondList()
    {
        entries=new ArrayList<Cond>();
    }
    
    public void addEntry(Cond entry)
    {
        entries.add(entry);
    }
    
    /** Gibt alle gefundenen Festgeldkonditionen als Array zurück
        @return Array mit Festgeldkonditionen */
    public Cond[] getEntries()
    {
        return entries.toArray(new Cond[entries.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
        
        for (int i=0;i<entries.size();i++) {
            Cond entry= entries.get(i);
            
            ret.append("#").append(i).append(linesep);
            ret.append(entry.toString());
            ret.append(linesep).append(linesep);
        }

        return ret.toString().trim();
    }
}
