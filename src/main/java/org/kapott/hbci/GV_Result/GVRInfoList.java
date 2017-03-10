
/*  $Id: GVRInfoList.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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

import org.kapott.hbci.manager.HBCIUtils;

/** Rückgabedaten für die Abfrage verfügbarer Kreditinstitutsinformationen. Es
    wird eine Liste mit allen verfügbaren Informationen gespeichert, wobei jeder
    Eintrag Daten über genau eine KI-Info enthält. */
public final class GVRInfoList
    extends HBCIJobResultImpl
{
    /** Daten über eine einzelne verfügbare Information */ 
    public static final class Info
    {
        /** Identifikationscode, mit der diese Information referenziert werden kann */
        public String code;
        /** Beschreibung des Inhalts dieser Information */
        public String description;
        /** Art der Information.
            <ul>
              <li>F für Freitextmeldung (siehe {@link org.kapott.hbci.GV_Result.GVRInfoOrder})</li>
              <li>S es handelt sich um ein Schriftdokument, welches bestellt werden kann</li>
              <li>T für Themenüberschrift</li>
            </ul> */
        public String type; // TODO: das als int-konstante machen
        
        public String format;
        /** Version dieser Information (optional) */
        public Date   date;
        /** Kommentare zu dieser Information. Dieses Array ist niemals <code>null</code>,
            kann aber die Länge <code>0</code> haben. */
        public String[] comment;

        public Info()
        {
            comment=new String[0];
        }
        
        public void addComment(String st)
        {
            ArrayList<String> a=new ArrayList<String>(Arrays.asList(comment));
            a.add(st);
            comment=a.toArray(comment);
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            if (type.equals("F"))
                ret.append("Freitextmeldung");
            else if (type.equals("D"))
                ret.append("Datei");
            else if (type.equals("S"))
                ret.append("Schriftdokument");
            else if (type.equals("T"))
                ret.append("Themenueberschrift");
            
            if (format!=null)
                ret.append("(").append(format).append(")");
            
            ret.append(" ").append(code).append(" ").append(description);
            if (date!=null) 
                ret.append(" ").append(HBCIUtils.date2StringLocal(date));
            ret.append(linesep);
            
            for (int i=0;i<comment.length;i++) {
                ret.append(comment[i]+linesep);
            }
            
            return ret.toString().trim();
        }
    }

    private List<Info> entries;

    public GVRInfoList()
    {
        entries=new ArrayList<Info>();
    }

    public void addEntry(Info entry)
    {
        entries.add(entry);
    }
    
    /** Gibt Daten über alle abfragbaren Kreditinstitutsinformationen zurück
        @return Array, wobei jeder Eintrag eine solche Information beschreibt */
    public Info[] getEntries()
    {
        return entries.toArray(new Info[entries.size()]);
    }

    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");

        for (int i=0;i<entries.size();i++) {
            Info entry= entries.get(i);
            
            ret.append("Info #").append(i).append(linesep);
            ret.append(entry.toString());
            ret.append(linesep+linesep);
        }
        
        return ret.toString().trim();
    }
}
