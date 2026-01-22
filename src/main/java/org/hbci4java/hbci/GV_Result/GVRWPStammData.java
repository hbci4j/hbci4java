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

package org.hbci4java.hbci.GV_Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hbci4java.hbci.structures.Value;
import org.hbci4java.hbci.structures.WPRef;

/** Diese Klasse kann noch nicht benutzt werden, fuer die
    Abfrage von WP-Stammdaten muss noch der Lowlevel-Job
    WPStammList verwendet werden. */
public class GVRWPStammData 
    extends HBCIJobResultImpl
{
    public final static class Entry
    {
        /** Einheit: Stück */
        public static final int EINHEIT_STCK=1;
        /** Einheit: Prozent */
        public static final int EINHEIT_PRCT=2;
        /** Einheit: Promille */
        public static final int EINHEIT_PRML=3;
        /** Einheit: Punkte */
        public static final int EINHEIT_PNKT=4;
        /** Einheit: Sonstiges */
        public static final int EINHEIT_ELSE=9; 
        
        /** Region: beliebig */
        public static final int REGION_BOTH=0;
        /** Region: nur Inland */
        public static final int REGION_ONLYHOME=1;
        /** Region: nur Ausland */
        public static final int REGION_ONLYFORGN=2;
        
        /** Orderart: kann nur gekauft werden */
        public static final int ORDER_ONLYBUY=1;
        /** Orderart: kann nur verkauft werden */
        public static final int ORDER_ONLYSELL=2;
        /** Orderart: kann sowohl ge- als auch verkauft werden */
        public static final int ORDER_BOTH=3;
        /** Orderart: kann nur über XETRA gehandelt werden */
        public static final int ORDER_ONLYXETRA=4;

        /** Wertpapierreferenz. */
        WPRef wpref;
        /** Wertpapierbezeichnung (kurzer Name) (optional) */
        String shortname;
        /** Wertpapierbezeichnung (langer Name) (optional) */
        String longname;
        /** Bezeichnung der Wertpapiergattung, unter der das Papier
            beim Kreditinstitut geführt wird (optional) */
        String gattung;
        /** Region der Gültigkeit des Wertpapiers (optional):
            <ul>
              <li>{@link #REGION_BOTH}</li>
              <li>{@link #REGION_ONLYHOME}</li>
              <li>{@link #REGION_ONLYFORGN}</li>
              <li>-1 wenn nicht gesetzt</li>
            </ul> */
        int region;
        /** Börsencode der Heimatbörse (optional) */
        String homemarket;
        /** Depotwährung (ISO-Währungscode) (optional) */
        String depotcurr;
        /** Nominalzinssatz (optional) */
        double zinssatz;
        /** Einheit der Effektennotiz (optional). Mögliche Werte sind
            <ul>
              <li>{@link #EINHEIT_STCK}</li>
              <li>{@link #EINHEIT_PRCT}</li>
              <li>{@link #EINHEIT_PRML}</li>
              <li>{@link #EINHEIT_PNKT}</li>
              <li>{@link #EINHEIT_ELSE}</li>
            </ul> */
        int effekteinheit;
        /** Mögliche Orderarten (0 wenn nicht belegt) (optional).
            <ul>
              <li>{@link #ORDER_ONLYBUY}</li>
              <li>{@link #ORDER_ONLYSELL}</li>
              <li>{@link #ORDER_BOTH}</li>
              <li>{@link #ORDER_ONLYXETRA}</li>
            </ul> */
        int canorder;
        /** Nennwert (optional) */
        Value nennwert;
        /** Emissionsdatum (optional) */
        Date date_of_emission;
    }
    
    private List<Entry[]> entries;
    
    public GVRWPStammData()
    {
        entries=new ArrayList<Entry[]>();
    }
    
    public Entry[] getEntries()
    {
        return entries.toArray(new Entry[entries.size()]);
    }
}
