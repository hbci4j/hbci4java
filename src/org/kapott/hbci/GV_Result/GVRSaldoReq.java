
/*  $Id: GVRSaldoReq.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;

/** Ergebnisse einer Saldenabfrage. Hier ist für jedes abgefragte Konto
    genau ein entsprechendes Saldo-Objekt eingetragen. */
public final class GVRSaldoReq
    extends HBCIJobResultImpl
{
    /** Saldo-Informationen für ein Konto */
    public static final class Info
        implements Serializable
    {
        /** Saldo für welches Konto */
        public Konto konto;
        /** Gebuchter Saldo */
        public Saldo ready;
        /** Saldo noch nicht verbuchter Umsätze (optional)*/
        public Saldo unready;
        /** Kreditlinie (optional) */
        public Value kredit;
        /** Aktuell verfügbarer Betrag (optional) */
        public Value available;
        /** Bereits verfügter Betrag (optional) */
        public Value used;

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String linesep=System.getProperty("line.separator");

            ret.append("Konto: ").append(konto.toString()).append(linesep);
            ret.append("  Gebucht: ").append(ready.toString()).append(linesep);
            
            if (unready!=null)
                ret.append("  Pending: ").append(unready.toString()).append(linesep);
            if (kredit!=null)
                ret.append("  Kredit: ").append(kredit.toString()).append(linesep);
            if (available!=null)
                ret.append("  Verfügbar: ").append(available.toString()).append(linesep);
            if (used!=null)
                ret.append("  Benutzt: ").append(used.toString());

            return ret.toString().trim();
        }
    }

    private List<Info> saldi;

    public GVRSaldoReq()
    {
        saldi=new ArrayList<Info>();
    }
    
    public void store(GVRSaldoReq.Info info)
    {
        saldi.add(info);
    }
    
    /** Gibt alle verfügbaren Saldo-Informationen in einem Feld zurück.
        Dabei existiert für jedes abgefragte Konto ein Eintrag in diesem Feld.
        @return Array mit Saldeninformationen */
    public Info[] getEntries()
    {
        return saldi.toArray(new Info[saldi.size()]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (int i=0;i<saldi.size();i++) {
            GVRSaldoReq.Info info= saldi.get(i);
            ret.append(info.toString()).append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
}
