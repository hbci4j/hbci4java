
/*  $Id: GVRTANList.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;

/** Diese Klasse enthält Informationen über aktuelle TAN-Listen des Kunden. Dabei wird
    für jede TAN-Liste ein separates Objekt erzeugt. Innerhalb einer TAN-Listen-Informationen
    gibt es zu jeder verbrauchten TAN genauere Daten. */
public class GVRTANList 
    extends HBCIJobResultImpl
{
    /** Daten zu genau einer TAN */
    public static class TANInfo
    {
        /** Feld zum "Übersetzen" der Verwendungs-Codes ({@link #usagecode}) in einen Klartext. */
        public static final String[] usageCodes={
                                                "TAN ist frei",
                                                "(not used)",
                                                "PIN-Änderung",
                                                "Kontosperre aufheben",
                                                "Aktivieren neuer TAN-Liste",
                                                "Entwertete TAN (maschinell)",
                                                "Mitteilung mit TAN",
                                                "Überweisung",
                                                "Wertpapierorder",
                                                "Dauerauftrag",
                                                "Entwertet wegen Zeitüberschreitung im Zweischritt-Verfahren",
                                                "Entwertet wegen Zeitüberschreitung im Zweischritt-Verfahren (Mehrfach-Signaturen)",
                                                "Entwertet (falsche Antwort auf Challenge?)",
                                                "", "", "", "", "", "", "",
                                                "Lastschriften",
                                                "Euro-Überweisung",
                                                "Auslandsüberweisung",
                                                "Terminüberweisung",
                                                "Umbuchung"
                                                };
        
        /** Code, wofür die TAN verwendet wurde, siehe auch {@link #usageCodes}). Gültige Codes sind:
            <ul>
              <li>0 - TAN wurde noch nicht verbraucht</li>
              <li>1 - Stornierung einer Überweisung</li>
              <li>2 - PIN-Änderung</li>
              <li>3 - Aufheben der Kontosperre</li>
              <li>4 - Aktivieren einer neuen TAN-Liste</li>
              <li>5 - TAN wurde maschinell entwertet</li>
              <li>6 - Mitteilungsversand</li>
              <li>7 - Überweisung/Lastschrift</li>
              <li>8 - Wertpapierverwaltung</li>
              <li>9 - Dauerauftragsverwaltung</li>
              <li>... (siehe Sourcecode)</li>
            </ul> */
        public int    usagecode;
        
        /** Erläuterung für TAN-Verwendung. Wenn {@link #usagecode}==<code>99</code>
         * ist, dann enthält dieser String optional einen erläuternden Text, wofür
         * die TAN verbraucht wurde. */
        public String usagetxt;
        
        /** Die TAN selbst. Ist nur dann gesetzt, wenn die TAN tatsächlich bereits verbraucht
            wurde, sonst ist dieses Feld <code>null</code>*/
        public String tan;
        
        /** Zeitpunkt, wann die TAN verbraucht wurde. Diese Variable ist nur dann ungleich
            <code>null</code>, wenn die TAN tatsächlich bereits verbraucht wurde */
        public Date   timestamp;
        
        public String toString()
        {
            String usage=usagecode<usageCodes.length?usageCodes[usagecode]:"(unknown)";
            return "TAN:"+tan+" Verwendung:"+usagecode+" ("+usage+") ("+usagetxt+")"+
                   (timestamp!=null?(" Zeitpunkt:"+HBCIUtils.datetime2StringLocal(timestamp)):"");
        }
    }
    
    /** Informationen zu genau einer TAN-Liste. */
    public static class TANList
    {
        /** Typ der TAN-Liste. Gültige Codes sind:
            <ul>
              <li>A - aktive Liste</li>
              <li>N - Noch nicht freigeschaltete Liste</li>
              <li>S - Sperre der Liste</li>
              <li>V - vorherige Liste</li>
            </ul> */
        public  char      status;
        /** Listennummer */
        public  String    number;
        /** Erstellungsdatum der Liste, kann <code>null</code> sein. */
        public  Date      date;
        
        /** Anzahl TANs pro Liste */
        public  int       nofTANsPerList;
        
        /** Anzahl verbrauchter TANs pro Liste */
        public  int       nofUsedTANsPerList;

        private List<TANInfo> taninfos;
        
        public TANList()
        {
            taninfos=new ArrayList<TANInfo>();
        }
    
        /** Gibt ein Feld mit Daten zu den einzelnen TANs dieser Liste zurück. 
            @return Array mit TAN-Informationen */
        public TANInfo[] getTANInfos()
        {
            return taninfos.toArray(new TANInfo[taninfos.size()]);
        }
        
        public void addTANInfo(TANInfo info)
        {
            taninfos.add(info);
        }
    
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
    
            ret.append("TANListe Nummer ").append(number).append(" Typ:").append(status+linesep);
            ret.append("nofTANsPerList: ").append(nofTANsPerList).append("; nofUsedTANsPerList: ").append(nofUsedTANsPerList+linesep);
            for (Iterator<TANInfo> i=taninfos.iterator();i.hasNext();) {
                ret.append("  ").append(i.next().toString()).append(linesep);
            }
    
            return ret.toString().trim();
        }
    }
    
    private List<TANList> tanlists;
    
    public GVRTANList()
    {
        tanlists=new ArrayList<TANList>();
    }
    
    public void addTANList(TANList list)
    {
        tanlists.add(list);
    }
    
    /** Gibt ein Array mit Informationen über jede verfügbare TAN-Liste zurück.
        @return Array mit TAN-Listen-Informationen */
    public TANList[] getTANLists()
    {
        return tanlists.toArray(new TANList[tanlists.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (Iterator<TANList> i=tanlists.iterator();i.hasNext();) {
            ret.append(i.next().toString()).append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
}
