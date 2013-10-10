
/*  $Id: GVRCardList.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import org.kapott.hbci.structures.Value;

/** Klasse mit den Ergebissen der Abfrage von Informationen zu 
    ausgegebenen Karten. Für jede Karte, für die Informationen
    verfügbar sind, wird eine separates Informationsobjekt
    {@link org.kapott.hbci.GV_Result.GVRCardList.CardInfo}
    erzeugt. */
public class GVRCardList 
    extends HBCIJobResultImpl
{
    /** Informationen über genau eine Karte */
    public static class CardInfo
    {
        /** Kartenart aus den BPD */
        public int    cardtype;
        /** Kartennummer */
        public String cardnumber;
        /** Kartenfolgenummer (optional) */
        public String cardordernumber;
        /** Name des Karteninhabers (optional) */
        public String owner;
        /** Karte gültig von (optional) */
        public Date   validFrom;
        /** Karte gültig bis (optional) */
        public Date   validUntil;
        /** Kartenlimit (optional) */
        public Value  limit;
        /** Bemerkungen (optional) */
        public String comment;
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            ret.append("Karte ").append(cardnumber);
            ret.append(" (typ ").append(cardtype);
            ret.append(", Folgenummer ").append(cardordernumber).append("): ");
            ret.append(owner).append(linesep);
            ret.append("Gültig von ").append((validFrom!=null?HBCIUtils.date2StringLocal(validFrom):"unknown"));
            ret.append(" bis ").append((validUntil!=null?HBCIUtils.date2StringLocal(validUntil):"unknown")).append(linesep);
            
            if (limit!=null)
                ret.append("Kartenlimit: ").append(limit).append(linesep);
            if (comment!=null)
                ret.append("Bemerkungen: ").append(comment).append(linesep);
            
            return ret.toString().trim();
        }
    }
    
    private List<CardInfo> entries;
    
    public GVRCardList()
    {
        entries=new ArrayList<CardInfo>();
    }
    
    public void addEntry(CardInfo info)
    {
        entries.add(info);
    }
    
    /** Gibt eine Liste aller empfangenen Karteninformations-Einträge zurück.
        @return Array mit Karteninformationsdaten. Das Array selbst ist niemals
        <code>null</code>, kann aber die Länge <code>0</code> haben */
    public CardInfo[] getEntries()
    {
        return entries.toArray(new CardInfo[entries.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
        
        int num=0;
        for (Iterator<CardInfo> i=entries.iterator();i.hasNext();) {
            num++;
            ret.append("Karteninfo #").append(num).append(linesep);
            ret.append(i.next().toString()+linesep);
        }
        
        return ret.toString().trim();
    }
}
