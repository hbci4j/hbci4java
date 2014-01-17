
/*  $Id: GVRAccInfo.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Klasse für die Ergebnisdaten einer Kontostammdaten-Abfrage */
public class GVRAccInfo 
    extends HBCIJobResultImpl
{
    private static final BigDecimal ONE_THOUSAND = new BigDecimal("1000.00");
    
    /** Informationen zu genau einem Konto */
    public static class AccInfo
    {
        // TODO: doku fehlt
        public static class Address 
        {
            public String name1;
            public String name2;
            public String street_pf;
            public String plz_ort;
            public String plz;
            public String ort;
            public String country;
            public String tel;
            public String fax;
            public String email;
            
            public String toString()
            {
                StringBuffer ret=new StringBuffer();
                String       linesep=System.getProperty("line.separator");
                
                ret.append(name1);
                if (name2!=null) {
                    ret.append(" "+name2);
                }
                ret.append(linesep);
                
                ret.append(street_pf).append(linesep);
                
                if (plz_ort!=null) {
                    ret.append(plz_ort);
                } else {
                    ret.append(plz).append(" ").append(ort);
                }
                ret.append(linesep);
                
                if (country!=null) {
                    ret.append(country).append(linesep);
                }
                if (tel!=null) {
                    ret.append("Tel: ").append(tel).append(linesep);
                }
                if (fax!=null) {
                    ret.append("Fax: ").append(fax).append(linesep);
                }
                if (email!=null) {
                    ret.append("Email: ").append(email).append(linesep);
                }
                
                return ret.toString();
            }
        }
        
        public static final int DELIVER_TYPE_NONE=0;
        public static final int DELIVER_TYPE_POST=1;
        public static final int DELIVER_TYPE_KAD=2;
        public static final int DELIVER_TYPE_OFFICE=3;
        public static final int DELIVER_TYPE_EDV=4;
        
        public static final int TURNUS_DAILY=1;
        public static final int TURNUS_WEEKLY=2;
        public static final int TURNUS_MONTHLY=3;
        public static final int TURNUS_QUARTER=4;
        public static final int TURNUS_HALF=5;
        public static final int TURNUS_ANNUAL=6;
        
        /** Konto, auf das sich diese Daten beziehen */
        public Konto   account;
        /** Kontoart - Folgende Wertebereiche sind definiert:.
            <ul>
              <li>1-9 - Kontokorrent-/Girokonto</li>
              <li>10-19 - Sparkonto</li>
              <li>20-29 - Festgeldkonto (Termineinlagen) </li>
              <li>30-39 - Wertpapierdepot</li>
              <li>40-49 - Kredit-/Darlehenskonto</li>
              <li>50-59 - Kreditkartenkonto</li>
              <li>60-69 - Fondsdepot bei einer Kapitalanlagegesellschaft</li>
              <li>70-79 - Bausparvertrag</li>
              <li>80-89 - Versicherungsvertrag</li>
              <li>90-99 - Sonstige</li>
            </ul>
            <code>-1</code>, wenn diese Information nicht von der Bank 
            bereitgestellt wird*/
        public int     type;
        /** Eröffnungsdatum (optional) */
        public Date    created;
        /** 1000*Sollzins (optional) */
        public long  sollzins;
        /** 1000*Habenzins (optional) */
        public long habenzins;
        /** 1000*Überziehungszins (optional) */
        public long ueberzins;
        /** Kreditlinie (optional) */
        public Value   kredit;
        /** Referenzkonto (zB für Kreditkartenkonten) (optional) */
        public Konto   refAccount;
        /** Versandart für Kontoauszüge. Folgende Werte sind definiert:
            <ul>
              <li><code>DELIVER_TYPE_NONE</code> - kein Auszug</li>
              <li><code>DELIVER_TYPE_POST</code> - Postzustellung</li>
              <li><code>DELIVER_TYPE_KAD</code> - Kontoauszugsdrucker</li>
              <li><code>DELIVER_TYPE_OFFICE</code> - Abholung in Geschäftsstelle</li>
              <li><code>DELIVER_TYPE_EDV</code> - elektronische Übermittlung</li>
            </ul>*/
        public int     versandart;
        /** Turnus für Kontoauszugszustellung (nur bei Postzustellung) (optional).
         * Folgende Werte sind definiert:
         * <ul>
         *   <li><code>TURNUS_DAILY</code> - täglicher Kontoauszug</li>
         *   <li><code>TURNUS_WEEKLY</code> - wöchentlicher Kontoauszug</li>
         *   <li><code>TURNUS_MONTHLY</code> - monatlicher Kontoauszug</li>
         *   <li><code>TURNUS_QUARTER</code> - vierteljährlicher Kontoauszug</li>
         *   <li><code>TURNUS_HALF</code> - halbjährlicher Kontoauszug</li>
         *   <li><code>TURNUS_ANNUAL</code> - jährlicher Kontoauszug</li>
         * </ul> */
        public int     turnus;
        /** Weitere Informationen (optional) */
        public String  comment;

        // TODO: doku fehlt
        public Address address;

        // TODO public Berechtigter[] berechtigte; 
        // /* TODO Briefanschrift (optional) */
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            ret.append("Konto ").append(account.toString());
            ret.append(" (art: ").append(type).append(")").append(linesep);
            
            if (created!=null)
                ret.append("Eröffnungsdatum: ").append(HBCIUtils.date2StringLocal(created)).append(linesep);
            ret.append("Sollzins:").append(HBCIUtilsInternal.bigDecimal2String(new BigDecimal(sollzins).divide(ONE_THOUSAND)));
            ret.append(" Habenzins:").append(HBCIUtilsInternal.bigDecimal2String(new BigDecimal(habenzins).divide(ONE_THOUSAND)));
            ret.append(" Überziehungszins:").append(HBCIUtilsInternal.bigDecimal2String(new BigDecimal(ueberzins).divide(ONE_THOUSAND)));
            ret.append(" Kredit: ").append(kredit).append(linesep);
            
            if (refAccount!=null)
                ret.append("Referenzkonto: ").append(refAccount.toString()).append(linesep);
            ret.append("Kontoauszug Versandart:").append(versandart);
            ret.append(" Turnus:").append(turnus).append(linesep);
            if (comment!=null)
                ret.append("Bemerkungen: ").append(comment).append(linesep);
                
            if (address!=null) {
            	ret.append("Anschrift").append(linesep);
            	ret.append(address.toString()).append(linesep);
            }
            // TODO: Berechtigte
            
            return ret.toString().trim();
        }
    }
    
    private List<AccInfo> entries;
    
    public GVRAccInfo()
    {
        entries=new ArrayList<AccInfo>();
    }
    
    public void addEntry(AccInfo info)
    {
        entries.add(info);
    }

    /** Holen aller empfangenen Kontostammdaten.
        @return Array mit einzelnen Konto-Informationen. Das Array ist niemals
        <code>null</code>, kann aber die Länge <code>0</code> haben */
    public AccInfo[] getEntries()
    {
        return entries.toArray(new AccInfo[entries.size()]);
    }

    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");

        int num=0;
        for (Iterator<AccInfo> i=entries.iterator();i.hasNext();) {
            num++;
            ret.append("Kontoinfo #").append(num).append(linesep);
            ret.append((i.next()).toString()+linesep);
        }

        return ret.toString().trim();
    }
}
